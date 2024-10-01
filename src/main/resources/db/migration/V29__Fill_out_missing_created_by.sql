UPDATE klage.text
SET created_by = 'SYSTEM'
WHERE created_by IS NULL;

UPDATE klage.text
SET created_by_name = 'SYSTEM'
WHERE created_by_name IS NULL;

