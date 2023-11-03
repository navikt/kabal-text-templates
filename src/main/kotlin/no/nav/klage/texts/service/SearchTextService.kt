package no.nav.klage.texts.service

import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.testCompositeValues
import no.nav.klage.texts.util.testSets
import org.springframework.stereotype.Service

@Service
class SearchTextService {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun searchTexts(
        texts: List<TextVersion>,
        textType: String?,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
    ): List<TextVersion> {

        return texts.filter { textVersion ->
            val textTypeCondition = if (textType != null) {
                textVersion.textType == textType
            } else true

            textTypeCondition &&
            testSets(utfallIdList, textVersion.utfallIdList) &&
            testSets(enhetIdList, textVersion.enhetIdList) &&
            testCompositeValues(templateSectionIdList, textVersion.templateSectionIdList) &&
            testCompositeValues(ytelseHjemmelIdList, textVersion.ytelseHjemmelIdList)
        }
    }
}