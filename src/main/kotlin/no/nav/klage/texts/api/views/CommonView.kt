package no.nav.klage.texts.api.views

import java.time.LocalDateTime

data class EditorView(
    val navIdent: String,
    val created: LocalDateTime,
    val modified: LocalDateTime,
)