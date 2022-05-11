package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text

interface SearchTextRepositoryCustom {

    fun searchTexts(
        type: String?,
        utfall: List<String>,
        ytelser: List<String>,
        hjemler: List<String>,
        enheter: List<String>,
    ): List<Text>
}