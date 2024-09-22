CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    email    varchar UNIQUE not null,
    name     varchar        not null,
    password varchar not null
);