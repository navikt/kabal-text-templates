package no.nav.klage.texts.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "changelog_entry", schema = "klage")
class ChangelogEntry(
    @Column(name = "saksbehandlerident")
    val saksbehandlerident: String?, //subjekt?
    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    val action: Action,
    @Enumerated(EnumType.STRING)
    @Column(name = "field")
    val field: Field,
    @Column(name = "from_value")
    val fromValue: String?,
    @Column(name = "to_value")
    val toValue: String?,
    @Column(name = "text_id")
    val textId: UUID,
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "created")
    val created: LocalDateTime = LocalDateTime.now()
) {
    companion object {

        fun changelog(
            saksbehandlerident: String?,
            field: Field,
            fromValue: String?,
            toValue: String?,
            textId: UUID,
            created: LocalDateTime
        ): ChangelogEntry? {
            if ((fromValue == null && toValue == null) || fromValue == toValue) {
                return null
            } else {
                val action = when {
                    fromValue == null -> Action.NEW
                    toValue == null -> Action.DELETION
                    else -> Action.CHANGE
                }
                return ChangelogEntry(
                    saksbehandlerident = saksbehandlerident,
                    action = action,
                    field = field,
                    fromValue = fromValue,
                    toValue = toValue,
                    textId = textId,
                    created = created
                )
            }
        }
    }
}

enum class Action {
    NEW, CHANGE, DELETION
}

enum class Field {
    TITLE,
    TEXT_TYPE,
    CONTENT,
    PLAIN_TEXT,
    UTFALL,
    ENHETER,
    TEMPLATE_SECTION_LIST,
    YTELSE_HJEMMEL_LIST,
    TEXT,
    SMARTEDITOR_VERSION,
}