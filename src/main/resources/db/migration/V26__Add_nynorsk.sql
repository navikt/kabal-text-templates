ALTER TABLE klage.text_version
    ADD COLUMN rich_text_nn           TEXT,
    ADD COLUMN rich_text_nb           TEXT,
    ADD COLUMN plain_text_nn          TEXT,
    ADD COLUMN plain_text_nb          TEXT,
    ADD COLUMN rich_text_untranslated TEXT;

UPDATE klage.text_version
SET rich_text_nb = content
WHERE text_type <> 'REGELVERK';

UPDATE klage.text_version
SET rich_text_untranslated = content
WHERE text_type = 'REGELVERK';

UPDATE klage.text_version
SET plain_text_nb = plain_text;

ALTER TABLE klage.version_editor
    ADD COLUMN change_type TEXT NOT NULL DEFAULT 'UNKNOWN';

ALTER TABLE klage.version_editor
    DROP COLUMN modified;