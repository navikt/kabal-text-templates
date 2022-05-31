package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Action
import no.nav.klage.texts.domain.ChangelogEntry
import no.nav.klage.texts.domain.Field
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
class TextRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var textRepository: TextRepository

    @Autowired
    lateinit var changelogRepository: ChangelogRepository

    @Test
    fun `add text works`() {
        val now = LocalDateTime.now()

        val text = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            hjemler = setOf("a", "b"),
            created = now,
            modified = now,
        )

        textRepository.save(text)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundText = textRepository.findById(text.id).get()
        assertThat(foundText).isEqualTo(text)
    }

    @Test
    fun `delete text works, and log remains`() {
        val now = LocalDateTime.now()

        val text = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            hjemler = setOf("a", "b"),
            created = now,
            modified = now,
        )

        val logEntry = ChangelogEntry(
            saksbehandlerident = "abc",
            action = Action.NEW,
            field = Field.TEXT,
            fromValue = null,
            toValue = null,
            textId = text.id
        )

        textRepository.save(text)
        changelogRepository.save(logEntry)

        testEntityManager.flush()
        testEntityManager.clear()

        textRepository.deleteById(text.id)

        testEntityManager.flush()
        testEntityManager.clear()

        assertThat(textRepository.findAll()).isEmpty()
        assertThat(changelogRepository.findAll()).hasSize(1)
    }

}
