package no.nav.klage.texts.domain

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "version_editor", schema = "klage")
class Editor(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "nav_ident")
    val navIdent: String,
    @Column(name = "name")
    val name: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "change_type")
    val changeType: ChangeType,
    @Column(name = "created")
    var created: LocalDateTime = LocalDateTime.now(),
) : Serializable {

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
        return "Editor(id=$id, navIdent='$navIdent', changeType=$changeType, created=$created)"
    }

    enum class ChangeType {
        RICH_TEXT_NB,
        RICH_TEXT_NN,
        RICH_TEXT_UNTRANSLATED,
        PLAIN_TEXT_NB,
        PLAIN_TEXT_NN,
        TEXT_TYPE,
        TEXT_VERSION_CREATED,
        TEXT_TITLE,
        TEXT_UTFALL,
        TEXT_SECTIONS,
        TEXT_YTELSE_HJEMMEL,
        TEXT_ENHETER,

        MALTEKSTSEKSJON_TITLE,
        MALTEKSTSEKSJON_TEXTS,
        MALTEKSTSEKSJON_VERSION_CREATED,
        MALTEKSTSEKSJON_UTFALL,
        MALTEKSTSEKSJON_ENHETER,
        MALTEKSTSEKSJON_SECTIONS,
        MALTEKSTSEKSJON_YTELSE_HJEMMEL,

        UNKNOWN,
    }

}