ALTER TABLE klage.text_version_editor
    RENAME TO version_editor;

DELETE
FROM klage.version_editor;

ALTER TABLE klage.version_editor
    ADD COLUMN id                         UUID PRIMARY KEY,
    ADD COLUMN created                    TIMESTAMP NOT NULL,
    ADD COLUMN modified                   TIMESTAMP NOT NULL,
    ADD COLUMN maltekstseksjon_version_id UUID REFERENCES klage.maltekstseksjon_version (id);

ALTER TABLE klage.version_editor
    ADD CONSTRAINT ck_version_editor_fk CHECK (
            (maltekstseksjon_version_id IS NOT NULL and text_version_id IS NULL)
            OR (maltekstseksjon_version_id IS NULL and text_version_id IS NOT NULL)
        );

DROP TABLE klage.maltekstseksjon_version_editor;

CREATE INDEX maltekstseksjon_version_editor_ix ON klage.version_editor (maltekstseksjon_version_id);
