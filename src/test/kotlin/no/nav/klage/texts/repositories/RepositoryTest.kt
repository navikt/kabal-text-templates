package no.nav.klage.texts.repositories

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
class RepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var textRepository: TextRepository

    @Test
    fun `add text works`() {
        val now = LocalDateTime.now()

        val text = Text(
            title = "title",
            type = "type",
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

}
