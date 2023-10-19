package no.nav.klage.texts.api.views

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

data class MaltekstInput(
    val id: UUID? = null,
    val title: String,
    val textIdList: Set<String> = emptySet(),
    val utfallIdList: Set<String> = emptySet(),
    val enhetIdList: Set<String> = emptySet(),
    val templateSectionIdList: Set<String> = emptySet(),
    val ytelseHjemmelIdList: Set<String> = emptySet(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateMaltekstInput(
    val title: String,
    val textIdList: Set<String> = emptySet(),
    val utfallIdList: Set<String> = emptySet(),
    val enhetIdList: Set<String> = emptySet(),
    val templateSectionIdList: Set<String> = emptySet(),
    val ytelseHjemmelIdList: Set<String> = emptySet(),
)

data class TextIdListInput(
    val textIdList: Set<String>
)

data class UtfallIdListInput(
    val utfallIdList: Set<String>
)

data class EnhetIdListInput(
    val enhetIdList: Set<String>
)

data class TemplateSectionIdListInput(
    val templateSectionIdList: Set<String>
)

data class YtelseHjemmelIdListInput(
    val ytelseHjemmelIdList: Set<String>
)