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
    fun findByPublishedIsTrueAndMaltekstseksjonDeletedIsFalse(): List<MaltekstseksjonVersion>

    @EntityGraph(attributePaths = [
        "texts",
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    fun findByPublishedDateTimeIsNullAndMaltekstseksjonDeletedIsFalse(): List<MaltekstseksjonVersion>

    fun findByPublishedIsTrueAndMaltekstseksjonId(
        maltekstseksjonId: UUID
    ): MaltekstseksjonVersion

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
            select mvl.maltekstseksjon.id from Text t inner join t.maltekstseksjonVersionList mvl
                where mvl.published = true
                and t.id = :textId
        """
    )
    fun findConnectedMaltekstseksjonIdList(textId: UUID): List<UUID>

}