CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email varchar,
  password TEXT
);

ALTER TABLE users ADD CONSTRAINT email_unique UNIQUE (email);