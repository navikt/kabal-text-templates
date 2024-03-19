INSERT INTO klage.version_editor (id, nav_ident, text_version_id, created, modified, maltekstseksjon_version_id)
SELECT gen_random_uuid(), 'SYSTEMBRUKER', tv.id, tv.created, tv.modified, NULL
FROM klage.text_version tv
         LEFT JOIN klage.version_editor ve ON tv.id = ve.text_version_id
WHERE ve.text_version_id IS NULL;

INSERT INTO klage.version_editor (id, nav_ident, text_version_id, created, modified, maltekstseksjon_version_id)
SELECT gen_random_uuid(), 'SYSTEMBRUKER', NULL, mv.created, mv.modified, mv.id
FROM klage.maltekstseksjon_version mv
         LEFT JOIN klage.version_editor ve ON mv.id = ve.maltekstseksjon_version_id
WHERE ve.maltekstseksjon_version_id IS NULL;

UPDATE klage.text_version
SET published_by = 'SYSTEMBRUKER'
WHERE published_by IS NULL
  AND published IS true;

UPDATE klage.maltekstseksjon_version
SET published_by = 'SYSTEMBRUKER'
WHERE published_by IS NULL
  AND published IS true;