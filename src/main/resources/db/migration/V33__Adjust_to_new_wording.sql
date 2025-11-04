--Nav klageinstans, title

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, '"Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, '"Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, '"\u00A0Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, '"\u00A0Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, '\. Nav klageinstans', '. Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, '\. Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, ': Nav klageinstans', ': Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, ': Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, '\\tNav klageinstans', '\tKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, '\\tNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, '\\nNav klageinstans', '\nKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, '\\nNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, 'Nav/Nav klageinstans', 'Nav/Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, 'Nav/Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, ' Nav klageinstans', ' klageinstansen', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, ' Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

--Nav klageinstans, content

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, '"Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, '"Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, '"\u00A0Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, '"\u00A0Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, '\. Nav klageinstans', '. Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, '\. Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, ': Nav klageinstans', ': Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, ': Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, '\\tNav klageinstans', '\tKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, '\\tNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, '\\nNav klageinstans', '\nKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, '\\nNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, 'Nav/Nav klageinstans', 'Nav/Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, 'Nav/Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET content =
        REGEXP_REPLACE(content, ' Nav klageinstans', ' klageinstansen', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.content, ' Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

--Nav klageinstans, plain_text

UPDATE klage.text_version tvo
SET plain_text =
        REGEXP_REPLACE(plain_text, '"Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text, '"Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text =
        REGEXP_REPLACE(plain_text, '"\u00A0Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text, '"\u00A0Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text =
        REGEXP_REPLACE(plain_text, '\. Nav klageinstans', '. Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text, '\. Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text =
        REGEXP_REPLACE(plain_text, ': Nav klageinstans', ': Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text, ': Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text =
        REGEXP_REPLACE(plain_text, '\\tNav klageinstans', '\tKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text, '\\tNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text =
        REGEXP_REPLACE(plain_text, '\\nNav klageinstans', '\nKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text, '\\nNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text =
        REGEXP_REPLACE(plain_text, 'Nav/Nav klageinstans', 'Nav/Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text, 'Nav/Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text =
        REGEXP_REPLACE(plain_text, ' Nav klageinstans', ' klageinstansen', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text, ' Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

--Nav klageinstans, rich_text_nn

UPDATE klage.text_version tvo
SET rich_text_nn =
        REGEXP_REPLACE(rich_text_nn, '"Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nn, '"Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nn =
        REGEXP_REPLACE(rich_text_nn, '"\u00A0Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nn, '"\u00A0Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nn =
        REGEXP_REPLACE(rich_text_nn, '\. Nav klageinstans', '. Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nn, '\. Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nn =
        REGEXP_REPLACE(rich_text_nn, ': Nav klageinstans', ': Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nn, ': Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nn =
        REGEXP_REPLACE(rich_text_nn, '\\tNav klageinstans', '\tKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nn, '\\tNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nn =
        REGEXP_REPLACE(rich_text_nn, '\\nNav klageinstans', '\nKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nn, '\\nNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nn =
        REGEXP_REPLACE(rich_text_nn, 'Nav/Nav klageinstans', 'Nav/Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nn, 'Nav/Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nn =
        REGEXP_REPLACE(rich_text_nn, ' Nav klageinstans', ' klageinstansen', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nn, ' Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

--Nav klageinstans, rich_text_nb

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, '"Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, '"Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, '"\u00A0Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, '"\u00A0Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, '\. Nav klageinstans', '. Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, '\. Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, ': Nav klageinstans', ': Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, ': Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, '\\tNav klageinstans', '\tKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, '\\tNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, '\\nNav klageinstans', '\nKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, '\\nNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, 'Nav/Nav klageinstans', 'Nav/Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, 'Nav/Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_nb =
        REGEXP_REPLACE(rich_text_nb, ' Nav klageinstans', ' klageinstansen', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_nb, ' Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));


--Nav klageinstans, plain_text_nn

UPDATE klage.text_version tvo
SET plain_text_nn =
        REGEXP_REPLACE(plain_text_nn, '"Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nn, '"Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nn =
        REGEXP_REPLACE(plain_text_nn, '"\u00A0Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nn, '"\u00A0Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nn =
        REGEXP_REPLACE(plain_text_nn, '\. Nav klageinstans', '. Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nn, '\. Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nn =
        REGEXP_REPLACE(plain_text_nn, ': Nav klageinstans', ': Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nn, ': Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nn =
        REGEXP_REPLACE(plain_text_nn, '\\tNav klageinstans', '\tKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nn, '\\tNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nn =
        REGEXP_REPLACE(plain_text_nn, '\\nNav klageinstans', '\nKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nn, '\\nNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nn =
        REGEXP_REPLACE(plain_text_nn, 'Nav/Nav klageinstans', 'Nav/Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nn, 'Nav/Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nn =
        REGEXP_REPLACE(plain_text_nn, ' Nav klageinstans', ' klageinstansen', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nn, ' Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

--Nav klageinstans, plain_text_nb

UPDATE klage.text_version tvo
SET plain_text_nb =
        REGEXP_REPLACE(plain_text_nb, '"Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nb, '"Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nb =
        REGEXP_REPLACE(plain_text_nb, '"\u00A0Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nb, '"\u00A0Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nb =
        REGEXP_REPLACE(plain_text_nb, '\. Nav klageinstans', '. Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nb, '\. Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nb =
        REGEXP_REPLACE(plain_text_nb, ': Nav klageinstans', ': Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nb, ': Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nb =
        REGEXP_REPLACE(plain_text_nb, '\\tNav klageinstans', '\tKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nb, '\\tNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nb =
        REGEXP_REPLACE(plain_text_nb, '\\nNav klageinstans', '\nKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nb, '\\nNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nb =
        REGEXP_REPLACE(plain_text_nb, 'Nav/Nav klageinstans', 'Nav/Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nb, 'Nav/Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET plain_text_nb =
        REGEXP_REPLACE(plain_text_nb, ' Nav klageinstans', ' klageinstansen', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.plain_text_nb, ' Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

--Nav klageinstans, rich_text_untranslated

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, '"Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, '"Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, '"\u00A0Nav klageinstans', '"Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, '"\u00A0Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, '\. Nav klageinstans', '. Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, '\. Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, ': Nav klageinstans', ': Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, ': Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, '\\tNav klageinstans', '\tKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, '\\tNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, '\\nNav klageinstans', '\nKlageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, '\\nNav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, 'Nav/Nav klageinstans', 'Nav/Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, 'Nav/Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET rich_text_untranslated =
        REGEXP_REPLACE(rich_text_untranslated, ' Nav klageinstans', ' klageinstansen', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.rich_text_untranslated, ' Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

UPDATE klage.text_version tvo
SET title =
        REGEXP_REPLACE(title, 'Nav klageinstans', 'Klageinstans', 'g')
WHERE tvo.id in (SELECT id
                 FROM klage.text_version tv
                          LEFT JOIN LATERAL
                     regexp_matches(tv.title, 'Nav klageinstans') AS matches(match_array) ON TRUE
                 WHERE matches.match_array IS NOT NULL
                   AND text_type IN ('REGELVERK', 'GOD_FORMULERING'));

