ALTER TABLE klage.text
    ADD COLUMN plain_text TEXT;

ALTER TABLE klage.text
    ALTER COLUMN content DROP NOT NULL;