DO
$do$
BEGIN
   IF EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'booking_manager') THEN

      RAISE NOTICE 'Role "booking_manager" already exists. Skipping.';
   ELSE
      CREATE ROLE booking_manager LOGIN PASSWORD 'q1w2e3r4';
   END IF;
END
$do$;


CREATE DATABASE booking_manager
            WITH OWNER = booking_manager
            ENCODING = 'UTF8'
            CONNECTION LIMIT = -1
            IS_TEMPLATE = False;

\c booking_manager booking_manager;

DO
$do$
BEGIN
   IF EXISTS (
      SELECT FROM information_schema.schemata
      WHERE  schema_name = 'app') THEN

      RAISE NOTICE 'Schema "app" already exists. Skipping.';
   ELSE
      CREATE SCHEMA app AUTHORIZATION booking_manager;
   END IF;
END
$do$;

CREATE TABLE app.supply
(
    uuid uuid NOT NULL,
    name text NOT NULL,
    price numeric NOT NULL,
    duration integer,
    dt_create timestamp without time zone NOT NULL,
    dt_update timestamp without time zone NOT NULL,
    PRIMARY KEY (uuid),
    UNIQUE (name)
);

ALTER TABLE IF EXISTS app.supply
    OWNER to booking_manager;

CREATE TABLE app.users
(
    uuid uuid NOT NULL,
    name text NOT NULL,
    phone_number text,
    role text NOT NULL,
    dt_create timestamp without time zone NOT NULL,
    dt_update timestamp without time zone NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE IF EXISTS app.users
    OWNER to booking_manager;

CREATE TABLE app.users_supply
(
    user_uuid uuid NOT NULL,
    supply_uuid uuid NOT NULL,
    FOREIGN KEY (user_uuid)
        REFERENCES app.users (uuid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    FOREIGN KEY (supply_uuid)
        REFERENCES app.supply (uuid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

ALTER TABLE IF EXISTS app.users_supply
    OWNER to booking_manager;

CREATE TABLE app.schedule
(
    uuid uuid NOT NULL,
    master_uuid uuid NOT NULL,
    dt_start timestamp without time zone,
    dt_end timestamp without time zone,
    dt_create timestamp without time zone NOT NULL,
    dt_update timestamp without time zone NOT NULL,
    PRIMARY KEY (uuid),
    FOREIGN KEY (master_uuid)
        REFERENCES app.users (uuid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

ALTER TABLE IF EXISTS app.schedule
    OWNER to booking_manager;