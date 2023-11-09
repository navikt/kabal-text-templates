ALTER TABLE klage.maltekst
    RENAME TO maltekstseksjon_version;

ALTER TABLE klage.maltekstseksjon_version
    ADD COLUMN published_date_time TIMESTAMP,
    ADD COLUMN published           BOOLEAN DEFAULT true,
    ADD COLUMN published_by        TEXT,
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
UPDATE klage.maltekstseksjon_version mv1
SET maltekstseksjon_id = (SELECT mv2.id FROM klage.maltekstseksjon_version mv2 WHERE mv1.id = mv2.id);

UPDATE klage.maltekstseksjon_version mv1
SET published_date_time = (SELECT mv2.created FROM klage.maltekstseksjon_version mv2 WHERE mv1.id = mv2.id);

CREATE INDEX maltekstseksjon_published_ix ON klage.maltekstseksjon_version (published);

CREATE TABLE klage.maltekstseksjon_version_editor
(
    maltekstseksjon_version_id UUID REFERENCES klage.maltekstseksjon_version (id),
    nav_ident                  TEXT NOT NULL
);

CREATE INDEX maltekstseksjon_version_id_ix ON klage.maltekstseksjon_version_editor (maltekstseksjon_version_id);

ALTER TABLE klage.enhet
    RENAME COLUMN maltekst_id TO maltekstseksjon_version_id;
ALTER TABLE klage.template_section
    RENAME COLUMN maltekst_id TO maltekstseksjon_version_id;
ALTER TABLE klage.utfall
    RENAME COLUMN maltekst_id TO maltekstseksjon_version_id;
ALTER TABLE klage.ytelse_hjemmel
    RENAME COLUMN maltekst_id TO maltekstseksjon_version_id;

ALTER TABLE klage.maltekst_text
    RENAME TO maltekstseksjon_version_text;

ALTER TABLE klage.maltekstseksjon_version_text
    RENAME COLUMN maltekst_id TO maltekstseksjon_version_id;

--TODO rename indicies