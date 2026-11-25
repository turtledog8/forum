CREATE TABLE users (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name         VARCHAR(32)  NOT NULL,
    last_name          VARCHAR(32)  NOT NULL,
    email              VARCHAR(255) NOT NULL,
    username           VARCHAR(64)  NOT NULL,
    password           VARCHAR(255) NOT NULL,
    phone_number       VARCHAR(32)  NULL,
    profile_photo_url  VARCHAR(512) NULL,
    role               VARCHAR(16)  NOT NULL DEFAULT 'USER',
    blocked            BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT chk_users_role CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT chk_users_first_name_length CHECK (CHAR_LENGTH(first_name) BETWEEN 4 AND 32),
    CONSTRAINT chk_users_last_name_length CHECK (CHAR_LENGTH(last_name) BETWEEN 4 AND 32)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE posts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(64)  NOT NULL,
    content     VARCHAR(8192) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_posts_title_length CHECK (CHAR_LENGTH(title) BETWEEN 16 AND 64),
    CONSTRAINT chk_posts_content_length CHECK (CHAR_LENGTH(content) BETWEEN 32 AND 8192)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_posts_user_id ON posts (user_id);
CREATE INDEX idx_posts_created_at ON posts (created_at);

CREATE TABLE comments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    content     VARCHAR(4096) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_comments_post_id ON comments (post_id);

CREATE TABLE post_likes (
    post_id     BIGINT    NOT NULL,
    user_id     BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (post_id, user_id),
    CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE tags (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(64) NOT NULL,

    CONSTRAINT uk_tags_name UNIQUE (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE post_tags (
    post_id  BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,

    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_tags_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
