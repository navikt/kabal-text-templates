package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.TextVersion
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TextVersionRepository : JpaRepository<TextVersion, UUID> {

    @EntityGraph("TextVersion.full")
    fun findByPublishedIsTrue(): List<TextVersion>

    @EntityGraph("TextVersion.full")
    fun findByPublishedDateTimeIsNull(): List<TextVersion>

    @EntityGraph("TextVersion.full")
    fun findByPublishedIsTrueAndTextId(
        textId: UUID
    ): TextVersion?

    @EntityGraph("TextVersion.full")
    fun findByPublishedDateTimeIsNotNullAndTextId(
        textId: UUID
    ): List<TextVersion>

    @EntityGraph("TextVersion.full")
    fun findByPublishedDateTimeIsNullAndTextId(
        textId: UUID
    ): TextVersion?

    @EntityGraph("TextVersion.full")
    fun findByTextId(textId: UUID): List<TextVersion>

    @EntityGraph("TextVersion.full")
    @Query(
        """
        SELECT tv
        FROM TextVersion tv
        WHERE tv.publishedDateTime is not null
          AND tv.text NOT IN (
            SELECT tv2.text
            FROM TextVersion tv2
            WHERE ((tv2.publishedDateTime = null) OR (tv2.published = true))
            GROUP BY tv2.text
          )
        """
    )
    fun findHiddenTextVersions(): List<TextVersion>

}