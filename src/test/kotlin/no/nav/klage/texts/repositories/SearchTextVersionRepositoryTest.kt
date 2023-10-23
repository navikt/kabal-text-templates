package no.nav.klage.texts.repositories

import org.junit.jupiter.api.BeforeEach
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
class SearchTextVersionRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var textVersionRepository: TextVersionRepository

    lateinit var searchTextService: SearchTextService

    @BeforeEach
    fun before() {
        searchTextService = SearchTextService(textVersionRepository)
    }
/*
    @Test
    fun `search textVersion with type works`() {
        val now = LocalDateTime.now()

        val text1 = TextVersion(
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

        val text2 = TextVersion(
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

        textVersionRepository.save(text1)
        textVersionRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextService.searchTexts(
            textType = "type1",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1)

        foundTexts = searchTextService.searchTexts(
            textType = "type2",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2)

        foundTexts = searchTextService.searchTexts(
            textType = null,
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `search textVersion works`() {
        val now = LocalDateTime.now()

        val text1 = TextVersion(
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

        val text2 = TextVersion(
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

        val text3 = TextVersion(
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

        val text4 = TextVersion(
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

        val text5 = TextVersion(
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

        textVersionRepository.save(text1)
        textVersionRepository.save(text2)
        textVersionRepository.save(text3)
        textVersionRepository.save(text4)
        textVersionRepository.save(text5)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("ts4"),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4, text5)

        foundTexts = searchTextService.searchTexts(
            textType = null,
            utfallIdList = listOf("ub1", "ub2"),
            enhetIdList = listOf("eb1", "eb2"),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua"),
            enhetIdList = listOf("ea"),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextService.searchTexts(
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

        val text1 = TextVersion(
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

        textVersionRepository.save(text1)
        textVersionRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundTexts = searchTextService.searchTexts(
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

        val text1 = TextVersion(
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

        val text2 = TextVersion(
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

        val text3 = TextVersion(
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

        val text4 = TextVersion(
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

        textVersionRepository.save(text1)
        textVersionRepository.save(text2)
        textVersionRepository.save(text3)
        textVersionRepository.save(text4)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("noe"),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2, text3, text4)
    }

    @Test
    fun `search textVersion with sets of utfall works`() {
        val now = LocalDateTime.now()

        val text1 = TextVersion(
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

        val text2 = TextVersion(
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

        textVersionRepository.save(text1)
        textVersionRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua1:ua2"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua2:ua1"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `search textVersion with wildcard works`() {
        val now = LocalDateTime.now()

        val text1 = TextVersion(
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

        val text2 = TextVersion(
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

        val text3 = TextVersion(
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

        textVersionRepository.save(text1)
        textVersionRepository.save(text2)
        textVersionRepository.save(text3)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("vedtak>*"),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("*>tittel"),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `search textVersion with NONE works`() {
        val now = LocalDateTime.now()

        val text1 = TextVersion(
            title = "title",
            textType = "type1",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            utfallIdList = setOf("ua", "ub1"),
            created = now,
            modified = now,
        )

        val text2 = TextVersion(
            title = "title",
            textType = "type2",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            ytelseHjemmelIdList = setOf("abc>cde"),
            created = now,
            modified = now,
        )

        textVersionRepository.save(text1)
        textVersionRepository.save(text2)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = searchTextService.searchTexts(
            textType = null,
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf("NONE"),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1)

        foundTexts = searchTextService.searchTexts(
            textType = null,
            utfallIdList = listOf("NONE"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2)
    }
*/
}
