package no.nav.klage.texts.api.views

data class SearchQueryParams(
    val textType: String?,
    val utfall: List<String>?,
    val enheter: List<String>?,
    val templateSectionList: List<String>?,
    val ytelseHjemmelList: List<String>?,
)