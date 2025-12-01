DROP TABLE IF EXISTS compilation_events;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS compilations;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

ALTER TABLE IF EXISTS categories DROP CONSTRAINT IF EXISTS uq_category_name;
ALTER TABLE IF EXISTS users DROP CONSTRAINT IF EXISTS uq_user_email;
ALTER TABLE IF EXISTS requests DROP CONSTRAINT IF EXISTS uq_request;

CREATE TABLE IF NOT EXISTS users (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(250) NOT NULL,
    email   VARCHAR(254) NOT NULL,
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(50) NOT NULL,
    CONSTRAINT uq_category_name UNIQUE (name)
);


CREATE TABLE IF NOT EXISTS events (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation          VARCHAR(2000)       NOT NULL,
    description         VARCHAR(7000)       NOT NULL,
    category_id         BIGINT              NOT NULL,
    created_on          TIMESTAMP           NOT NULL,
    event_date          TIMESTAMP           NOT NULL,
    initiator_id        BIGINT              NOT NULL,
    loc_lat             DOUBLE PRECISION    NOT NULL,
    loc_lon             DOUBLE PRECISION    NOT NULL,
    paid                BOOLEAN             NOT NULL,
    participant_limit   INT                 NOT NULL,
    published_on        TIMESTAMP,
    request_moderation  BOOLEAN             NOT NULL,
    state               VARCHAR(20)         NOT NULL,
    title               VARCHAR(120)        NOT NULL,

    CONSTRAINT fk_events_categories
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,

    CONSTRAINT fk_events_users
        FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP   NOT NULL,
    event_id     BIGINT      NOT NULL,
    requester_id BIGINT      NOT NULL,
    status       VARCHAR(20) NOT NULL,

    CONSTRAINT fk_requests_events
        FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,

    CONSTRAINT fk_requests_users
        FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE,

    CONSTRAINT uq_request UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned  BOOLEAN       NOT NULL DEFAULT FALSE,
    title   VARCHAR(50)   NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id   BIGINT NOT NULL,
    event_id         BIGINT NOT NULL,

    CONSTRAINT fk_compilation_events_compilations
        FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,

    CONSTRAINT fk_compilation_events_events
        FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,

    CONSTRAINT pk_compilation_events PRIMARY KEY (compilation_id, event_id)
);