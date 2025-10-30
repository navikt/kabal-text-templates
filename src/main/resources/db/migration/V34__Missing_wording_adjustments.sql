--Nav klageinstans, title

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, 'Nav klageinstans', 'Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, '"Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

--ledd, title

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, ' ledd$', ' avsnitt', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, ' ledd$') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, ' ledd\)', ' avsnitt)', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, ' ledd\)') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, ' ledd-', ' avsnitt-', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, ' ledd-') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, ' leddpleiepenger', ' avsnitt pleiepenger', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, ' leddpleiepenger') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));


--ledd, content
UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, ' ledd"', ' avsnitt"', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, ' ledd"') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, ' ledd]', ' avsnitt]', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, ' ledd]') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, ' ledd»', ' avsnitt»', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, ' ledd»') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, ' ledd\)', ' avsnitt)', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, ' ledd\)') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, '§ 2-1 2\.ledd', '§ 2-1 2\. avsnitt', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, '§ 2-1 2\.ledd') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, 'dette leddet', 'dette avsnittet', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, 'dette leddet') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

--ledd, rich_text_nb
UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, ' ledd"', ' avsnitt"', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, ' ledd"') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, ' ledd]', ' avsnitt]', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, ' ledd]') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, ' ledd\\n', ' avsnitt\n', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, ' ledd\\n') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));
--ledd, rich_text_untranslated
UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, ' ledd"', ' avsnitt"', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, ' ledd"') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, ' ledd]', ' avsnitt]', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, ' ledd]') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, ' ledd»', ' avsnitt»', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, ' ledd»') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, ' ledd\)', ' avsnitt)', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, ' ledd\)') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, ' leddet', ' avsnittet', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, ' leddet') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));