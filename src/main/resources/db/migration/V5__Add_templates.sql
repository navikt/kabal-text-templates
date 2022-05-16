CREATE TABLE klage.template
(
    text_id  UUID REFERENCES klage.text (id),
    template TEXT NOT NULL
);

CREATE INDEX text_template_ix ON klage.template (text_id);