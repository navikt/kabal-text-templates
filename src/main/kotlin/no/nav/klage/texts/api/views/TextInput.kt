package no.nav.klage.texts.api.views

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class VersionInput(
    val versionId: UUID
)


data class TextInput(
    val id: UUID? = null,
    val title: String,
    val textType: String,
    val content: JsonNode?,
    val plainText: String?,
    val version: Int?,
    val utfall: Set<String> = emptySet(),
    val enheter: Set<String> = emptySet(),
    val templateSectionList: Set<String> = emptySet(),
    val ytelseHjemmelList: Set<String> = emptySet(),

    val utfallIdList: Set<String>?,
    val enhetIdList: Set<String>?,
    val templateSectionIdList: Set<String>?,
    val ytelseHjemmelIdList: Set<String>?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateTextInput(
    val title: String,
    val textType: String,
    val content: JsonNode?,
    val plainText: String?,
    val utfall: Set<String> = emptySet(),
    val enheter: Set<String> = emptySet(),
    val templateSectionList: Set<String> = emptySet(),
    val ytelseHjemmelList: Set<String> = emptySet(),

    val utfallIdList: Set<String> = emptySet(),
    val enhetIdList: Set<String> = emptySet(),
    val templateSectionIdList: Set<String> = emptySet(),
    val ytelseHjemmelIdList: Set<String> = emptySet(),
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