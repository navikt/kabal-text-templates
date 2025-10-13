CREATE UNIQUE INDEX ON klage.text_version (text_id, (published_date_time IS NULL))
    WHERE published_date_time IS NULL;