package no.nav.klage.texts.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "version_editor", schema = "klage")
class Editor(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "nav_ident")
    val navIdent: String,
    @Column(name = "created")
    var created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Editor

        if (id != other.id) return false
        if (navIdent != other.navIdent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + navIdent.hashCode()
        return result
    }

    override fun toString(): String {
        return "Editor(id=$id, navIdent='$navIdent', created=$created, modified=$modified)"
    }

}