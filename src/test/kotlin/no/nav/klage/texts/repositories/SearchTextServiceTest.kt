package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.service.SearchTextService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SearchTextServiceTest {

    private val searchTextService = SearchTextService()

    private fun createTextVersion(): TextVersion {
        val now = LocalDateTime.now()

        return TextVersion(
            title = "",
            textType = "",
            richTextNN = null,
            richTextNB = null,
            richTextUntranslated = null,
            plainTextNN = null,
            plainTextNB = null,
            smartEditorVersion = null,
            text = Text(
                created = now,
                modified = now,
                createdBy = "abc",
                maltekstseksjonVersions = mutableListOf()
            ),
            publishedDateTime = null,
            publishedBy = null,
            published = false,
            created = now,
            modified = now,
        )
    }

    @Test
    fun `search textVersion with type works`() {
        val text1 = createTextVersion().apply {
            textType = "type1"
        }

        val text2 = createTextVersion().apply {
            textType = "type2"
        }

        val texts = listOf(text1, text2)

        var foundTexts = searchTextService.searchTexts(
            textType = "type1",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1)

        foundTexts = searchTextService.searchTexts(
            textType = "type2",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2)

        foundTexts = searchTextService.searchTexts(
            textType = null,
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `search textVersion works`() {
        val text1 = createTextVersion().apply {
            textType = "type"
            utfallIdList = setOf("ua", "ub1")
            enhetIdList = setOf("ea", "eb1")
        }

        val text2 = createTextVersion().apply {
            textType = "type"
            utfallIdList = setOf("ua", "ub2")
            enhetIdList = setOf("ea", "eb2")
        }

        val text3 = createTextVersion().apply {
            textType = "type"
            utfallIdList = setOf("ua", "ub3")
            enhetIdList = setOf("ea", "eb3")
        }

        val text4 = createTextVersion().apply {
            textType = "type"
            enhetIdList = setOf("ea", "eb4")
            templateSectionIdList = setOf("ts4")
        }

        val text5 = createTextVersion().apply {
            textType = "type"
            utfallIdList = setOf("zzz")
            templateSectionIdList = setOf("ta>sa")
        }

        val texts = listOf(text1, text2, text3, text4, text5)

        var foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("ts4"),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4, text5)

        foundTexts = searchTextService.searchTexts(
            textType = null,
            utfallIdList = listOf("ub1", "ub2"),
            enhetIdList = listOf("eb1", "eb2"),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua"),
            enhetIdList = listOf("ea"),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("ta>sa"),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text5)
    }

    @Test
    fun `texts with no specifications are considered a 'hit'`() {
        val text1 = createTextVersion().apply {
            textType = "type"
        }

        val text2 = createTextVersion().apply {
            textType = "type"
        }

        val foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf("1"),
            enhetIdList = listOf("4250"),
            templateSectionIdList = listOf("noe"),
            ytelseHjemmelIdList = listOf("annet"),
            texts = listOf(text1, text2)
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `texts with no specifications are considered a 'hit' 2`() {
        val text1 = createTextVersion().apply {
            textType = "type"
            templateSectionIdList = setOf("abc")
        }

        val text2 = createTextVersion().apply {
            textType = "type"
        }

        val text3 = createTextVersion().apply {
            textType = "type"
        }

        val text4 = createTextVersion().apply {
            textType = "type"
        }

        val foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("noe"),
            ytelseHjemmelIdList = listOf(),
            texts = listOf(text1, text2, text3, text4),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2, text3, text4)
    }

    @Test
    fun `search textVersion with sets of utfall works`() {
        val now = LocalDateTime.now()

        val text1 = createTextVersion().apply {
            textType = "type"
            utfallIdList = setOf("ua1:ua2", "ub1:ub2")
            enhetIdList = setOf("ea", "eb1")
        }

        val text2 = createTextVersion().apply {
            textType = "type"
            utfallIdList = setOf("ua1", "ub2")
            enhetIdList = setOf("ea", "eb2")
        }

        val texts = listOf(text1, text2)

        var foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua1:ua2"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf("ua2:ua1"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `search textVersion with wildcard works`() {
        val text1 = createTextVersion().apply {
            textType = "type"
            utfallIdList = setOf("ua1:ua2", "ub1:ub2")
            enhetIdList = setOf("ea", "eb1")
            templateSectionIdList = setOf("vedtak>tittel")
        }

        val text2 = createTextVersion().apply {
            textType = "type"
            utfallIdList = setOf("ua1", "ub2")
            enhetIdList = setOf("ea", "eb2")
        }

        val text3 = createTextVersion().apply {
            textType = "type"
            utfallIdList = setOf("ua1", "ub2")
            enhetIdList = setOf("ea", "eb2")
            templateSectionIdList = setOf("anke>head")
        }

        val texts = listOf(text1, text2, text3)

        var foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("vedtak>*"),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = searchTextService.searchTexts(
            textType = "type",
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("*>tittel"),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)
    }

    @Test
    fun `search textVersion with NONE works`() {
        val text1 = createTextVersion().apply {
            utfallIdList = setOf("ua", "ub1")
        }

        val text2 = createTextVersion().apply {
            ytelseHjemmelIdList = setOf("abc>cde")
        }

        val texts = listOf(text1, text2)

        var foundTexts = searchTextService.searchTexts(
            textType = null,
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf("NONE"),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1)

        foundTexts = searchTextService.searchTexts(
            textType = null,
            utfallIdList = listOf("NONE"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            texts = texts,
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2)
    }

}
