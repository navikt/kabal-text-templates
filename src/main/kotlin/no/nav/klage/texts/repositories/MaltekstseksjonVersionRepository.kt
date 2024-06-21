package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.MaltekstseksjonVersion
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface MaltekstseksjonVersionRepository : JpaRepository<MaltekstseksjonVersion, UUID> {

    @EntityGraph("MaltekstseksjonVersion.full")
    fun findByPublishedIsTrue(): List<MaltekstseksjonVersion>

    @EntityGraph("MaltekstseksjonVersion.full")
    fun findByPublishedDateTimeIsNull(): List<MaltekstseksjonVersion>

    @EntityGraph("MaltekstseksjonVersion.full")
    fun findByPublishedIsTrueAndMaltekstseksjonId(
        maltekstseksjonId: UUID
    ): MaltekstseksjonVersion?

    @EntityGraph("MaltekstseksjonVersion.full")
    fun findByPublishedDateTimeIsNullAndMaltekstseksjonId(
        maltekstseksjonId: UUID
    ): MaltekstseksjonVersion?

    @EntityGraph("MaltekstseksjonVersion.full")
    fun findByMaltekstseksjonId(maltekstseksjonId: UUID): List<MaltekstseksjonVersion>

    @EntityGraph("MaltekstseksjonVersion.full")
    @Query(
        """
            select mv.maltekstseksjon.id from Text t inner join t.maltekstseksjonVersions mv
                where mv.published = true
                and t.id = :textId
        """
    )
    fun findConnectedMaltekstseksjonPublishedIdList(textId: UUID): List<UUID>

    @EntityGraph("MaltekstseksjonVersion.full")
    @Query(
        """
            select mv.maltekstseksjon.id, t.id from Text t inner join t.maltekstseksjonVersions mv
                where mv.published = true
                and t.id in (:textIdList)
        """
    )
    fun findConnectedMaltekstseksjonPublishedIdListBulk(textIdList: List<UUID>): List<List<UUID>>

    @EntityGraph("MaltekstseksjonVersion.full")
    @Query(
        """
            select mvl.maltekstseksjon.id from Text t inner join t.maltekstseksjonVersions mvl
                where mvl.publishedDateTime = null
                and t.id = :textId
        """
    )
    fun findConnectedMaltekstseksjonDraftsIdList(textId: UUID): List<UUID>

    @EntityGraph("MaltekstseksjonVersion.full")
    @Query(
        """
            select mvl.maltekstseksjon.id, t.id from Text t inner join t.maltekstseksjonVersions mvl
                where mvl.publishedDateTime = null
                and t.id in (:textIdList)
        """
    )
    fun findConnectedMaltekstseksjonDraftsIdListBulk(textIdList: List<UUID>): List<List<UUID>>

    @EntityGraph("MaltekstseksjonVersion.full")
    override fun findAll(): List<MaltekstseksjonVersion>

    @EntityGraph("MaltekstseksjonVersion.full")
    override fun findAllById(ids: Iterable<UUID>): List<MaltekstseksjonVersion>

    @EntityGraph("MaltekstseksjonVersion.full")
    @Query(
        """
        SELECT mv
        FROM MaltekstseksjonVersion mv
        WHERE mv.publishedDateTime is not null
          AND mv.maltekstseksjon NOT IN (
            SELECT mv2.maltekstseksjon
            FROM MaltekstseksjonVersion mv2
            WHERE ((mv2.publishedDateTime is null) OR (mv2.published = true))
            GROUP BY mv2.maltekstseksjon
          )
        """
    )
    fun findHiddenMaltekstseksjonVersions(): List<MaltekstseksjonVersion>

    @EntityGraph("MaltekstseksjonVersion.full")
    override fun findById(maltekstseksjonVersionId: UUID): Optional<MaltekstseksjonVersion>

}