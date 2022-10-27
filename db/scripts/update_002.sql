CREATE TABLE candidate (
   id SERIAL PRIMARY KEY,
   "name" TEXT,
   "desc" TEXT,
   created TIMESTAMP,
   city_id INT,
   photo bytea
);