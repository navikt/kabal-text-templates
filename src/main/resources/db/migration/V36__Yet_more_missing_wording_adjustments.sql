UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, '§ 2-1 2\\. avsnitt', '§ 2-1 2. avsnitt', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, '§ 2-1 2\\. avsnitt') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, '§ 2-1 2\\. avsnitt', '§ 2-1 2. avsnitt', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, '§ 2-1 2\\. avsnitt') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, '\."  "}', '."', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, '\."  "}') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));
