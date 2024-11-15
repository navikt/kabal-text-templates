package no.nav.klage.texts.repositories

import io.mockk.mockk
import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.service.MaltekstseksjonService
import no.nav.klage.texts.service.PublishService
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
class MaltekstseksjonServiceTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository

    @Autowired
    lateinit var maltekstseksjonRepository: MaltekstseksjonRepository

    @Autowired
    lateinit var textVersionRepository: TextVersionRepository

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    lateinit var maltekstseksjonService: MaltekstseksjonService

    @BeforeEach
    fun setup() {
        maltekstseksjonService = MaltekstseksjonService(
            maltekstseksjonRepository = maltekstseksjonRepository,
            maltekstseksjonVersionRepository = maltekstseksjonVersionRepository,
            textRepository = mockk(),
            textService = mockk(),
            searchMaltekstseksjonService = mockk(),
            publishService = PublishService(
                maltekstseksjonVersionRepository,
                textVersionRepository = textVersionRepository
            ),
            textVersionRepository = textVersionRepository
        )

        val now = LocalDateTime.now()

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                id = UUID.fromString("368f1610-7463-4688-b069-c07667a86b33"),
                created = now,
                modified = now,
                createdBy = "abc",
                createdByName = "abc",
            )
        )

        val text = testEntityManager.persist(
            Text(
                created = now,
                modified = now,
                maltekstseksjonVersions = mutableListOf(),
                createdBy = "abc",
                createdByName = "abc",
            )
        )

        val maltekstseksjonVersionPublished = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(text),
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
                    name = "saksbehandlerNavn",
                    created = now,
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = now,
            modified = now,
        )

        testEntityManager.persist(maltekstseksjonVersionPublished)

        testEntityManager.flush()
        testEntityManager.clear()
    }

    @Test
    fun `making sure we don't get this again Parameter specified as non-null is null`() {
        maltekstseksjonService.createNewDraft(
            maltekstseksjonId = UUID.fromString("368f1610-7463-4688-b069-c07667a86b33"),
            versionInput = null,
            saksbehandlerIdent = "abc",
            saksbehandlerName = "abc",
        )
    }

}