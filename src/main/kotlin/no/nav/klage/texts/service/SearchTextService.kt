package no.nav.klage.texts.service

import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.repositories.TextVersionRepository
import no.nav.klage.texts.util.getLogger
import org.springframework.stereotype.Service

@Service
class SearchTextService(
    private val textVersionRepository: TextVersionRepository,
) {

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

    private fun testSets(queryValues: List<String>, dbValues: Set<String>): Boolean {
        if (queryValues.contains("NONE") && queryValues.size == 1 && dbValues.isEmpty()) {
            return true
        }

        if (queryValues.isEmpty() || dbValues.isEmpty()) {
            return true
        }

        val queryValueSets = queryValues.map { valueString ->
            valueString.split(":").toSet()
        }

        val dbValueSets = dbValues.map { dbString ->
            dbString.split(":").toSet()
        }

        return dbValueSets.any { dbValueSet ->
            queryValueSets.any { queryValueSet ->
                queryValueSet.containsAll(dbValueSet)
            }
        }
    }

    private fun testCompositeValues(queryValues: List<String>, dbValues: Set<String>): Boolean {
        if (queryValues.contains("NONE") && queryValues.size == 1 && dbValues.isEmpty()) {
            return true
        }

        if (queryValues.isEmpty() || dbValues.isEmpty()) {
            return true
        }

        for (query in queryValues) {
            for (dbValue in dbValues) {
                val regex = Regex(query.replace("*", ".*"))
                if (dbValue.matches(regex)) {
                    return true
                }
            }
        }

        return false
    }
}