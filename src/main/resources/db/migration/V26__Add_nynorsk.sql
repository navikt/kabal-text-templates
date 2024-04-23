ALTER TABLE klage.text_version
    ADD COLUMN rich_text_nn TEXT,
    ADD COLUMN rich_text_nb TEXT,
    ADD COLUMN plain_text_nn TEXT,
    ADD COLUMN plain_text_nb TEXT,
    ADD COLUMN rich_text_untranslated TEXT;

update klage.text_version
    set rich_text_nb = content
where text_type <> 'REGELVERK';

update klage.text_version
set rich_text_untranslated = content
where text_type = 'REGELVERK';

update klage.text_version
set plain_text_nb = plain_text;

ALTER TABLE klage.version_editor
    ADD COLUMN change_type TEXT NOT NULL DEFAULT 'UNKNOWN';

ALTER TABLE klage.version_editor
    DROP COLUMN modified;