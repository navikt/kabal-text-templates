ALTER TABLE klage.maltekstseksjon
    ADD COLUMN created_by_name TEXT;

UPDATE klage.maltekstseksjon
SET created_by_name = 'Eriksson, Daniel'
WHERE created_by = 'E141394';

UPDATE klage.maltekstseksjon
SET created_by_name = 'Resløkken, Mia'
WHERE created_by = 'R158216';

---

ALTER TABLE klage.maltekstseksjon_version
    ADD COLUMN published_by_name TEXT;

UPDATE klage.maltekstseksjon_version
SET published_by_name = 'Eriksson, Daniel'
WHERE published_by = 'E141394';

UPDATE klage.maltekstseksjon_version
SET published_by_name = 'Resløkken, Mia'
WHERE published_by = 'R158216';

---

ALTER TABLE klage.text
    ADD COLUMN created_by_name TEXT;

UPDATE klage.text
SET created_by_name = 'Aavik, Ketil'
WHERE created_by = 'A100182';

UPDATE klage.text
SET created_by_name = 'Breivik, Tove'
WHERE created_by = 'B101325';

UPDATE klage.text
SET created_by_name = 'Bjurstedt, Sophie'
WHERE created_by = 'B153222';

UPDATE klage.text
SET created_by_name = 'Bergheim, Christine'
WHERE created_by = 'C142264';

UPDATE klage.text
SET created_by_name = 'Engebretsen, Kristin Beate'
WHERE created_by = 'E127480';

UPDATE klage.text
SET created_by_name = 'Eriksson, Daniel'
WHERE created_by = 'E141394';

UPDATE klage.text
SET created_by_name = 'Nordgård, Marlene'
WHERE created_by = 'E142628';

UPDATE klage.text
SET created_by_name = 'Evjenth, Eskild'
WHERE created_by = 'E161617';

UPDATE klage.text
SET created_by_name = 'Holm, Tina Terese L'
WHERE created_by = 'H125545';

UPDATE klage.text
SET created_by_name = 'Lindholm, Jakob'
WHERE created_by = 'L142009';

UPDATE klage.text
SET created_by_name = 'Landgraf, Tanita Saranya'
WHERE created_by = 'L158906';

UPDATE klage.text
SET created_by_name = 'Ramsøy, Christina Nilsson'
WHERE created_by = 'R148453';

UPDATE klage.text
SET created_by_name = 'Resløkken, Mia'
WHERE created_by = 'R158216';

UPDATE klage.text
SET created_by_name = 'Rikheim, Ragna Hove'
WHERE created_by = 'R169190';

UPDATE klage.text
SET created_by_name = 'Seppola, Marian'
WHERE created_by = 'S108187';

UPDATE klage.text
SET created_by_name = 'Storebø, Stian'
WHERE created_by = 'S108846';

UPDATE klage.text
SET created_by_name = 'Simonsen, Camilla Folgerø'
WHERE created_by = 'S160846';

UPDATE klage.text
SET created_by_name = 'Skaanes, Lisbeth'
WHERE created_by = 'S163082';

UPDATE klage.text
SET created_by_name = 'Thorsen, Anne Berith Følstad'
WHERE created_by = 'T109423';

---

ALTER TABLE klage.text_version
    ADD COLUMN published_by_name TEXT;

UPDATE klage.text_version
SET published_by_name = 'Aavik, Ketil'
WHERE published_by = 'A100182';

UPDATE klage.text_version
SET published_by_name = 'Breivik, Tove'
WHERE published_by = 'B101325';

UPDATE klage.text_version
SET published_by_name = 'Bjurstedt, Sophie'
WHERE published_by = 'B153222';

UPDATE klage.text_version
SET published_by_name = 'Bergheim, Christine'
WHERE published_by = 'C142264';

UPDATE klage.text_version
SET published_by_name = 'Engebretsen, Kristin Beate'
WHERE published_by = 'E127480';

UPDATE klage.text_version
SET published_by_name = 'Eriksson, Daniel'
WHERE published_by = 'E141394';

UPDATE klage.text_version
SET published_by_name = 'Nordgård, Marlene'
WHERE published_by = 'E142628';

UPDATE klage.text_version
SET published_by_name = 'Evjenth, Eskild'
WHERE published_by = 'E161617';

UPDATE klage.text_version
SET published_by_name = 'Gulbrandsen, Steffen Niklas'
WHERE published_by = 'G153065';

UPDATE klage.text_version
SET published_by_name = 'Holm, Tina Terese L'
WHERE published_by = 'H125545';

