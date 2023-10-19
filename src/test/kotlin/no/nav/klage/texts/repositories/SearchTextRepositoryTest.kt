package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text
import org.assertj.core.api.Assertions.assertThat
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
            utfallIdList = setOf("ua", "ub1"),
            enhetIdList = setOf("ea", "eb1"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type2",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf("ua", "ub2"),
            enhetIdList = setOf("ea", "eb2"),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextRepository.searchTexts(
            textType = "type1",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type2",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2)

        foundTexts = searchTextRepository.searchTexts(
            textType = null,
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
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
            utfallIdList = setOf("ua", "ub1"),
            enhetIdList = setOf("ea", "eb1"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf("ua", "ub2"),
            enhetIdList = setOf("ea", "eb2"),
            created = now,
            modified = now,
        )

        val text3 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf("ua", "ub3"),
            enhetIdList = setOf("ea", "eb3"),
            created = now,
            modified = now,
        )

        val text4 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            enhetIdList = setOf("ea", "eb4"),
            templateSectionIdList = setOf("ts4"),
            created = now,
            modified = now,
        )

        val text5 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf("zzz"),
            enhetIdList = setOf(),
            templateSectionIdList = setOf("ta>sa"),
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
            utfallIdList = listOf("ua"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("ts4"),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4, text5)

        foundTexts = searchTextRepository.searchTexts(
            textType = null,
            utfallIdList = listOf("ub1", "ub2"),
            enhetIdList = listOf("eb1", "eb2"),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua"),
            enhetIdList = listOf("ea"),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("ta>sa"),
            ytelseHjemmelIdList = listOf(),
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
            utfallIdList = listOf("1"),
            enhetIdList = listOf("4250"),
            templateSectionIdList = listOf("noe"),
            ytelseHjemmelIdList = listOf("annet"),
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
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            templateSectionIdList = setOf("abc"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            created = now,
            modified = now,
        )

        val text3 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf(),
            enhetIdList = setOf(),
            created = now,
            modified = now,
        )

        val text4 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf(),
            enhetIdList = setOf(),
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
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("noe"),
            ytelseHjemmelIdList = listOf(),
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
            utfallIdList = setOf("ua1:ua2", "ub1:ub2"),
            enhetIdList = setOf("ea", "eb1"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf("ua1", "ub2"),
            enhetIdList = setOf("ea", "eb2"),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua1:ua2"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua2:ua1"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
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
            utfallIdList = setOf("ua1:ua2", "ub1:ub2"),
            enhetIdList = setOf("ea", "eb1"),
            templateSectionIdList = setOf("vedtak>tittel"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf("ua1", "ub2"),
            enhetIdList = setOf("ea", "eb2"),
            created = now,
            modified = now,
        )

        val text3 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf("ua1", "ub2"),
            enhetIdList = setOf("ea", "eb2"),
            templateSectionIdList = setOf("anke>head"),
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
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("vedtak>*"),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextRepository.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("*>tittel"),
            ytelseHjemmelIdList = listOf(),
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
            utfallIdList = setOf("ua", "ub1"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type2",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            ytelseHjemmelIdList = setOf("abc>cde"),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextRepository.searchTexts(
            textType = null,
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf("NONE"),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1)

        foundTexts = searchTextRepository.searchTexts(
            textType = null,
            utfallIdList = listOf("NONE"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2)
    }

}
