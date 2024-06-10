CREATE TABLE articles
(
    id        VARCHAR(255) NOT NULL,
    title     VARCHAR(255),
    slug      VARCHAR(255),
    content   VARCHAR(255),
    author_id VARCHAR(255),
    CONSTRAINT pk_articles PRIMARY KEY (id)
);

CREATE TABLE users
(
    id         VARCHAR(255) NOT NULL,
    email      VARCHAR(255),
    password   VARCHAR(255),
    role       VARCHAR(255),
    refresh_id VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE articles
    ADD CONSTRAINT uc_articles_slug UNIQUE (slug);

ALTER TABLE articles
    ADD CONSTRAINT uc_articles_title UNIQUE (title);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE articles
    ADD CONSTRAINT FK_ARTICLES_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);