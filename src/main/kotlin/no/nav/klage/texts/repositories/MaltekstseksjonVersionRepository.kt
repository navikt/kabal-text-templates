package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.MaltekstseksjonVersion
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface MaltekstseksjonVersionRepository : JpaRepository<MaltekstseksjonVersion, UUID> {

    @EntityGraph(attributePaths = [
        "texts",
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    fun findByPublishedIsTrue(): List<MaltekstseksjonVersion>

    @EntityGraph(attributePaths = [
        "texts",
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    fun findByPublishedDateTimeIsNull(): List<MaltekstseksjonVersion>

    fun findByPublishedIsTrueAndMaltekstseksjonId(
        maltekstseksjonId: UUID
    ): MaltekstseksjonVersion?

    fun findByPublishedDateTimeIsNullAndMaltekstseksjonId(
        maltekstseksjonId: UUID
    ): MaltekstseksjonVersion?

    @EntityGraph(attributePaths = [
        "texts",
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    fun findByMaltekstseksjonId(maltekstseksjonId: UUID): List<MaltekstseksjonVersion>

    @EntityGraph(attributePaths = [
        "texts",
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    @Query(
        """
            select mv.maltekstseksjon.id from Text t inner join t.maltekstseksjonVersions mv
                where mv.published = true
                and t.id = :textId
        """
    )
    fun findConnectedMaltekstseksjonPublishedIdList(textId: UUID): List<UUID>

    @EntityGraph(attributePaths = [
        "texts",
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    @Query(
        """
            select mvl.maltekstseksjon.id from Text t inner join t.maltekstseksjonVersions mvl
                where mvl.publishedDateTime = null
                and t.id = :textId
        """
    )
    fun findConnectedMaltekstseksjonDraftsIdList(textId: UUID): List<UUID>

}