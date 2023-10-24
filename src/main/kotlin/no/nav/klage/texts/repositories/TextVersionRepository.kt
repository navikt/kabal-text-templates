package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.TextVersion
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TextVersionRepository : JpaRepository<TextVersion, UUID> {

    @EntityGraph(attributePaths = [
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    fun findByPublishedIsTrue(): List<TextVersion>

    @EntityGraph(attributePaths = [
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    fun findByPublishedDateTimeIsNull(): List<TextVersion>

    @EntityGraph(attributePaths = [
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    fun findByPublishedIsFalseAndPublishedDateTimeIsNotNullAndTextId(textId: UUID): List<TextVersion>

    fun findByPublishedIsTrueAndTextId(
        textId: UUID
    ): TextVersion

    fun findByPublishedDateTimeIsNullAndTextId(
        textId: UUID
    ): TextVersion?

}