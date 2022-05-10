DO
$$
    BEGIN
        IF EXISTS
            (SELECT 1 from pg_roles where rolname = 'cloudsqliamuser')
        THEN
            GRANT USAGE ON SCHEMA public TO cloudsqliamuser;
            GRANT USAGE ON SCHEMA klage TO cloudsqliamuser;
            GRANT SELECT ON ALL TABLES IN SCHEMA public TO cloudsqliamuser;
            GRANT SELECT ON ALL TABLES IN SCHEMA klage TO cloudsqliamuser;
            ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO cloudsqliamuser;
            ALTER DEFAULT PRIVILEGES IN SCHEMA klage GRANT SELECT ON TABLES TO cloudsqliamuser;
        END IF;
    END
$$;

CREATE TABLE klage.text
(
    id       UUID PRIMARY KEY,
    title    TEXT      NOT NULL,
    type     TEXT      NOT NULL,
    content  TEXT      NOT NULL,
    created  TIMESTAMP NOT NULL,
    modified TIMESTAMP
);

CREATE TABLE klage.hjemmel
(
    text_id UUID REFERENCES klage.text (id),
    hjemmel TEXT NOT NULL
);

CREATE TABLE klage.ytelse
(
    text_id UUID REFERENCES klage.text (id),
    ytelse  TEXT NOT NULL
);

CREATE TABLE klage.utfall
(
    text_id UUID REFERENCES klage.text (id),
    utfall  TEXT NOT NULL
);

CREATE TABLE klage.enhet
(
    text_id UUID REFERENCES klage.text (id),
    enhet   TEXT NOT NULL
);

CREATE INDEX text_hjemmel_ix ON klage.hjemmel (text_id);
CREATE INDEX text_ytelse_ix ON klage.ytelse (text_id);
CREATE INDEX text_utfall_ix ON klage.utfall (text_id);
CREATE INDEX text_enhet_ix ON klage.enhet (text_id);