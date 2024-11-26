package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.service.FilterMaltekstseksjonService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FilterMaltekstseksjonServiceTest {

    private val filterMaltekstseksjonService = FilterMaltekstseksjonService()

    private fun createMaltekstseksjonVersion(): MaltekstseksjonVersion {
        val now = LocalDateTime.now()

        return MaltekstseksjonVersion(
            title = "",
            maltekstseksjon = Maltekstseksjon(
                created = now,
                modified = now,
                createdBy = "abc",
                createdByName = "abc",
            ),
            publishedDateTime = null,
            publishedBy = null,
            publishedByName = null,
            published = false,
            created = now,
            modified = now,
        )
    }

    @Test
    fun `search maltekstseksjonVersion works`() {
        val maltekstseksjon1 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("ua", "ub1")
            enhetIdList = setOf("ea", "eb1")
        }

        val maltekstseksjon2 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("ua", "ub2")
            enhetIdList = setOf("ea", "eb2")
        }

        val maltekstseksjon3 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("ua", "ub3")
            enhetIdList = setOf("ea", "eb3")
        }

        val maltekstseksjon4 = createMaltekstseksjonVersion().apply {
            enhetIdList = setOf("ea", "eb4")
            templateSectionIdList = setOf("ts4")
        }

        val maltekstseksjon5 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("zzz")
            templateSectionIdList = setOf("ta>sa")
        }

        val maltekstseksjonVersions = listOf(maltekstseksjon1, maltekstseksjon2, maltekstseksjon3, maltekstseksjon4, maltekstseksjon5)

        var foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            utfallIdList = listOf("ua"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            textIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2, maltekstseksjon3, maltekstseksjon4)

        foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("ts4"),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2, maltekstseksjon3, maltekstseksjon4)

        foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2, maltekstseksjon3, maltekstseksjon4, maltekstseksjon5)

        foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf("ub1", "ub2"),
            enhetIdList = listOf("eb1", "eb2"),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2)

        foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf("ua"),
            enhetIdList = listOf("ea"),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2, maltekstseksjon3, maltekstseksjon4)

        foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("ta>sa"),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2, maltekstseksjon3, maltekstseksjon5)
    }

    @Test
    fun `maltekstseksjonVersions with no specifications are considered a 'hit'`() {
        val maltekstseksjon1 = createMaltekstseksjonVersion()
        val maltekstseksjon2 = createMaltekstseksjonVersion()

        val foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf("1"),
            enhetIdList = listOf("4250"),
            templateSectionIdList = listOf("noe"),
            ytelseHjemmelIdList = listOf("annet"),
            maltekstseksjonVersions = listOf(maltekstseksjon1, maltekstseksjon2)
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2)
    }

    @Test
    fun `maltekstseksjonVersions with no specifications are considered a 'hit' 2`() {
        val maltekstseksjon1 = createMaltekstseksjonVersion().apply {
            templateSectionIdList = setOf("abc")
        }

        val maltekstseksjon2 = createMaltekstseksjonVersion()
        val maltekstseksjon3 = createMaltekstseksjonVersion()
        val maltekstseksjon4 = createMaltekstseksjonVersion()

        val foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("noe"),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = listOf(maltekstseksjon1, maltekstseksjon2, maltekstseksjon3, maltekstseksjon4),
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon2, maltekstseksjon3, maltekstseksjon4)
    }

    @Test
    fun `search maltekstseksjonVersion with texts works`() {
        val texts = (0 until 4).toList().map {
            getText()
        }

        val maltekstseksjon1 = createMaltekstseksjonVersion().apply {
            this.texts.addAll(listOf(texts[0], texts[1], texts[2], texts[3]))
        }

        val maltekstseksjon2 = createMaltekstseksjonVersion().apply {
            this.texts.addAll(listOf(texts[0], texts[3]))
        }

        val maltekstseksjonVersions = listOf(maltekstseksjon1, maltekstseksjon2)

        var foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf("${texts[0].id}", "${texts[1].id}"),
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2)

        foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf("${texts[1].id}", "${texts[0].id}"),
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2)
    }

    @Test
    fun `search maltekstseksjonVersion with sets of utfall works`() {
        val maltekstseksjon1 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("ua1:ua2", "ub1:ub2")
            enhetIdList = setOf("ea", "eb1")
        }

        val maltekstseksjon2 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("ua1", "ub2")
            enhetIdList = setOf("ea", "eb2")
        }

        val maltekstseksjonVersions = listOf(maltekstseksjon1, maltekstseksjon2)

        var foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf("ua1:ua2"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2)

        foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf("ua2:ua1"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2)
    }

    @Test
    fun `search maltekstseksjonVersion with wildcard works`() {
        val maltekstseksjon1 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("ua1:ua2", "ub1:ub2")
            enhetIdList = setOf("ea", "eb1")
            templateSectionIdList = setOf("vedtak>tittel")
        }

        val maltekstseksjon2 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("ua1", "ub2")
            enhetIdList = setOf("ea", "eb2")
        }

        val maltekstseksjon3 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("ua1", "ub2")
            enhetIdList = setOf("ea", "eb2")
            templateSectionIdList = setOf("anke>head")
        }

        val maltekstseksjonVersions = listOf(maltekstseksjon1, maltekstseksjon2, maltekstseksjon3)

        var foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("vedtak>*"),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2)

        foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf("*>tittel"),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1, maltekstseksjon2)
    }

    @Test
    fun `search maltekstseksjonVersion with NONE works`() {
        val maltekstseksjon1 = createMaltekstseksjonVersion().apply {
            utfallIdList = setOf("ua", "ub1")
        }

        val maltekstseksjon2 = createMaltekstseksjonVersion().apply {
            ytelseHjemmelIdList = setOf("abc>cde")
        }

        val maltekstseksjonVersions = listOf(maltekstseksjon1, maltekstseksjon2)

        var foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf(),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf("NONE"),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon1)

        foundMaltekstseksjonVersions = filterMaltekstseksjonService.filterMaltekstseksjoner(
            textIdList = listOf(),
            utfallIdList = listOf("NONE"),
            enhetIdList = listOf(),
            templateSectionIdList = listOf(),
            ytelseHjemmelIdList = listOf(),
            maltekstseksjonVersions = maltekstseksjonVersions,
        )
        assertThat(foundMaltekstseksjonVersions).containsExactlyInAnyOrder(maltekstseksjon2)
    }

    private fun getText(): Text {
        val now = LocalDateTime.now()
        return Text(
            createdBy = "abc",
            createdByName = "abc",
            created = now,
            modified = now,
            maltekstseksjonVersions = mutableListOf()
        )
    }

}
