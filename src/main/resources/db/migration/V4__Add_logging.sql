CREATE TABLE klage.changelog_entry
(
    id                 UUID PRIMARY KEY,
    text_id            UUID      NOT NULL,
    saksbehandlerident TEXT,
    action             TEXT      NOT NULL,
    field              TEXT      NOT NULL,
    from_value         TEXT,
    to_value           TEXT,
    created            TIMESTAMP NOT NULL,
    CONSTRAINT fk_changelog_entry_text
        FOREIGN KEY (text_id)
            REFERENCES klage.text (id)
);

CREATE INDEX changelog_text_idx ON klage.changelog_entry (text_id);