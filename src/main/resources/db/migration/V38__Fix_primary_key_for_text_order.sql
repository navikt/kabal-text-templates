-- Primærnøkkelen på (maltekstseksjon_version_id, text_id) stammer fra V13, der tabellen ennå ikke
-- var en ordnet liste. Hibernate adresserer rader i en @OrderColumn-liste på (eier, index) og
-- oppdaterer text_id, altså nettopp den kolonnen primærnøkkelen lå på. Det ga transiente
-- nøkkelkollisjoner ved omorganisering, som V22 dempet ved å gjøre nøkkelen deferrable.
--
-- Her flyttes nøkkelen dit identiteten faktisk ligger, slik at den kan være immediate igjen.
-- Kravet om at samme tekst bare kan forekomme én gang per versjon beholdes som en egen
-- unik constraint. Den må fortsatt være deferrable: MaltekstseksjonService.updateTexts gjør
-- clear() etterfulgt av addAll(), og Hibernate optimaliserer dette til UPDATE ... SET text_id
-- når listelengden er uendret. Hver omorganisering en redaktør gjør, gir derfor et forbigående
-- duplikat på (maltekstseksjon_version_id, text_id).
-- Verifisert i MaltekstseksjonVersionTextOrderTest.

DO
$$
    DECLARE
        pk_name             TEXT;
        duplicate_positions BIGINT;
        versions_with_gaps  BIGINT;
    BEGIN
        SELECT count(*)
        INTO duplicate_positions
        FROM (SELECT 1
              FROM klage.maltekstseksjon_version_text
              GROUP BY maltekstseksjon_version_id, "index"
              HAVING count(*) > 1) AS duplicates;

        IF duplicate_positions > 0 THEN
            RAISE EXCEPTION
                'Fant % kombinasjoner av (maltekstseksjon_version_id, index) med mer enn én rad. Disse må ryddes opp før migrasjonen kan kjøre.',
                duplicate_positions;
        END IF;

        -- Diagnostikk, ikke en forutsetning for nøkkelbyttet: en primærnøkkel godtar hull i index,
        -- men Hibernate bygger @OrderColumn-listen posisjonsvis og gir da null-elementer.
        -- Slike rader er allerede korrupte, og bør oppdages her framfor når en redaktør åpner
        -- maltekstseksjonen.
        SELECT count(*)
        INTO versions_with_gaps
        FROM (SELECT 1
              FROM klage.maltekstseksjon_version_text
              GROUP BY maltekstseksjon_version_id
              HAVING min("index") <> 0
                  OR max("index") <> count(*) - 1) AS gaps;

        IF versions_with_gaps > 0 THEN
            RAISE EXCEPTION
                'Fant % maltekstseksjonversjoner med hull i index. Disse må ryddes opp før migrasjonen kan kjøre.',
                versions_with_gaps;
        END IF;

        SELECT conname
        INTO pk_name
        FROM pg_constraint
        WHERE conrelid = 'klage.maltekstseksjon_version_text'::regclass
          AND contype = 'p';

        IF pk_name IS NOT NULL THEN
            EXECUTE format('ALTER TABLE klage.maltekstseksjon_version_text DROP CONSTRAINT %I', pk_name);
        END IF;
    END
$$;

ALTER TABLE klage.maltekstseksjon_version_text
    ADD CONSTRAINT pk_maltekstseksjon_version_text
        PRIMARY KEY (maltekstseksjon_version_id, "index");

ALTER TABLE klage.maltekstseksjon_version_text
    ADD CONSTRAINT uq_maltekstseksjon_version_text
        UNIQUE (maltekstseksjon_version_id, text_id) DEFERRABLE INITIALLY DEFERRED;

-- Overflødig etter at maltekstseksjon_version_id ble ledende kolonne i primærnøkkelen.
-- Indeksen på text_id beholdes, den brukes ved sletting av tekster.
DROP INDEX IF EXISTS klage.maltekst_text_maltekst_ix;

-- Nødvendig for logisk replikering: nøkkelen er nå immediate og kan brukes som replica identity.
-- Overstyrer REPLICA IDENTITY FULL fra V37, som var et forsøk på å komme rundt den deferrable
-- primærnøkkelen. DMS avviser FULL når tabellen har primærnøkkel, og DEFAULT er nå gyldig.
ALTER TABLE klage.maltekstseksjon_version_text
    REPLICA IDENTITY DEFAULT;
