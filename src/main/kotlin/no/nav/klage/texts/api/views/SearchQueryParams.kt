package no.nav.klage.texts.api.views

data class SearchQueryParams(
    val utfall: List<String> = emptyList(),
    val ytelser: List<String> = emptyList(),
    val hjemler: List<String> = emptyList(),
    val enheter: List<String> = emptyList(),
)