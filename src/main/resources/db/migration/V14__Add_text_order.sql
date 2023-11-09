CREATE TABLE klage.maltekst_text
(
    maltekst_id UUID NOT NULL,
    text_id     UUID NOT NULL,
    index       INT  NOT NULL,
    CONSTRAINT fk_changelog_entry_text
        PRIMARY KEY (maltekst_id, text_id),
    FOREIGN KEY (maltekst_id) REFERENCES klage.maltekst (id),
    FOREIGN KEY (text_id) REFERENCES klage.text (id)
);

ALTER TABLE klage.text
    DROP COLUMN maltekst_id;

CREATE INDEX maltekst_text_maltekst_ix ON klage.maltekst_text (maltekst_id);
CREATE INDEX maltekst_text_text_ix ON klage.maltekst_text (text_id);
