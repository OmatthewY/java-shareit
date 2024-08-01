DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    email VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(150),
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items(item_id),
    FOREIGN KEY (booker_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    text VARCHAR(150) NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items(item_id),
    FOREIGN KEY (author_id) REFERENCES users(user_id)
);