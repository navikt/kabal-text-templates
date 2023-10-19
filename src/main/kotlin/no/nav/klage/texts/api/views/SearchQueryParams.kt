package no.nav.klage.texts.api.views

data class SearchQueryParams(
    val textType: String?,
    val utfall: List<String>?,
    val enheter: List<String>?,
    val templateSectionList: List<String>?,
    val ytelseHjemmelList: List<String>?,

    val utfallIdList: List<String>?,
    val enhetIdList: List<String>?,
    val templateSectionIdList: List<String>?,
    val ytelseHjemmelIdList: List<String>?,
)