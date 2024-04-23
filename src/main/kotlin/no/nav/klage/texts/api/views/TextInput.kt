package no.nav.klage.texts.api.views

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class VersionInput(
    val versionId: UUID
)

data class TextInput(
    val id: UUID? = null,
    val title: String,
    val textType: String,

    val richTextNN: JsonNode?,
    val richTextNB: JsonNode?,
    val richTextUntranslated: JsonNode?,

    val plainTextNN: String?,
    val plainTextNB: String?,

    val version: Int?,
    val enhetIdList: Set<String>?,
    val utfallIdList: Set<String>?,
    val templateSectionIdList: Set<String>?,
    val ytelseHjemmelIdList: Set<String>?,
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

data class PlainTextInput(
    val plainText: String
)

data class SmartEditorVersionInput(
    val version: Int
)

data class UtfallIdListCompatibleInput(
    val utfall: Set<String>?,
    val utfallIdList: Set<String>?,
)

data class EnhetIdListCompatibleInput(
    val enheter: Set<String>?,
    val enhetIdList: Set<String>?,
)

data class TemplateSectionIdListCompatibleInput(
    val templateSectionList: Set<String>?,
    val templateSectionIdList: Set<String>?,
)

data class YtelseHjemmelIdListCompatibleInput(
    val ytelseHjemmelList: Set<String>?,
    val ytelseHjemmelIdList: Set<String>?,
)

enum class Language {
    @JsonProperty("nn")
    NN,
    @JsonProperty("nb")
    NB,
    @JsonProperty("untranslated")
    UNTRANSLATED,
}