ALTER TABLE klage.maltekstseksjon_version_text
    DROP CONSTRAINT pk_maltekstseksjon_version_text;

ALTER TABLE klage.maltekstseksjon_version_text
    ADD PRIMARY KEY (maltekstseksjon_version_id, text_id) DEFERRABLE INITIALLY DEFERRED;
