UPDATE klage.text
SET created_by = 'SYSTEMBRUKER'
WHERE created_by IS NULL;

UPDATE klage.text
SET created_by_name = 'SYSTEMBRUKER'
WHERE created_by_name IS NULL;

