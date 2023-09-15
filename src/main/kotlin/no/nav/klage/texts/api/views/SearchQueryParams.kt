package no.nav.klage.texts.api.views

data class SearchQueryParams(
    val textType: String?,
    val requiredSection: String?,
    val utfall: List<String>?,
    val ytelser: List<String>?,
    val hjemler: List<String>?,
    val enheter: List<String>?,
    val sections: List<String>?,
    val templates: List<String>?,
    val templateSectionList: List<String>?,
    val ytelseHjemmelList: List<String>?,
)