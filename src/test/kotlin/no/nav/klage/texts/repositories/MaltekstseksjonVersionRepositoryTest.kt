package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.Text
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MaltekstseksjonVersionRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository

    @Autowired
    lateinit var textRepository: TextRepository

    @Test
    fun `add maltekstseksjonVersion works`() {
        val now = LocalDateTime.now()

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                created = now,
                modified = now,
                createdBy = "abc",
            )
        )

        val text = textRepository.save(
            Text(
                created = now,
                modified = now,
                maltekstseksjonVersions = mutableListOf(),
                createdBy = "abc",
            )
        )

        val maltekstseksjonVersion = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(text),
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
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = now,
            modified = now,
        )

        maltekstseksjonVersionRepository.save(maltekstseksjonVersion)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundMaltekstseksjonVersion = maltekstseksjonVersionRepository.getReferenceById(maltekstseksjonVersion.id)
        assertThat(foundMaltekstseksjonVersion).isEqualTo(maltekstseksjonVersion)
    }

    @Test
    fun `delete text also removes from maltekstseksjonVersion works`() {
        val now = LocalDateTime.now()

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                created = now,
                modified = now,
                createdBy = "abc",
            )
        )

        val text = testEntityManager.persist(
            Text(
                created = now,
                modified = now,
                maltekstseksjonVersions = mutableListOf(),
                createdBy = "abc",
            )
        )

        val maltekstseksjonVersion = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(text),
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
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = now,
            modified = now,
        )

        maltekstseksjonVersionRepository.save(maltekstseksjonVersion)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundMaltekstseksjonVersion = maltekstseksjonVersionRepository.getReferenceById(maltekstseksjonVersion.id)

        assertThat(foundMaltekstseksjonVersion.texts).hasSize(1)

        val textFetched = textRepository.getReferenceById(text.id)

        textFetched.maltekstseksjonVersions.forEach { mv ->
            mv.texts.removeIf { it.id == textFetched.id }
        }

        testEntityManager.flush()
        testEntityManager.clear()

        foundMaltekstseksjonVersion = maltekstseksjonVersionRepository.getReferenceById(maltekstseksjonVersion.id)

        assertThat(foundMaltekstseksjonVersion.texts).isEmpty()
    }

    @Test
    fun `find published bulk works`() {
        val now = LocalDateTime.now()

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                created = now,
                modified = now,
                createdBy = "abc",
            )
        )

        val text = textRepository.save(
            Text(
                created = now,
                modified = now,
                maltekstseksjonVersions = mutableListOf(),
                createdBy = "abc",
            )
        )

        val maltekstseksjonVersion = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = mutableListOf(text),
            publishedDateTime = now,
            publishedBy = "null",
            published = true,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    created = now,
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
                )
            ),
            created = now,
            modified = now,
        )

        maltekstseksjonVersionRepository.save(maltekstseksjonVersion)

        testEntityManager.flush()
        testEntityManager.clear()

        val bulk = maltekstseksjonVersionRepository.findConnectedMaltekstseksjonPublishedIdListBulk(listOf(text.id))

        assertThat(bulk.first().first()).isEqualTo(maltekstseksjon.id)
        assertThat(bulk.first().last()).isEqualTo(text.id)
    }

}