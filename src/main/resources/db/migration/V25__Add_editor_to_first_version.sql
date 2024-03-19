INSERT INTO klage.version_editor (id, nav_ident, text_version_id, created, modified, maltekstseksjon_version_id)
SELECT gen_random_uuid(), 'SYSTEMBRUKER', tv.id, tv.created, tv.modified, NULL
FROM klage.text_version tv
         LEFT JOIN klage.version_editor ve ON tv.id = ve.text_version_id
WHERE ve.text_version_id IS NULL;

INSERT INTO klage.version_editor (id, nav_ident, text_version_id, created, modified, maltekstseksjon_version_id)
SELECT gen_random_uuid(), 'SYSTEMBRUKER', mv.id, mv.created, mv.modified, NULL
FROM klage.maltekstseksjon_version mv
         LEFT JOIN klage.version_editor ve ON mv.id = ve.text_version_id
WHERE ve.text_version_id IS NULL;