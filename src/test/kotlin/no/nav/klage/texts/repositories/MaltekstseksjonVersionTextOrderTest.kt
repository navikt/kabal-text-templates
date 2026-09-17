package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.Text
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

/**
 * Regresjonstester for rekkefølgen på tekster i en maltekstseksjonversjon.
 *
 * Bakgrunn: primærnøkkelen på klage.maltekstseksjon_version_text lå opprinnelig på
 * (maltekstseksjon_version_id, text_id), mens Hibernate adresserer rader i en @OrderColumn-liste
 * på (maltekstseksjon_version_id, index) og oppdaterer text_id. Omorganisering ga derfor
 * transiente nøkkelkollisjoner. V38 flytter nøkkelen til (maltekstseksjon_version_id, index).
 *
 * Kjør med show-sql på og se etter UPDATE-setninger mot koblingstabellen for å forstå
 * hva Hibernate faktisk gjør.
 */
@ActiveProfiles("local")
@DataJpaTest
class MaltekstseksjonVersionTextOrderTest : TestPostgresqlContainer() {
    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository

    @Autowired
    lateinit var textRepository: TextRepository

    @Test
    fun `omorganisering av tekster beholder rekkefolgen`() {
        val maltekstseksjonVersionId = persistMaltekstseksjonVersionWithTexts(numberOfTexts = 3).id

        testEntityManager.flush()
        testEntityManager.clear()

        val originalTextIds =
            maltekstseksjonVersionRepository
                .getReferenceById(maltekstseksjonVersionId)
                .texts
                .map { it.id }

        assertThat(originalTextIds).hasSize(3)

        testEntityManager.clear()

        // Bytter om forste og siste tekst uten a rore de ovrige. Hibernate løser dette med
        // UPDATE ... SET text_id = ? WHERE maltekstseksjon_version_id = ? AND "index" = ?,
        // som gir et forbigående duplikat pa (maltekstseksjon_version_id, text_id) mellom de to
        // setningene. Med den gamle primaernokkelen feilet dette umiddelbart.
        val maltekstseksjonVersion = maltekstseksjonVersionRepository.getReferenceById(maltekstseksjonVersionId)
        val first = maltekstseksjonVersion.texts[0]
        maltekstseksjonVersion.texts[0] = maltekstseksjonVersion.texts[2]
        maltekstseksjonVersion.texts[2] = first

        testEntityManager.flush()
        testEntityManager.clear()

        val reorderedTextIds =
            maltekstseksjonVersionRepository
                .getReferenceById(maltekstseksjonVersionId)
                .texts
                .map { it.id }

        assertThat(reorderedTextIds)
            .containsExactly(originalTextIds[2], originalTextIds[1], originalTextIds[0])
    }

    @Test
    fun `fjerning av tekst fra midten lukker hullet i rekkefolgen`() {
        val maltekstseksjonVersionId = persistMaltekstseksjonVersionWithTexts(numberOfTexts = 3).id

        testEntityManager.flush()
        testEntityManager.clear()

        val originalTextIds =
            maltekstseksjonVersionRepository
                .getReferenceById(maltekstseksjonVersionId)
                .texts
                .map { it.id }

        testEntityManager.clear()

        val maltekstseksjonVersion = maltekstseksjonVersionRepository.getReferenceById(maltekstseksjonVersionId)
        maltekstseksjonVersion.texts.removeIf { it.id == originalTextIds[1] }

        testEntityManager.flush()
        testEntityManager.clear()

        // Sjekkes for radene som leses via texts, fordi et hull i index gir et null-element i listen
        // og dermed en NullPointerException i kartleggingen under i stedet for en lesbar feilmelding.
        val positions =
            testEntityManager.entityManager
                .createNativeQuery(
                    """
                    SELECT "index"
                    FROM klage.maltekstseksjon_version_text
                    WHERE maltekstseksjon_version_id = :maltekstseksjonVersionId
                    ORDER BY "index"
                    """,
                ).setParameter("maltekstseksjonVersionId", maltekstseksjonVersionId)
                .resultList
                .map { (it as Number).toInt() }

        assertThat(positions).containsExactly(0, 1)

        val remainingTextIds =
            maltekstseksjonVersionRepository
                .getReferenceById(maltekstseksjonVersionId)
                .texts
                .map { it.id }

        assertThat(remainingTextIds)
            .containsExactly(originalTextIds[0], originalTextIds[2])
    }

