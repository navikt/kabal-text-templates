package no.nav.klage.texts.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "text", schema = "klage")
class Text(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "created")
    val created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime,
    @Column(name = "deleted")
    var deleted: Boolean = false,

    @OneToMany
    @JoinColumn(name = "text_id")
    @JoinTable(schema = "klage", name = "maltekstseksjon_version_text", inverseJoinColumns = [JoinColumn(name = "maltekstseksjon_version_id")])
    val maltekstseksjonVersionList: List<MaltekstseksjonVersion>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Text

        return id == other.id
    }

    override fun hashCode() = id.hashCode()

    override fun toString(): String {
        return "TextVersion(id=$id, created=$created, modified=$modified, deleted=$deleted)"
    }

}