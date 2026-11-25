# Forum - Technical Overview

## 1. Overview

The Forum is a Spring Boot web application built around
two topics, consumer PC building and servers/homelabs.
Users can create posts, comment on them, like them and
tag them. It ships two ways to use it: a server-rendered
Thymeleaf frontend, and a REST API documented with
Swagger for other developers.

Structurally the project is split into `controller`
(HTTP layer, split again into `web` for Thymeleaf pages
and `rest` for the JSON API), `service` (business logic,
interface + implementation), `repository` (Spring Data
JPA interfaces), and `model` (JPA entities), with a
separate `dto` package for what actually crosses the
API boundary. On top of that there's `security` (Spring
Security wiring), `exception` (custom exceptions +
global handler for the API), and `config` (OpenAPI and
static resource configuration).

Both controller layers call the same services, so the
rules (ownership checks, blocked users, tag
normalization) live in one place regardless of whether
a request came from a form or from the API.

Stack: Java 17, Spring Boot 3 (Web, Security,
Validation, Data JPA), Hibernate, Thymeleaf, MariaDB for
persistence with Flyway-managed migrations, and
springdoc-openapi for Swagger documentation.

## 2. Database

The schema is managed entirely by Flyway, with
Hibernate set to `ddl-auto: validate` so it only checks
that the tables match the entities and never changes
them itself.

- `V1__init_schema.sql` creates the tables.
- `V2__seed_data.sql` and `V3__more_seed_content.sql`
fill them with users, posts, comments, likes and tags so
the forum isn't empty on first run.

Tables:

| Table | Purpose |
|---|---|
| `users` | Users and admins, with `role` (`USER`/`ADMIN`), `blocked` flag and optional phone number / profile photo |
| `posts` | Belongs to a user, title + content |
| `comments` | Belongs to a post and a user |
| `post_likes` | Join table, composite key `(post_id, user_id)` so a user can like a post only once |
| `tags` | Unique, lowercase tag names |
| `post_tags` | Many-to-many between posts and tags |

The length rules from the requirements (names 4-32,
title 16-64, content 32-8192) are enforced twice: once
with Bean Validation on the DTOs so the user gets a
proper error message, and once with `CHECK` constraints
in the database as a last line of defense. Email and
username uniqueness are enforced the same way.

All foreign keys use `ON DELETE CASCADE`, so deleting a
user or post cleans up its comments, likes and tag
links without the service layer having to do it
manually.

The connection URL has `createDatabaseIfNotExist=true`,
so the only thing needed is a running MariaDB instance.

## 3. Security / Authorization

Authentication is plain session-based form login
through Spring Security (`SecurityConfig`). Passwords
are hashed with BCrypt, and users are loaded through
`CustomUserDetailsService`, which wraps the `User`
entity in `ForumUserDetails` so controllers can get the
current user's id and role directly.

Access is split into three levels:

1. **Public** - home page, login, register, static
files, uploaded photos, `/api/auth/**`, `/api/public/**`
and Swagger. The home page shows the user and post
counts and the top 10 most recent / most commented
posts.

2. **Authenticated** - everything else. Browsing and
reading posts, creating posts, commenting, liking,
editing your profile.

3. **Admin** - `/admin/**` and `/api/admin/**`, for
searching users, blocking/unblocking them, promoting
them to admin and deleting any post or tag.

The REST API uses the same session as the web pages, so
to try protected endpoints in Swagger you log in at
`/login` first in the same browser. CSRF protection is on
for the web forms and disabled for `/api/**`.

Ownership is checked in the service layer, not in the
controllers. Every update/delete method takes the
requester's id and whether they're an admin, and throws
`ForbiddenActionException` if a regular user tries to
modify something that isn't theirs. Blocked users are
checked the same way: `PostServiceImpl` and
`CommentServiceImpl` refuse to create posts or comments
for them.

## 4. Features

**Posts.** The post list supports searching by text,
filtering by tag or by category, and pagination/sorting
through Spring's `Pageable`. A single post page shows
the content, comments, likes and tags, along with the
edit/delete/comment/like actions the current user is
allowed to use.

**Categories.** The two forum topics are defined in the
`ForumCategory` enum, each mapped to a set of tags.
Filtering by category simply means filtering by any of
its tags, so there's no separate category table to keep
in sync.

**Tags.** Tags are added to a post after it's created,
from the edit page. `TagServiceImpl.findOrCreate` trims
and lowercases the name and reuses an existing tag if
there is one, so duplicates can't appear. Only the
post's author or an admin can add/remove tags.

**Likes.** Liking is a toggle (`LikeServiceImpl`):
liking a post a second time removes the like. The
composite primary key on `post_likes` guarantees one
like per user per post.

**Profile photos.** Uploads go through
`FileStorageServiceImpl`, which only accepts JPEG, PNG,
GIF and WEBP, renames the file to a random UUID and
checks the resolved path stays inside the upload
directory. Files are stored on disk under `uploads/`
(configurable with `UPLOAD_DIR`) and served back at
`/uploads/**` by `WebConfig`.

## 5. REST API

All endpoints are documented in Swagger at
`/swagger-ui.html`.

| Resource | Endpoints |
|---|---|
| Auth | `POST /api/auth/register` |
| Public | `GET /api/public/stats`, `/posts/recent`, `/posts/most-commented` |
| Posts | CRUD on `/api/posts`, `GET /by-tag/{tagName}`, add/remove tags, comments, like |
| Comments | `PUT` / `DELETE /api/comments/{id}` |
| Users | `GET` / `PUT` / `DELETE /api/users/me`, `POST /me/photo`, `GET /{id}`, `GET /{id}/posts` |
| Tags | `GET /api/tags`, `GET /api/tags/{id}` |
| Admin | `/api/admin/users` (search, block, unblock, promote, delete), `DELETE /api/admin/tags/{id}` |

Errors from the API come back in one consistent JSON
shape (`ApiError`) through `GlobalExceptionHandler`:

| Exception | Status |
|---|---|
| `ResourceNotFoundException` | 404 |
| `DuplicateResourceException` | 409 |
| `ForbiddenActionException` | 403 |
| `InvalidFileException` | 400 |
| Validation failure | 400, with per-field details |

The handler is scoped to the `rest` package only, so the
Thymeleaf pages keep using the normal error page instead
of getting JSON back.

## 6. Architectural Decisions & Trade-offs

- **One service layer for two frontends.** The web and
REST controllers are thin and only translate between
HTTP and the services. The downside is some duplicated
mapping code between the two controller sets.

- **Sessions instead of tokens.** Since the main client
is the server-rendered frontend, form login with
sessions was the simplest fit and lets the API reuse the
same login. A token-based setup would suit external API
clients better.

- **Categories as an enum.** Since the forum only covers
two fixed topics, an enum mapped to tags was simpler
than a table and admin screens for managing it. Adding a
category means changing code, which is fine at this
scale.

- **Files on local disk.** Profile photos are stored on
the filesystem instead of in the database or object
storage. That's simple and fine for a single instance,
but won't work if the app is scaled to multiple
instances.

- **Validation in two places.** Bean Validation gives
readable errors, the DB constraints guarantee the data
stays valid even if something bypasses the service
layer.
