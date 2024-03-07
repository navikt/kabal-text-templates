package no.nav.klage.texts.service

import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.testCompositeValues
import no.nav.klage.texts.util.testSets
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

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
        logger.debug(
            "running searchTexts with textType {}, utfallIdList {}, enhetIdList {}, templateSectionIdList {}, ytelseHjemmelIdList{}. Number of texts: {}",
            textType,
            utfallIdList,
            enhetIdList,
            textType,
            ytelseHjemmelIdList,
            texts.size
        )

        val textVersions: List<TextVersion>

        val millis = measureTimeMillis {
            textVersions = texts.filter { textVersion ->
                val textTypeCondition = if (textType != null) {
                    textVersion.textType == textType
                } else true

                val utfallIdListOutcome: Boolean
                val enhetIdListOutcome: Boolean
                val templateSectionOutcome: Boolean
                val ytelseHjemmelOutcome: Boolean

                var innerMillis = measureTimeMillis {
                    utfallIdListOutcome = testSets(utfallIdList, textVersion.utfallIdList)
                }

                logger.debug("utfallIdListOutcome: {}", innerMillis)

                innerMillis = measureTimeMillis {
                    enhetIdListOutcome = testSets(enhetIdList, textVersion.enhetIdList)
                }

                logger.debug("enhetIdListOutcome: {}", innerMillis)

                innerMillis = measureTimeMillis {
                    templateSectionOutcome = testCompositeValues(templateSectionIdList, textVersion.templateSectionIdList)
                }

                logger.debug("templateSectionOutcome: {}", innerMillis)

                innerMillis = measureTimeMillis {
                    ytelseHjemmelOutcome = testCompositeValues(ytelseHjemmelIdList, textVersion.ytelseHjemmelIdList)
                }

                logger.debug("ytelseHjemmelOutcome: {}", innerMillis)

                textTypeCondition &&
                        utfallIdListOutcome &&
                        enhetIdListOutcome &&
                        templateSectionOutcome &&
                        ytelseHjemmelOutcome
//                        testSets(utfallIdList, textVersion.utfallIdList) &&
//                        testSets(enhetIdList, textVersion.enhetIdList) &&
//                        testCompositeValues(templateSectionIdList, textVersion.templateSectionIdList) &&
//                        testCompositeValues(ytelseHjemmelIdList, textVersion.ytelseHjemmelIdList)
            }
        }

        logger.debug("textfiltering took {} millis. Found {} texts", millis, textVersions.size)

        return texts
    }
}