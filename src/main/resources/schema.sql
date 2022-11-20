CREATE TABLE IF NOT EXISTS users
(
    ID    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(100)                            NOT NULL,
    email VARCHAR(200)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UNIQUE_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(200)                            NOT NULL,
    description VARCHAR(1000)                           NOT NULL,
    available   BOOLEAN,
    owner_id    BIGINT                                  NOT NULL,
    request_id  BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT UNIQUE_OWNER_ITEM_NAME UNIQUE (owner_id, name)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date_time TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_date_time   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id         BIGINT,
    booker_id       BIGINT,
    status          VARCHAR(20),
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT FK_BOOKING_ON_BOOKER FOREIGN KEY (booker_id) REFERENCES users (id),
    CONSTRAINT FK_BOOKING_ON_ITEM FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(2000),
    item_id   BIGINT,
    author_id BIGINT,
    created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT FK_COMMENT_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT FK_COMMENT_ON_ITEM FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(1000)                           NOT NULL,
    requestor_id BIGINT                                  NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT FK_REQUEST_ON_AUTHOR FOREIGN KEY (requestor_id) REFERENCES users (id)
);



