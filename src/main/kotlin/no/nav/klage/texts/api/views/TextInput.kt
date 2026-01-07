package no.nav.klage.texts.api.views

import com.fasterxml.jackson.annotation.JsonProperty
import tools.jackson.databind.JsonNode
import java.util.*

data class VersionInput(
    val versionId: UUID
)

data class TextInput(
    val id: UUID? = null,
    val title: String,
    val textType: String,

    val richText: RichText?,
    val plainText: PlainText?,

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

data class RichTextInput(
    val richText: JsonNode
)

data class PlainTextInput(
    val plainText: String
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