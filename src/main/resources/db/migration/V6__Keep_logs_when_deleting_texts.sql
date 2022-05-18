ALTER TABLE klage.changelog_entry
    DROP CONSTRAINT fk_changelog_entry_text;

ALTER TABLE klage.changelog_entry
    ALTER COLUMN text_id DROP NOT NULL;

ALTER TABLE klage.changelog_entry
    ADD CONSTRAINT fk_changelog_entry_text
        FOREIGN KEY (text_id)
            REFERENCES klage.text (id)
            ON DELETE SET NULL;