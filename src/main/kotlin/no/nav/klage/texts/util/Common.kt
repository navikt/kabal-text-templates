package no.nav.klage.texts.util

import no.nav.klage.texts.domain.Editor
import java.time.LocalDateTime

fun getUpdatedEditors(existingEditors: Set<Editor>, newEditorNavIdent: String): Set<Editor> {
    val now = LocalDateTime.now()
    val found = existingEditors.find { it.navIdent == newEditorNavIdent }
    return if (found != null) {
        found.modified = now
        existingEditors
    } else {
        existingEditors + Editor(
            navIdent = newEditorNavIdent,
            created = now,
            modified = now,
        )
    }
}