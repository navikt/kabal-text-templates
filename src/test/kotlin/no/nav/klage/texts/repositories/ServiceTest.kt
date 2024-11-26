package no.nav.klage.texts.repositories

import io.mockk.mockk
import no.nav.klage.texts.api.views.TextInput
import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.service.MaltekstseksjonService
import no.nav.klage.texts.service.PublishService
import no.nav.klage.texts.service.TextService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
class ServiceTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var maltekstseksjonRepository: MaltekstseksjonRepository

    @Autowired
    lateinit var maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository

    @Autowired
    lateinit var textVersionRepository: TextVersionRepository

    @Autowired
    lateinit var textRepository: TextRepository

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    lateinit var textService: TextService

    lateinit var maltekstseksjonService: MaltekstseksjonService

    lateinit var currentTextID: UUID

    lateinit var currentMaltekstseksjonID: UUID

    @BeforeEach
    fun setup() {
        textService = TextService(
            textRepository = textRepository,
            textVersionRepository = textVersionRepository,
            maltekstseksjonVersionRepository = maltekstseksjonVersionRepository,
            searchTextService = mockk(),
            publishService = PublishService(
                maltekstseksjonVersionRepository = maltekstseksjonVersionRepository,
                textVersionRepository = textVersionRepository
            ),
        )

        maltekstseksjonService = MaltekstseksjonService(
            maltekstseksjonRepository = maltekstseksjonRepository,
            maltekstseksjonVersionRepository = maltekstseksjonVersionRepository,
            textRepository = textRepository,
            textService = textService,
            filterMaltekstseksjonService = mockk(),
            publishService = PublishService(
                maltekstseksjonVersionRepository = maltekstseksjonVersionRepository,
                textVersionRepository = textVersionRepository
            ),
            textVersionRepository = textVersionRepository
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
        assertThat(maltekstseksjonVersions.first().texts).hasSize(2)

        textService.unpublishText(
            textId = currentTextID,
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

        testEntityManager.flush()
        testEntityManager.clear()

        maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()
        assertThat(maltekstseksjonVersions).hasSize(2)

        assertThat(maltekstseksjonVersions.find { !it.published }!!.texts).hasSize(2)
        assertThat(maltekstseksjonVersions.find { it.published }!!.texts).hasSize(1)
    }

    @Test
    fun `unpublish text with only maltekstseksjon draft works`() {
        setupDataForOnlyDraftMaltekstseksjon()

        var maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()
        assertThat(maltekstseksjonVersions).hasSize(1)
        assertThat(maltekstseksjonVersions.first().texts).hasSize(1)

        textService.unpublishText(
            textId = currentTextID,
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

        testEntityManager.flush()
        testEntityManager.clear()

        maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()
        assertThat(maltekstseksjonVersions).hasSize(1)
        assertThat(maltekstseksjonVersions.first().texts).hasSize(0)
    }

    @Test
    fun `unpublish text with maltekstseksjon draft and published works`() {
        setupDataForDraftAndPublishedMaltekstseksjon()

        var maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()
        assertThat(maltekstseksjonVersions).hasSize(2)
        assertThat(maltekstseksjonVersions.find { !it.published }!!.texts).hasSize(2)
        assertThat(maltekstseksjonVersions.find { it.published }!!.texts).hasSize(2)

        val result = textService.unpublishText(
            textId = currentTextID,
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

        testEntityManager.flush()
        testEntityManager.clear()

        maltekstseksjonVersions = maltekstseksjonVersionRepository.findAll()

        assertThat(maltekstseksjonVersions).hasSize(3)

        assertThat(maltekstseksjonVersions.map { it.maltekstseksjon.id }.toSet())
            .containsAll(result.maltekstseksjonVersions.flatMap { it.maltekstseksjonVersions }.map { it.id }.toSet())

        assertThat(maltekstseksjonVersions.find { !it.published && it.publishedDateTime != null }!!.texts).hasSize(2)
        assertThat(maltekstseksjonVersions.find { it.published }!!.texts).hasSize(1)

        val draft = maltekstseksjonVersions.find { !it.published && it.publishedDateTime == null }
        assertThat(draft!!.texts).hasSize(1)
        assertThat(draft.title).isEqualTo("new title")
        assertThat(draft.utfallIdList).containsExactlyInAnyOrder("1", "2")
    }

    @Test
    fun `make sure updating texts bug does not return (CIRCULAR REFERENCE)`() {
        setupDataForUpdatingTextList()

        val now = LocalDateTime.now()

        val text1 = Text(
            created = now,
            modified = now,
            createdBy = "abc",
            createdByName = "abc",
            maltekstseksjonVersions = mutableListOf()
        )
        val text2 = Text(
            created = now,
            modified = now,
            createdBy = "abc",
            createdByName = "abc",
            maltekstseksjonVersions = mutableListOf()
        )

        textRepository.save(text1)
        textRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        val maltekstseksjonVersion =
            maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(currentMaltekstseksjonID)!!
        maltekstseksjonVersion.texts.add(text1)
        maltekstseksjonVersion.texts.add(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        maltekstseksjonService.updateTexts(
            input = listOf(text2.id.toString(), text1.id.toString()),
            maltekstseksjonId = currentMaltekstseksjonID,
            saksbehandlerIdent = "bac",
            saksbehandlerName = "abc",
        )

        testEntityManager.flush()
        testEntityManager.clear()

        assertThat(
            maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
                currentMaltekstseksjonID
            )!!.texts
        ).containsExactly(text2, text1)
    }

    private fun setupDataForPublishedMaltekstseksjon() {
        val now = LocalDateTime.now()

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                created = now,
                modified = now,
                createdBy = "abc",
                createdByName = "abc",
            )
        )

        val textVersion1 = textService.createNewText(
            textInput = TextInput(
                title = "",
                textType = "",
                richText = null,
                plainText = null,
                version = null,
                utfallIdList = setOf(),
                enhetIdList = setOf(),
                templateSectionIdList = setOf(),
                ytelseHjemmelIdList = setOf(),
            ),
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

        val textVersion2 = textService.createNewText(
            textInput = TextInput(
                title = "",
                textType = "",
                richText = null,
                plainText = null,
                version = null,
                utfallIdList = setOf(),
                enhetIdList = setOf(),
                templateSectionIdList = setOf(),
                ytelseHjemmelIdList = setOf(),
            ),
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

        textService.publishTextVersion(textId = textVersion2.id, saksbehandlerIdent = "abc", saksbehandlerName = "abc")

        currentTextID = textVersion1.id

        val text1 = textRepository.findById(textVersion1.id).get()
        val text2 = textRepository.findById(textVersion2.id).get()

        val maltekstseksjonVersionPublished = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(text1, text2),
            publishedDateTime = now,
            publishedBy = "noen",
            publishedByName = "noen",
            published = true,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    name = "saksbehandlerName",
                    created = now,
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = now,
            modified = now,
        )

        testEntityManager.persist(maltekstseksjonVersionPublished)

        textService.publishTextVersion(textId = currentTextID, saksbehandlerIdent = "abc", saksbehandlerName = "abc")
        textService.createNewDraft(
            textId = currentTextID,
            versionInput = null,
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc"
        )

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
                createdByName = "abc",
            )
        )

        val textVersion = textService.createNewText(
            textInput = TextInput(
                title = "",
                textType = "",
                version = null,
                richText = null,
                plainText = null,
                utfallIdList = setOf(),
                enhetIdList = setOf(),
                templateSectionIdList = setOf(),
                ytelseHjemmelIdList = setOf(),
            ),
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

        currentTextID = textVersion.id

        val text = textRepository.findById(textVersion.id).get()


        val maltekstseksjonVersionDraft = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(text),
            publishedDateTime = null,
            publishedBy = null,
            publishedByName = null,
            published = false,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    name = "saksbehandlerName",
                    created = now,
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = now,
            modified = now,
        )

        testEntityManager.persist(maltekstseksjonVersionDraft)

        textService.publishTextVersion(textId = currentTextID, saksbehandlerIdent = "abc", saksbehandlerName = "abc")
        textService.createNewDraft(
            textId = currentTextID,
            versionInput = null,
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

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
                createdByName = "abc",
            )
        )

        val textVersion1 = textService.createNewText(
            textInput = TextInput(
                title = "",
                textType = "",
                richText = null,
                plainText = null,
                version = null,
                utfallIdList = setOf(),
                enhetIdList = setOf(),
                templateSectionIdList = setOf(),
                ytelseHjemmelIdList = setOf(),
            ),
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

        val textVersion2 = textService.createNewText(
            textInput = TextInput(
                title = "",
                textType = "",
                richText = null,
                plainText = null,
                version = null,
                utfallIdList = setOf(),
                enhetIdList = setOf(),
                templateSectionIdList = setOf(),
                ytelseHjemmelIdList = setOf(),
            ),
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

        textService.publishTextVersion(textId = textVersion2.id, saksbehandlerIdent = "abc", saksbehandlerName = "abc")

        currentTextID = textVersion1.id

        someDateTime = LocalDateTime.now().minusDays(9)

        val text1 = textRepository.findById(textVersion1.id).get()
        val text2 = textRepository.findById(textVersion2.id).get()

        val maltekstseksjonVersionPublished = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(text1, text2),
            publishedDateTime = someDateTime,
            publishedBy = "noen",
            publishedByName = "noen",
            published = true,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    name = "saksbehandlerName",
                    created = someDateTime,
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = someDateTime,
            modified = someDateTime,
        )

        someDateTime = LocalDateTime.now().minusDays(8)

        val maltekstseksjonVersionDraft = MaltekstseksjonVersion(
            title = "new title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(text1, text2),
            publishedDateTime = null,
            publishedBy = null,
            publishedByName = null,
            published = false,
            utfallIdList = setOf("1", "2"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    name = "saksbehandlerName",
                    created = someDateTime,
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = someDateTime,
            modified = someDateTime,
        )

        testEntityManager.persist(maltekstseksjonVersionPublished)
        testEntityManager.persist(maltekstseksjonVersionDraft)

        textService.publishTextVersion(textId = currentTextID, saksbehandlerIdent = "abc", saksbehandlerName = "abc")
        textService.createNewDraft(
            textId = currentTextID,
            versionInput = null,
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )

        testEntityManager.flush()
        testEntityManager.clear()
    }

    private fun setupDataForUpdatingTextList() {
        var someDateTime = LocalDateTime.now().minusDays(10)

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                created = someDateTime,
                modified = someDateTime,
                createdBy = "abc",
                createdByName = "abc"
            )
        )

        currentMaltekstseksjonID = maltekstseksjon.id

        someDateTime = LocalDateTime.now().minusDays(9)

        val maltekstseksjonVersionPublished = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(),
            publishedDateTime = someDateTime,
            publishedBy = "noen",
            publishedByName = "noen",
            published = true,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    name = "saksbehandlerName",
                    created = someDateTime,
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = someDateTime,
            modified = someDateTime,
        )

        someDateTime = LocalDateTime.now().minusDays(8)

        val maltekstseksjonVersionDraft = MaltekstseksjonVersion(
            title = "new title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(),
            publishedDateTime = null,
            publishedBy = null,
            publishedByName = null,
            published = false,
            utfallIdList = setOf("1", "2"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    name = "saksbehandlerName",
                    created = someDateTime,
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = someDateTime,
            modified = someDateTime,
        )

        testEntityManager.persist(maltekstseksjonVersionPublished)
        testEntityManager.persist(maltekstseksjonVersionDraft)

        testEntityManager.flush()
        testEntityManager.clear()
    }

}