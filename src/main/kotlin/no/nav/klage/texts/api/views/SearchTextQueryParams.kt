package no.nav.klage.texts.api.views

data class SearchTextQueryParams(
    val textType: String?,
    val utfallIdList: List<String>?,
    val enhetIdList: List<String>?,
    val templateSectionIdList: List<String>?,
    val ytelseHjemmelIdList: List<String>?,
    val trash: Boolean?,
)

data class SearchMaltekstseksjonQueryParams(
    val textIdList: List<String>?,
    val utfallIdList: List<String>?,
    val enhetIdList: List<String>?,
    val templateSectionIdList: List<String>?,
    val ytelseHjemmelIdList: List<String>?,
)
