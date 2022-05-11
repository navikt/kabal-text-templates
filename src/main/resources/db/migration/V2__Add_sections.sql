CREATE TABLE klage.section
(
    text_id UUID REFERENCES klage.text (id),
    section   TEXT NOT NULL
);

CREATE INDEX text_section_ix ON klage.section (text_id);