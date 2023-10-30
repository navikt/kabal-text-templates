package no.nav.klage.texts.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "maltekstseksjon", schema = "klage")
class Maltekstseksjon(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "created")
    val created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime,
    @Column(name = "deleted")
    var deleted: Boolean = false,
    @Column(name = "created_by")
    var createdBy: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Maltekstseksjon

        return id == other.id
    }

    override fun hashCode() = id.hashCode()

    override fun toString(): String {
        return "Maltekstseksjon(id=$id, created=$created, modified=$modified, deleted=$deleted, createdBy=$createdBy)"
    }

}