DROP TABLE IF EXISTS cart_items, order_items, items, orders, images, users CASCADE;
DROP TYPE IF EXISTS roles CASCADE;

CREATE TABLE IF NOT EXISTS images (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    image_bytes BYTEA
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(100) NOT NULL,
    image_id INT,
    price NUMERIC(10, 2) NOT NULL,
    amount INT,
    CONSTRAINT items_images FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TYPE roles AS ENUM ('USER', 'ADMIN');

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role roles DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_id INT NOT NULL,
    username VARCHAR(30) NOT NULL,
    CONSTRAINT cart_items_items FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT cart_items_users FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(30) NOT NULL,
    total_sum NUMERIC(10, 2),
    CONSTRAINT order_users FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id INT,
    item_id INT NOT NULL,
    item_price NUMERIC(9, 2) NOT NULL,
    item_amount INT NOT NULL,
    order_item_total_sum NUMERIC(10, 2) NOT NULL,
    CONSTRAINT order_items_orders FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT order_items_items FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO users(username, password, role) VALUES
    ('user1', '$2a$12$o9FYCapKCbubG3r5mHtTA.dWWr0xsIJPku3y4NXWywaUpW.DRrIx.', 'USER'), --pass = pass1
    ('user2', '$2a$12$TjUVWFCg/9OOsgVKoCAba.wg.8xGrmGfowL.p8S.U.17DtaNOJfPa', 'USER'), --pass = pass2
    ('1', '$2a$12$tjr4cxHzoJ/pdOXPPyFgGuKlnILawpnKWO/7yKKPkNxNRgUuJ7s1y', 'USER'), --pass = 1
    ('2', '$2a$12$poSSKtrQ7yWlvX2LuTUsL.rIbxHDm6GuyVP8BhUBjGeZ35uYouloa', 'USER'), --pass = 2
    ('admin', '$$2a$12$wC5ziXwxwZyXFERx0QEh7OmwHJUw4HbH1.c/IDEGjmoyMJCFP7dpC', 'ADMIN'); --pass = admin

