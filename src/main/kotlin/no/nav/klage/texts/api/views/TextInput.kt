package no.nav.klage.texts.api.views

data class TextInput(
    val title: String,
    val type: String,
    val content: String,
    val hjemler: Set<String> = emptySet(),
    val ytelser: Set<String> = emptySet(),
    val utfall: Set<String> = emptySet(),
    val enheter: Set<String> = emptySet(),
)