UPDATE klage.text_version
SET published_by_name = 'Iversen, Trine Erland'
WHERE published_by = 'I104325';

UPDATE klage.text_version
SET published_by_name = 'Lindholm, Jakob'
WHERE published_by = 'L142009';

UPDATE klage.text_version
SET published_by_name = 'Landgraf, Tanita Saranya'
WHERE published_by = 'L158906';

UPDATE klage.text_version
SET published_by_name = 'Resløkken, Mia'
WHERE published_by = 'R158216';

UPDATE klage.text_version
SET published_by_name = 'Rikheim, Ragna Hove'
WHERE published_by = 'R169190';

UPDATE klage.text_version
SET published_by_name = 'Seppola, Marian'
WHERE published_by = 'S108187';

UPDATE klage.text_version
SET published_by_name = 'Storebø, Stian'
WHERE published_by = 'S108846';

UPDATE klage.text_version
SET published_by_name = 'Simonsen, Camilla Folgerø'
WHERE published_by = 'S160846';

UPDATE klage.text_version
SET published_by_name = 'Skaanes, Lisbeth'
WHERE published_by = 'S163082';

UPDATE klage.text_version
SET published_by_name = 'SYSTEMBRUKER'
WHERE published_by = 'SYSTEMBRUKER';

UPDATE klage.text_version
SET published_by_name = 'Thorsen, Anne Berith Følstad'
WHERE published_by = 'T109423';

UPDATE klage.text_version
SET published_by_name = 'Vigestad, Odin'
WHERE published_by = 'V135783';

---

ALTER TABLE klage.version_editor
    ADD COLUMN name TEXT;

UPDATE klage.version_editor
SET name = 'Aavik, Ketil'
WHERE nav_ident = 'A100182';

UPDATE klage.version_editor
SET name = 'Breivik, Tove'
WHERE nav_ident = 'B101325';

UPDATE klage.version_editor
SET name = 'Bjurstedt, Sophie'
WHERE nav_ident = 'B153222';

UPDATE klage.version_editor
SET name = 'Bergheim, Christine'
WHERE nav_ident = 'C142264';

UPDATE klage.version_editor
SET name = 'Engebretsen, Kristin Beate'
WHERE nav_ident = 'E127480';

UPDATE klage.version_editor
SET name = 'Eriksson, Daniel'
WHERE nav_ident = 'E141394';

UPDATE klage.version_editor
SET name = 'Nordgård, Marlene'
WHERE nav_ident = 'E142628';

UPDATE klage.version_editor
SET name = 'Evjenth, Eskild'
WHERE nav_ident = 'E161617';

UPDATE klage.version_editor
SET name = 'Gulbrandsen, Steffen Niklas'
WHERE nav_ident = 'G153065';

UPDATE klage.version_editor
SET name = 'Holm, Tina Terese L'
WHERE nav_ident = 'H125545';

UPDATE klage.version_editor
SET name = 'Iversen, Trine Erland'
WHERE nav_ident = 'I104325';

UPDATE klage.version_editor
SET name = 'Lindholm, Jakob'
WHERE nav_ident = 'L142009';

UPDATE klage.version_editor
SET name = 'Landgraf, Tanita Saranya'
WHERE nav_ident = 'L158906';

UPDATE klage.version_editor
SET name = 'Ramsøy, Christina Nilsson'
WHERE nav_ident = 'R148453';

UPDATE klage.version_editor
SET name = 'Resløkken, Mia'
WHERE nav_ident = 'R158216';

UPDATE klage.version_editor
SET name = 'Rikheim, Ragna Hove'
WHERE nav_ident = 'R169190';

UPDATE klage.version_editor
SET name = 'Seppola, Marian'
WHERE nav_ident = 'S108187';

UPDATE klage.version_editor
SET name = 'Storebø, Stian'
WHERE nav_ident = 'S108846';

UPDATE klage.version_editor
SET name = 'Simonsen, Camilla Folgerø'
WHERE nav_ident = 'S160846';

UPDATE klage.version_editor
SET name = 'Skaanes, Lisbeth'
WHERE nav_ident = 'S163082';

UPDATE klage.version_editor
SET name = 'SYSTEMBRUKER'
WHERE nav_ident = 'SYSTEMBRUKER';

UPDATE klage.version_editor
SET name = 'Thorsen, Anne Berith Følstad'
WHERE nav_ident = 'T109423';

UPDATE klage.version_editor
SET name = 'Vigestad, Odin'
WHERE nav_ident = 'V135783';