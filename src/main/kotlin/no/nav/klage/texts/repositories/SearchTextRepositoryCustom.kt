package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text

interface SearchTextRepositoryCustom {

    fun searchTexts(
        textType: String?,
        utfall: List<String>,
        ytelser: List<String>,
        hjemler: List<String>,
        enheter: List<String>,
        sections: List<String>,
    ): List<Text>
}