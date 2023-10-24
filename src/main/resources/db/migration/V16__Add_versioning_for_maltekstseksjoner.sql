ALTER TABLE klage.maltekst
    RENAME TO maltekstseksjon_version;

ALTER TABLE klage.maltekstseksjon_version
    ADD COLUMN published_date_time TIMESTAMP,
    ADD COLUMN published           BOOLEAN DEFAULT true,
    ADD COLUMN maltekstseksjon_id  UUID;

CREATE TABLE klage.maltekstseksjon
(
    id       UUID PRIMARY KEY,
    created  TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    deleted  BOOLEAN DEFAULT FALSE
);

ALTER TABLE klage.maltekstseksjon_version
    ADD CONSTRAINT fk_maltekstseksjon_maltekstseksjon_version
        FOREIGN KEY (maltekstseksjon_id)
            REFERENCES klage.maltekstseksjon (id)
            ON DELETE SET NULL;

--use same id initially
INSERT INTO klage.maltekstseksjon(id, created, modified)
SELECT id, created, modified
FROM klage.maltekstseksjon_version;

--use same id initially
UPDATE klage.maltekstseksjon_version
SET maltekstseksjon_id  = (SELECT id FROM klage.maltekstseksjon_version),
    published_date_time = (SELECT created FROM klage.maltekstseksjon_version);

CREATE INDEX maltekstseksjon_published_ix ON klage.maltekstseksjon_version (published);

CREATE TABLE klage.maltekstseksjon_version_editor
(
    maltekstseksjon_version_id UUID REFERENCES klage.maltekstseksjon_version (id),
    nav_ident                  TEXT NOT NULL
);

CREATE INDEX maltekstseksjon_version_id_ix ON klage.maltekstseksjon_version_editor (maltekstseksjon_version_id);

--TODO rename indicies