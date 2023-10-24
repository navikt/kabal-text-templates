ALTER TABLE klage.text
    RENAME TO text_version;

ALTER TABLE klage.text_version
    ADD COLUMN published_date_time TIMESTAMP,
    ADD COLUMN published           BOOLEAN DEFAULT true,
    ADD COLUMN published_by        TEXT,
    ADD COLUMN text_id             UUID;

CREATE TABLE klage.text
(
    id       UUID PRIMARY KEY,
    created  TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    deleted  BOOLEAN DEFAULT FALSE
);

ALTER TABLE klage.text_version
    ADD CONSTRAINT fk_text_text_version
        FOREIGN KEY (text_id)
            REFERENCES klage.text (id)
            ON DELETE SET NULL;

--use same id initially
INSERT INTO klage.text(id, created, modified)
SELECT id, created, modified
FROM klage.text_version;

--use same id initially
UPDATE klage.text_version tv1
SET text_id = (SELECT tv2.id FROM klage.text_version tv2 WHERE tv1.id = tv2.id);

UPDATE klage.text_version tv1
SET published_date_time = (SELECT tv2.created FROM klage.text_version tv2 WHERE tv1.id = tv2.id);

CREATE INDEX text_published_ix ON klage.text_version (published);

CREATE TABLE klage.text_version_editor
(
    text_version_id UUID REFERENCES klage.text_version (id),
    nav_ident       TEXT NOT NULL
);

CREATE INDEX text_version_editor_ix ON klage.text_version_editor (text_version_id);

ALTER TABLE klage.enhet
    RENAME COLUMN text_id TO text_version_id;
ALTER TABLE klage.template_section
    RENAME COLUMN text_id TO text_version_id;
ALTER TABLE klage.utfall
    RENAME COLUMN text_id TO text_version_id;
ALTER TABLE klage.ytelse_hjemmel
    RENAME COLUMN text_id TO text_version_id;

ALTER TABLE klage.maltekst_text
    DROP CONSTRAINT maltekst_text_text_id_fkey;

ALTER TABLE klage.maltekst_text
    ADD FOREIGN KEY (text_id)
        REFERENCES klage.text (id);


--TODO rename indicies