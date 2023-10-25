package no.nav.klage.texts.repositories

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

    @Test
    fun `add maltekstseksjonVersion works`() {
        val now = LocalDateTime.now()

        val maltekstseksjon = testEntityManager.persist(
            Maltekstseksjon(
                created = now,
                modified = now,
                deleted = false
            )
        )

        val text = testEntityManager.persist(
            Text(
                created = now,
                modified = now,
                deleted = false,
//                maltekstseksjonVersionList = emptyList(),
            )
        )

        val maltekstseksjonVersion = MaltekstseksjonVersion(
            title = "title",
            maltekstseksjon = maltekstseksjon,
            texts = listOf(text),
            publishedDateTime = null,
            publishedBy = null,
            published = false,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = setOf("Navident"),
            created = now,
            modified = now,
        )

        maltekstseksjonVersionRepository.save(maltekstseksjonVersion)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundMaltekstseksjonVersion = maltekstseksjonVersionRepository.findById(maltekstseksjonVersion.id).get()
        assertThat(foundMaltekstseksjonVersion).isEqualTo(maltekstseksjonVersion)
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
