package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.domain.TextVersion
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
class TextVersionRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var textVersionRepository: TextVersionRepository

    @Test
    fun `add textVersion works`() {
        val now = LocalDateTime.now()

        val text = testEntityManager.persist(
            Text(
                created = now,
                modified = now,
                deleted = false,
                maltekstseksjonVersionList = emptyList(),
            )
        )

        val textVersion = TextVersion(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            publishedDateTime = null,
            publishedBy = null,
            published = false,
            text = text,
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

        textVersionRepository.save(textVersion)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundText = textVersionRepository.findById(textVersion.id).get()
        assertThat(foundText).isEqualTo(textVersion)
    }
    /*
    @Test
    fun `delete textVersion works, and log remains`() {
        val now = LocalDateTime.now()

        val textVersion = TextVersion(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            created = now,
            modified = now,
        )

        val logEntry = ChangelogEntry(
            saksbehandlerident = "abc",
            action = Action.NEW,
            field = Field.TEXT,
            fromValue = null,
            toValue = null,
            textId = textVersion.id
        )

        textVersionRepository.save(textVersion)
        changelogRepository.save(logEntry)

        testEntityManager.flush()
        testEntityManager.clear()

        textVersionRepository.deleteById(textVersion.id)

        testEntityManager.flush()
        testEntityManager.clear()

        assertThat(textVersionRepository.findAll()).isEmpty()
        assertThat(changelogRepository.findAll()).hasSize(1)
    }
*/
}
