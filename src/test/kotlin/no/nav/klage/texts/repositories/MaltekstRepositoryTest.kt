package no.nav.klage.texts.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

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
    lateinit var maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository

    @Autowired
    lateinit var textVersionRepository: TextVersionRepository
/*
    @Test
    fun `add maltekstseksjon works`() {
        val now = LocalDateTime.now()

        val maltekstseksjonVersion = MaltekstseksjonVersion(
            title = "title",
            texts = listOf(),
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            templateSectionIdList = setOf(),
            ytelseHjemmelIdList = setOf(),
            created = now,
            modified = now,
        )

        maltekstRepository.save(maltekstseksjonVersion)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundMaltekst = maltekstRepository.findById(maltekstseksjonVersion.id).get()
        assertThat(foundMaltekst).isEqualTo(maltekstseksjonVersion)
    }

    @Test
    fun `findAll works`() {
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

        textVersionRepository.save(textVersion)

        testEntityManager.flush()
        testEntityManager.clear()

        val maltekstseksjonVersion = MaltekstseksjonVersion(
            title = "title",
            texts = listOf(textVersion),
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            templateSectionIdList = setOf(),
            ytelseHjemmelIdList = setOf(),
            created = now,
            modified = now,
        )

        val maltekst2 = MaltekstseksjonVersion(
            title = "title",
            texts = listOf(),
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            templateSectionIdList = setOf(),
            ytelseHjemmelIdList = setOf(),
            created = now,
            modified = now,
        )

        maltekstRepository.save(maltekstseksjonVersion)
        maltekstRepository.save(maltekst2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundMaltekster = maltekstRepository.findAll()
        assertThat(foundMaltekster).hasSize(2)
    }

    @Test
    fun `order of texts work`() {
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

        val text2 = TextVersion(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            created = now,
            modified = now,
        )

        val text3 = TextVersion(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            created = now,
            modified = now,
        )

        textVersionRepository.save(textVersion)
        textVersionRepository.save(text2)
        textVersionRepository.save(text3)

        testEntityManager.flush()
        testEntityManager.clear()

        val maltekstseksjonVersion = MaltekstseksjonVersion(
            title = "title",
            texts = listOf(text2, textVersion, text3),
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            templateSectionIdList = setOf(),
            ytelseHjemmelIdList = setOf(),
            created = now,
            modified = now,
        )

        maltekstRepository.save(maltekstseksjonVersion)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundMaltekster = maltekstRepository.findAll()
        assertThat(foundMaltekster).hasSize(1)

        assertThat(foundMaltekster.first().texts).containsExactly(text2, textVersion, text3)
    }
*/
}
