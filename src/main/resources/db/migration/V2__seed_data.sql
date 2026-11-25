
INSERT INTO users (first_name, last_name, email, username, password, role, blocked) VALUES
    ('Admin', 'Admin',    'admin@forum.local', 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN', FALSE),
    ('Ivan',  'Petrov',   'ivan@forum.local',  'ivan',  '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'USER',  FALSE),
    ('Elena', 'Todorova', 'elena@forum.local', 'elena', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'USER',  FALSE);

INSERT INTO tags (name) VALUES
    ('gpu'),
    ('cooling'),
    ('budget-build'),
    ('homelab'),
    ('nas');

INSERT INTO posts (user_id, title, content, created_at) VALUES
    (2, 'Best GPU choices for 1440p gaming right now',
        'I have been comparing price-to-performance across the current GPU lineup for 1440p at high refresh rates. Here is what I would actually buy at each budget tier, plus a couple of cards I would avoid right now and why.',
        CURRENT_TIMESTAMP),
    (3, 'Planning cable management before you even buy parts',
        'Cable management is so much easier if you plan it before ordering anything rather than fighting your case after the build is done. Here is how I pick a case and PSU together to keep the routing clean from day one.',
        CURRENT_TIMESTAMP),
    (2, 'Building a quiet home server on a budget',
        'My homelab started as a single old desktop running a NAS and has grown into a small rack. Here is the hardware I would pick today if I were starting over, with an emphasis on keeping noise and power draw low.',
        CURRENT_TIMESTAMP);

INSERT INTO post_tags (post_id, tag_id) VALUES
    (1, 1),
    (1, 3),
    (2, 2),
    (3, 4),
    (3, 5),
    (3, 3);

INSERT INTO comments (post_id, user_id, content, created_at) VALUES
    (1, 3, 'Curious how these picks hold up once the next-gen cards launch.', CURRENT_TIMESTAMP),
    (2, 2, 'Wish I had read this before my last build, thanks for sharing!', CURRENT_TIMESTAMP),
    (3, 3, 'What NAS software are you running on top of this?', CURRENT_TIMESTAMP);

INSERT INTO post_likes (post_id, user_id) VALUES
    (1, 3),
    (2, 2),
    (3, 3);
