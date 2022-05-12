package no.nav.klage.texts.api.views

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime
import java.util.*

data class TextView(
    val id: UUID,
    val title: String,
    val type: String,
    val content: JsonNode,
    val hjemler: Set<String> = emptySet(),
    val ytelser: Set<String> = emptySet(),
    val utfall: Set<String> = emptySet(),
    val enheter: Set<String> = emptySet(),
    val sections: Set<String> = emptySet(),
    val created: LocalDateTime,
    var modified: LocalDateTime?,
)