    @Test
    fun `clear og addAll med samme tekster i ny rekkefolge fungerer`() {
        val maltekstseksjonVersionId = persistMaltekstseksjonVersionWithTexts(numberOfTexts = 3).id

        testEntityManager.flush()
        testEntityManager.clear()

        val originalTextIds =
            maltekstseksjonVersionRepository
                .getReferenceById(maltekstseksjonVersionId)
                .texts
                .map { it.id }

        testEntityManager.clear()

        // Speiler MaltekstseksjonService.updateTexts: hele listen tommes og fylles pa nytt med
        // proxyer, med noyaktig samme tekster som for, bare i motsatt rekkefolge. Dette er stien
        // redaktoren utloser ved omorganisering i grensesnittet.
        val maltekstseksjonVersion = maltekstseksjonVersionRepository.getReferenceById(maltekstseksjonVersionId)
        maltekstseksjonVersion.texts.clear()
        maltekstseksjonVersion.texts.addAll(originalTextIds.reversed().map { textRepository.getReferenceById(it) })

        testEntityManager.flush()
        testEntityManager.clear()

        val positions =
            testEntityManager.entityManager
                .createNativeQuery(
                    """
                    SELECT "index"
                    FROM klage.maltekstseksjon_version_text
                    WHERE maltekstseksjon_version_id = :maltekstseksjonVersionId
                    ORDER BY "index"
                    """,
                ).setParameter("maltekstseksjonVersionId", maltekstseksjonVersionId)
                .resultList
                .map { (it as Number).toInt() }

        assertThat(positions).containsExactly(0, 1, 2)

        val reorderedTextIds =
            maltekstseksjonVersionRepository
                .getReferenceById(maltekstseksjonVersionId)
                .texts
                .map { it.id }

        assertThat(reorderedTextIds).containsExactlyElementsOf(originalTextIds.reversed())
    }

    @Test
    fun `samme tekst kan ikke legges til to ganger i samme versjon`() {
        val maltekstseksjonVersionId = persistMaltekstseksjonVersionWithTexts(numberOfTexts = 1).id

        testEntityManager.flush()
        testEntityManager.clear()

        val textId =
            maltekstseksjonVersionRepository
                .getReferenceById(maltekstseksjonVersionId)
                .texts
                .single()
                .id

        testEntityManager.clear()

        val maltekstseksjonVersion = maltekstseksjonVersionRepository.getReferenceById(maltekstseksjonVersionId)
        maltekstseksjonVersion.texts.add(textRepository.getReferenceById(textId))

        // Selve innsettingen gaar gjennom: uq_maltekstseksjon_version_text er DEFERRABLE INITIALLY
        // DEFERRED, saa bruddet oppdages forst naar constrainten gjores immediate. I produksjon
        // skjer det ved commit. @DataJpaTest ruller tilbake i stedet for aa committe, saa uten
        // SET CONSTRAINTS ALL IMMEDIATE ville testen passert uansett om regelen holdt eller ikke.
        testEntityManager.flush()

        assertThatThrownBy {
            testEntityManager.entityManager
                .createNativeQuery("SET CONSTRAINTS ALL IMMEDIATE")
                .executeUpdate()
        }.hasMessageContaining("uq_maltekstseksjon_version_text")
            .hasRootCauseInstanceOf(java.sql.SQLException::class.java)
    }

    private fun persistMaltekstseksjonVersionWithTexts(numberOfTexts: Int): MaltekstseksjonVersion {
        val now = LocalDateTime.now()

        val maltekstseksjon =
            testEntityManager.persist(
                Maltekstseksjon(
                    created = now,
                    modified = now,
                    createdBy = "abc",
                    createdByName = "abc",
                ),
            )

        val texts =
            (1..numberOfTexts).map {
                testEntityManager.persist(
                    Text(
                        created = now,
                        modified = now,
                        maltekstseksjonVersions = mutableListOf(),
                        createdBy = "abc",
                        createdByName = "abc",
                    ),
                )
            }

        val maltekstseksjonVersion =
            MaltekstseksjonVersion(
                title = "title",
                maltekstseksjon = maltekstseksjon,
                texts = texts.toMutableList(),
                publishedDateTime = null,
                publishedBy = null,
                publishedByName = null,
                published = false,
                utfallIdList = setOf("1"),
                enhetIdList = setOf("1"),
                templateSectionIdList = setOf("1"),
                ytelseHjemmelIdList = setOf("1"),
                editors =
                    mutableSetOf(
                        Editor(
                            navIdent = "saksbehandlerIdent",
                            name = "saksbehandlerName",
                            created = now,
                            changeType = Editor.ChangeType.MALTEKSTSEKSJON_TEXTS,
                        ),
                    ),
                created = now,
                modified = now,
            )

        return maltekstseksjonVersionRepository.save(maltekstseksjonVersion)
    }
}
