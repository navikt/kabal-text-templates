UPDATE klage.text
SET modified = created
WHERE modified is null;

ALTER TABLE klage.text
    ALTER COLUMN modified SET NOT NULL;