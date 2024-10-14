UPDATE klage.text_version
SET published_by      = 'SYSTEMBRUKER',
    published_by_name = 'SYSTEMBRUKER'
WHERE published_by IS NULL
  AND published_date_time IS NOT NULL;

UPDATE klage.maltekstseksjon_version
SET published_by      = 'SYSTEMBRUKER',
    published_by_name = 'SYSTEMBRUKER'
WHERE published_by IS NULL
  AND published_date_time IS NOT NULL;