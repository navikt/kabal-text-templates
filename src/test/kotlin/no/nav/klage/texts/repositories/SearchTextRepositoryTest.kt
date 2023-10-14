package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
class SearchTextRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var textRepository: TextRepository

    lateinit var searchTextRepository: SearchTextRepository

    @BeforeEach
    fun before() {
        searchTextRepository = SearchTextRepository(textRepository)
    }

    @Test
    fun `search text with type works`() {
        val now = LocalDateTime.now()

        val text1 = Text(
            title = "title",
            textType = "type1",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua", "ub1"),
            enheter = setOf("ea", "eb1"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type2",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua", "ub2"),
            enheter = setOf("ea", "eb2"),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextRepository.searchTexts(
            textType = "type1",
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type2",
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2)

        foundTexts = searchTextRepository.searchTexts(
            textType = null,
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `search text works`() {
        val now = LocalDateTime.now()

        val text1 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua", "ub1"),
            enheter = setOf("ea", "eb1"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua", "ub2"),
            enheter = setOf("ea", "eb2"),
            created = now,
            modified = now,
        )

        val text3 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua", "ub3"),
            enheter = setOf("ea", "eb3"),
            created = now,
            modified = now,
        )

        val text4 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            enheter = setOf("ea", "eb4"),
            templateSectionList = setOf("ts4"),
            created = now,
            modified = now,
        )

        val text5 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("zzz"),
            enheter = setOf(),
            templateSectionList = setOf("ta>sa"),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)
        textRepository.save(text3)
        textRepository.save(text4)
        textRepository.save(text5)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf("ua"),
            enheter = listOf(),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf("ts4"),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4, text5)

        foundTexts = searchTextRepository.searchTexts(
            textType = null,
            utfall = listOf("ub1", "ub2"),
            enheter = listOf("eb1", "eb2"),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf("ua"),
            enheter = listOf("ea"),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf("ta>sa"),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text5)
    }

    @Test
    fun `texts with no specifications are considered a 'hit'`() {
        val now = LocalDateTime.now()

        val text1 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf("1"),
            enheter = listOf("4250"),
            templateSectionList = listOf("noe"),
            ytelseHjemmelList = listOf("annet"),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `texts with no specifications are considered a 'hit' 2`() {
        val now = LocalDateTime.now()

        val text1 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf(),
            enheter = setOf(),
            templateSectionList = setOf("abc"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf(),
            enheter = setOf(),
            created = now,
            modified = now,
        )

        val text3 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf(),
            enheter = setOf(),
            created = now,
            modified = now,
        )

        val text4 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf(),
            enheter = setOf(),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)
        textRepository.save(text3)
        textRepository.save(text4)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf("noe"),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2, text3, text4)
    }

    @Test
    fun `search text with sets of utfall works`() {
        val now = LocalDateTime.now()

        val text1 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua1:ua2", "ub1:ub2"),
            enheter = setOf("ea", "eb1"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua1", "ub2"),
            enheter = setOf("ea", "eb2"),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf("ua1:ua2"),
            enheter = listOf(),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf("ua2:ua1"),
            enheter = listOf(),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `search text with wildcard works`() {
        val now = LocalDateTime.now()

        val text1 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua1:ua2", "ub1:ub2"),
            enheter = setOf("ea", "eb1"),
            templateSectionList = setOf("vedtak>tittel"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua1", "ub2"),
            enheter = setOf("ea", "eb2"),
            created = now,
            modified = now,
        )

        val text3 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua1", "ub2"),
            enheter = setOf("ea", "eb2"),
            templateSectionList = setOf("anke>head"),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)
        textRepository.save(text3)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf("vedtak>*"),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf("*>tittel"),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `search text with NONE works`() {
        val now = LocalDateTime.now()

        val text1 = Text(
            title = "title",
            textType = "type1",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfall = setOf("ua", "ub1"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type2",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            ytelseHjemmelList = setOf("abc>cde"),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextRepository.searchTexts(
            textType = null,
            utfall = listOf(),
            enheter = listOf(),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf("NONE"),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1)

        foundTexts = searchTextRepository.searchTexts(
            textType = null,
            utfall = listOf("NONE"),
            enheter = listOf(),
            templateSectionList = listOf(),
            ytelseHjemmelList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2)
    }

}
