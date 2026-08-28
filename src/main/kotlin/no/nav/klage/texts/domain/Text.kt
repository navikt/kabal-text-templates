package no.nav.klage.texts.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "text", schema = "klage")
class Text(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "created")
    val created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime,
    @Column(name = "created_by")
    var createdBy: String,
    @Column(name = "created_by_name")
    var createdByName: String,
    @ManyToMany(mappedBy = "texts")
    val maltekstseksjonVersions: MutableList<MaltekstseksjonVersion>,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Text

        return id == other.id
    }

    override fun hashCode() = id.hashCode()

    override fun toString(): String =
        "Text(id=$id, created=$created, modified=$modified, createdBy=$createdBy, maltekstseksjonVersions=${maltekstseksjonVersions.map {
            it.id
        }})"
}
