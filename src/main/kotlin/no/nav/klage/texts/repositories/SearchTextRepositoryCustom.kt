package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text

interface SearchTextRepositoryCustom {

    fun searchTexts(
        textType: String?,
        requiredSection: String?,
        utfall: List<String>,
        ytelser: List<String>,
        hjemler: List<String>,
        enheter: List<String>,
        sections: List<String>,
        templates: List<String>,
    ): List<Text>
}