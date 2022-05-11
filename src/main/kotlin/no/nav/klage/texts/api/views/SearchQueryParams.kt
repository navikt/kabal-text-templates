package no.nav.klage.texts.api.views

data class SearchQueryParams(
    val type: String?,
    val utfall: List<String>?,
    val ytelser: List<String>?,
    val hjemler: List<String>?,
    val enheter: List<String>?,
    val sections: List<String>?,
)