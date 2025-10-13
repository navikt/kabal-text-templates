package no.nav.klage.texts.repositories

import jakarta.persistence.QueryHint
import no.nav.klage.texts.domain.TextVersion
import org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import java.util.*
import java.util.stream.Stream

interface TextVersionRepository : JpaRepository<TextVersion, UUID> {

    @QueryHints(QueryHint(name = HINT_FETCH_SIZE, value = "100"))
    @EntityGraph("TextVersion.fullWithoutEditors")
    @Query(
        """
        SELECT tv
        FROM TextVersion tv
        WHERE tv.published = true
        """
    )
    fun findByPublishedIsTrueForConsumer(): Stream<TextVersion>

    @QueryHints(QueryHint(name = HINT_FETCH_SIZE, value = "100"))
    @EntityGraph("TextVersion.fullWithoutEditors")
    fun findByPublishedIsTrue(): Stream<TextVersion>

    @QueryHints(QueryHint(name = HINT_FETCH_SIZE, value = "100"))
    @EntityGraph("TextVersion.fullWithoutEditors")
    fun findByPublishedDateTimeIsNull(): Stream<TextVersion>

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

    @EntityGraph("TextVersion.fullWithoutEditors")
    @Query(
        """
        SELECT tv
        FROM TextVersion tv
        WHERE tv.publishedDateTime is not null
          AND tv.text NOT IN (
            SELECT tv2.text
            FROM TextVersion tv2
            WHERE ((tv2.publishedDateTime is null) OR (tv2.published = true))
            GROUP BY tv2.text
          )
        """
    )
    fun findHiddenTextVersions(): List<TextVersion>

    @EntityGraph("TextVersion.full")
    override fun findById(textVersionId: UUID): Optional<TextVersion>

}