UPDATE klage.text
SET modified = created
WHERE modified IS NULL;

ALTER TABLE klage.text
    ALTER COLUMN modified SET NOT NULL;