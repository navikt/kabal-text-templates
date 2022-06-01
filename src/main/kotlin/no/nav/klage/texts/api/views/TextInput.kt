package no.nav.klage.texts.api.views

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class TextInput(
    val id: UUID? = null,
    val title: String,
    val textType: String,
    val content: JsonNode,
    val version: Int?,
    val hjemler: Set<String> = emptySet(),
    val ytelser: Set<String> = emptySet(),
    val utfall: Set<String> = emptySet(),
    val enheter: Set<String> = emptySet(),
    val sections: Set<String> = emptySet(),
    val templates: Set<String> = emptySet(),
)

data class TitleInput(
    val title: String
)

data class TextTypeInput(
    val textType: String
)

data class ContentInput(
    val content: JsonNode
)

data class SmartEditorVersionInput(
    val version: Int
)

data class HjemlerInput(
    val hjemler: Set<String>
)

data class YtelserInput(
    val ytelser: Set<String>
)

data class UtfallInput(
    val utfall: Set<String>
)

data class EnheterInput(
    val enheter: Set<String>
)

data class SectionsInput(
    val sections: Set<String>
)

data class TemplatesInput(
    val templates: Set<String>
)