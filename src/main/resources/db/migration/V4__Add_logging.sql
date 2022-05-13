CREATE TABLE klage.changelogentry
(
    id                 UUID PRIMARY KEY,
    text_id            UUID      NOT NULL,
    saksbehandlerident TEXT,
    action             TEXT      NOT NULL,
    field              TEXT      NOT NULL,
    fromvalue          TEXT,
    tovalue            TEXT,
    timestamp          TIMESTAMP NOT NULL,
    CONSTRAINT fk_changelogentry_text
        FOREIGN KEY (text_id)
            REFERENCES klage.text (id)
);

CREATE INDEX changelog_text_idx ON klage.changelogentry (text_id);