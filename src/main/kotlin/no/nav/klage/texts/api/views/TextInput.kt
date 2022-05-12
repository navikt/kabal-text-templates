package no.nav.klage.texts.api.views

import com.fasterxml.jackson.databind.JsonNode

data class TextInput(
    val title: String,
    val textType: String,
    val content: JsonNode,
    val hjemler: Set<String> = emptySet(),
    val ytelser: Set<String> = emptySet(),
    val utfall: Set<String> = emptySet(),
    val enheter: Set<String> = emptySet(),
    val sections: Set<String> = emptySet(),
)