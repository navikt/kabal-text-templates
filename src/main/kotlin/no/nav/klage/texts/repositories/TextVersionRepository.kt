package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.TextVersion
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
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
    fun findByPublishedDateTimeIsNullAndTextId(
        textId: UUID
    ): TextVersion?

    @EntityGraph("TextVersion.full")
    fun findByTextId(textId: UUID): List<TextVersion>

}