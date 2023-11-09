package no.nav.klage.texts.util

import no.nav.klage.texts.domain.Editor
import java.time.LocalDateTime

fun updateEditors(existingEditors: MutableSet<Editor>, newEditorNavIdent: String) {
    val now = LocalDateTime.now()
    val found = existingEditors.find { it.navIdent == newEditorNavIdent }
    if (found != null) {
        found.modified = now
    } else {
        existingEditors += Editor(
            navIdent = newEditorNavIdent,
            created = now,
            modified = now,
        )
    }
}