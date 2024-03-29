package no.nav.klage.texts.api.views

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

data class MaltekstseksjonInput(
    val id: UUID? = null,
    val title: String,
    val textIdList: List<String> = emptyList(),
    val utfallIdList: Set<String> = emptySet(),
    val enhetIdList: Set<String> = emptySet(),
    val templateSectionIdList: Set<String> = emptySet(),
    val ytelseHjemmelIdList: Set<String> = emptySet(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateMaltekstseksjonInput(
    val title: String,
    val textIdList: List<String> = emptyList(),
    val utfallIdList: Set<String> = emptySet(),
    val enhetIdList: Set<String> = emptySet(),
    val templateSectionIdList: Set<String> = emptySet(),
    val ytelseHjemmelIdList: Set<String> = emptySet(),
)

data class TextIdListInput(
    val textIdList: List<String>
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