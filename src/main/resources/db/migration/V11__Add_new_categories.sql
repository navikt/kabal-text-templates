CREATE TABLE klage.template_section
(
    text_id  UUID REFERENCES klage.text (id),
    template_section TEXT NOT NULL
);

CREATE INDEX text_template_section_ix ON klage.template_section (text_id);

CREATE TABLE klage.ytelse_hjemmel
(
    text_id  UUID REFERENCES klage.text (id),
    ytelse_hjemmel TEXT NOT NULL
);

CREATE INDEX text_ytelse_hjemmel_ix ON klage.ytelse_hjemmel (text_id);