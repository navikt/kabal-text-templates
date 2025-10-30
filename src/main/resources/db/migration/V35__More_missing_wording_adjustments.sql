UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, 'Nav klageinstans', 'Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, 'Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, '<ledd>', '<avsnitt>', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, '<ledd>') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, '"Ledd"', '"Avsnitt"', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, '"Ledd"') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, '"ledd"', '"avsnitt"', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, '"ledd"') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, '§ 2-1 2\.ledd', '§ 2-1 2\. avsnitt', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, '§ 2-1 2\.ledd') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));