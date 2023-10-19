package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Maltekst
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
class MaltekstRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var maltekstRepository: MaltekstRepository

    @Test
    fun `add maltekst works`() {
        val now = LocalDateTime.now()

        val maltekst = Maltekst(
            title = "title",
            texts = setOf(),
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            templateSectionIdList = setOf(),
            ytelseHjemmelIdList = setOf(),
            created = now,
            modified = now,
        )

        maltekstRepository.save(maltekst)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundMaltekst = maltekstRepository.findById(maltekst.id).get()
        assertThat(foundMaltekst).isEqualTo(maltekst)
    }

    @Test
    fun `findAll works`() {
        val now = LocalDateTime.now()

        val maltekst = Maltekst(
            title = "title",
            texts = setOf(),
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            templateSectionIdList = setOf(),
            ytelseHjemmelIdList = setOf(),
            created = now,
            modified = now,
        )

        val maltekst2 = Maltekst(
            title = "title",
            texts = setOf(),
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            templateSectionIdList = setOf(),
            ytelseHjemmelIdList = setOf(),
            created = now,
            modified = now,
        )

        maltekstRepository.save(maltekst)
        maltekstRepository.save(maltekst2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundMaltekster = maltekstRepository.findAll()
        assertThat(foundMaltekster).hasSize(2)
    }

}


