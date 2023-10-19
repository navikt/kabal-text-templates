CREATE TABLE klage.maltekst
(
    id       UUID PRIMARY KEY,
    title    TEXT      NOT NULL,
    created  TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL
);

ALTER TABLE klage.text
    ADD COLUMN maltekst_id UUID REFERENCES klage.maltekst (id);

ALTER TABLE klage.template_section
    ADD COLUMN maltekst_id UUID REFERENCES klage.maltekst (id);

ALTER TABLE klage.ytelse_hjemmel
    ADD COLUMN maltekst_id UUID REFERENCES klage.maltekst (id);

ALTER TABLE klage.utfall
    ADD COLUMN maltekst_id UUID REFERENCES klage.maltekst (id);

ALTER TABLE klage.enhet
    ADD COLUMN maltekst_id UUID REFERENCES klage.maltekst (id);


CREATE INDEX maltekst_text_ix ON klage.text (maltekst_id);
CREATE INDEX maltekst_template_section_ix ON klage.template_section (maltekst_id);
CREATE INDEX maltekst_ytelse_hjemmel_ix ON klage.ytelse_hjemmel (maltekst_id);
CREATE INDEX maltekst_utfall_ix ON klage.utfall (maltekst_id);
CREATE INDEX maltekst_enhet_ix ON klage.enhet (maltekst_id);
