package no.nav.klage.texts.domain

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "changelogentry", schema = "klage")
class ChangelogEntry(
    @Column(name = "saksbehandlerident")
    val saksbehandlerident: String?, //subjekt?
    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    val action: Action,
    @Enumerated(EnumType.STRING)
    @Column(name = "field")
    val field: Field,
    @Column(name = "fromvalue")
    val fromValue: String?,
    @Column(name = "tovalue")
    val toValue: String?,
    @Column(name = "text")
    val textId: UUID,
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "timestamp")
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {

        fun changelog(
            saksbehandlerident: String?,
            field: Field,
            fromValue: String?,
            toValue: String?,
            textId: UUID,
            timestamp: LocalDateTime
        ): ChangelogEntry? {
            if ((fromValue == null && toValue == null) || fromValue == toValue) {
                return null
            } else {
                val action = when {
                    fromValue == null && toValue != null -> Action.NEW
                    fromValue != null && toValue == null -> Action.DELETION
                    else -> Action.CHANGE
                }
                return ChangelogEntry(
                    saksbehandlerident = saksbehandlerident,
                    action = action,
                    field = field,
                    fromValue = fromValue,
                    toValue = toValue,
                    textId = textId,
                    timestamp = timestamp
                )
            }
        }
    }
}

enum class Action {
    NEW, CHANGE, DELETION
}

enum class Field {
    TITLE, TYPE, CONTENT, HJEMLER, YTELSER, UTFALL, ENHETER, TEXT, DELETION
}