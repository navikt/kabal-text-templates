package no.nav.klage.texts.repositories

import io.mockk.mockk
import no.nav.klage.texts.api.views.TextInput
import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.exceptions.TextNotFoundException
import no.nav.klage.texts.service.PublishMaltekstseksjonService
import no.nav.klage.texts.service.TextService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.*

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TextServiceTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository

    @Autowired
    lateinit var textVersionRepository: TextVersionRepository

    @Autowired
    lateinit var textRepository: TextRepository

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    lateinit var textService: TextService

    lateinit var currentTextID: UUID

    @BeforeEach
    fun setup() {
        textService = TextService(
            textRepository = textRepository,
            textVersionRepository = textVersionRepository,
            maltekstseksjonVersionRepository = maltekstseksjonVersionRepository,
            searchTextService = mockk(),
            publishMaltekstseksjonService = PublishMaltekstseksjonService(maltekstseksjonVersionRepository),
        )
    }

    @AfterEach
    fun cleanupDB() {
        maltekstseksjonVersionRepository.deleteAll()
        textVersionRepository.deleteAll()

        testEntityManager.flush()
        testEntityManager.clear()
    }

    @Test
    fun `unpublish text works for published maltekstseksjon`() {
        setupDataForPublishedMaltekstseksjon()

        var maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()
        assertThat(maltekstseksjonVersions).hasSize(1)
        assertThat(maltekstseksjonVersions.first().texts).hasSize(1)

        textService.deleteOrUnpublishText(
            textId = currentTextID,
            saksbehandlerIdent = "abc"
        )

        testEntityManager.flush()
        testEntityManager.clear()

        assertThrows<TextNotFoundException> { textService.getCurrentTextVersion(currentTextID) }

        maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()
        assertThat(maltekstseksjonVersions).hasSize(2)

        assertThat(maltekstseksjonVersions.find { !it.published }!!.texts).hasSize(1)
        assertThat(maltekstseksjonVersions.find { it.published }!!.texts).hasSize(0)
    }

    @Test
    fun `unpublish text with only maltekstseksjon draft works`() {
        setupDataForOnlyDraftMaltekstseksjon()

        var maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()
        assertThat(maltekstseksjonVersions).hasSize(1)
        assertThat(maltekstseksjonVersions.first().texts).hasSize(1)

        textService.deleteOrUnpublishText(
            textId = currentTextID,
            saksbehandlerIdent = "abc"
        )

        testEntityManager.flush()
        testEntityManager.clear()

        assertThrows<TextNotFoundException> { textService.getCurrentTextVersion(currentTextID) }

        maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()
        assertThat(maltekstseksjonVersions).hasSize(1)
        assertThat(maltekstseksjonVersions.first().texts).hasSize(0)
    }

    @Test
    fun `unpublish text with maltekstseksjon draft and published works`() {
        setupDataForDraftAndPublishedMaltekstseksjon()

        var maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()
        assertThat(maltekstseksjonVersions).hasSize(2)
        assertThat(maltekstseksjonVersions.find { !it.published }!!.texts).hasSize(1)
        assertThat(maltekstseksjonVersions.find { it.published }!!.texts).hasSize(1)

        val result = textService.deleteOrUnpublishText(
            textId = currentTextID,
            saksbehandlerIdent = "abc"
        )

        testEntityManager.flush()
        testEntityManager.clear()

        assertThrows<TextNotFoundException> { textService.getCurrentTextVersion(currentTextID) }

        maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()

        assertThat(maltekstseksjonVersions).hasSize(3)

        assertThat(maltekstseksjonVersions).containsAll(result)

        assertThat(maltekstseksjonVersions.find { !it.published && it.publishedDateTime != null }!!.texts).hasSize(1)
        assertThat(maltekstseksjonVersions.find { it.published }!!.texts).hasSize(0)

        val draft = maltekstseksjonVersions.find { !it.published && it.publishedDateTime == null }
        assertThat(draft!!.texts).hasSize(0)
        assertThat(draft.title).isEqualTo("new title")
        assertThat(draft.utfallIdList).containsExactlyInAnyOrder("1", "2")
    }

    private fun setupDataForPublishedMaltekstseksjon() {
        val now = LocalDateTime.now()

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                created = now,
                modified = now,
                createdBy = "abc",
            )
        )

        val textVersion = textService.createNewText(
            textInput = TextInput(
                title = "",
                textType = "",
                content = null,
                plainText = null,
                version = null,
                utfall = setOf(),
                enheter = setOf(),
                templateSectionList = setOf(),
                ytelseHjemmelList = setOf(),
                utfallIdList = setOf(),
                enhetIdList = setOf(),
                templateSectionIdList = setOf(),
                ytelseHjemmelIdList = setOf(),
            ),
            saksbehandlerIdent = "abc",
        )

        currentTextID = textVersion.text.id

        val maltekstseksjonVersionPublished = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(textVersion.text),
            publishedDateTime = now,
            publishedBy = "noen",
            published = true,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    created = now,
                    modified = now,
                )
            ),
            created = now,
            modified = now,
        )

        testEntityManager.persist(maltekstseksjonVersionPublished)

        textService.publishTextVersion(textId = currentTextID, saksbehandlerIdent = "abc")
        textService.createNewDraft(textId = currentTextID, versionInput = null, saksbehandlerIdent = "abc")

        testEntityManager.flush()
        testEntityManager.clear()
    }

    private fun setupDataForOnlyDraftMaltekstseksjon() {
        val now = LocalDateTime.now()

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                created = now,
                modified = now,
                createdBy = "abc",
            )
        )

        val textVersion = textService.createNewText(
            textInput = TextInput(
                title = "",
                textType = "",
                content = null,
                plainText = null,
                version = null,
                utfall = setOf(),
                enheter = setOf(),
                templateSectionList = setOf(),
                ytelseHjemmelList = setOf(),
                utfallIdList = setOf(),
                enhetIdList = setOf(),
                templateSectionIdList = setOf(),
                ytelseHjemmelIdList = setOf(),
            ),
            saksbehandlerIdent = "abc",
        )

        currentTextID = textVersion.text.id

        val maltekstseksjonVersionDraft = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(textVersion.text),
            publishedDateTime = null,
            publishedBy = null,
            published = false,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    created = now,
                    modified = now,
                )
            ),
            created = now,
            modified = now,
        )

        testEntityManager.persist(maltekstseksjonVersionDraft)

        textService.publishTextVersion(textId = currentTextID, saksbehandlerIdent = "abc")
        textService.createNewDraft(textId = currentTextID, versionInput = null, saksbehandlerIdent = "abc")

        testEntityManager.flush()
        testEntityManager.clear()
    }

    private fun setupDataForDraftAndPublishedMaltekstseksjon() {
        var someDateTime = LocalDateTime.now().minusDays(10)

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                created = someDateTime,
                modified = someDateTime,
                createdBy = "abc",
            )
        )

        val textVersion = textService.createNewText(
            textInput = TextInput(
                title = "",
                textType = "",
                content = null,
                plainText = null,
                version = null,
                utfall = setOf(),
                enheter = setOf(),
                templateSectionList = setOf(),
                ytelseHjemmelList = setOf(),
                utfallIdList = setOf(),
                enhetIdList = setOf(),
                templateSectionIdList = setOf(),
                ytelseHjemmelIdList = setOf(),
            ),
            saksbehandlerIdent = "abc",
        )

        currentTextID = textVersion.text.id

        someDateTime = LocalDateTime.now().minusDays(9)

        val maltekstseksjonVersionPublished = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(textVersion.text),
            publishedDateTime = someDateTime,
            publishedBy = "noen",
            published = true,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    created = someDateTime,
                    modified = someDateTime,
                )
            ),
            created = someDateTime,
            modified = someDateTime,
        )

        someDateTime = LocalDateTime.now().minusDays(8)

        val maltekstseksjonVersionDraft = MaltekstseksjonVersion(
            title = "new title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(textVersion.text),
            publishedDateTime = null,
            publishedBy = null,
            published = false,
            utfallIdList = setOf("1", "2"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    created = someDateTime,
                    modified = someDateTime,
                )
            ),
            created = someDateTime,
            modified = someDateTime,
        )

        testEntityManager.persist(maltekstseksjonVersionPublished)
        testEntityManager.persist(maltekstseksjonVersionDraft)

        textService.publishTextVersion(textId = currentTextID, saksbehandlerIdent = "abc")
        textService.createNewDraft(textId = currentTextID, versionInput = null, saksbehandlerIdent = "abc")

        testEntityManager.flush()
        testEntityManager.clear()
    }

}