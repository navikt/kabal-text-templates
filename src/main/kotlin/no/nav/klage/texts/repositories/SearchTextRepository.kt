package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.util.getLogger
import org.springframework.stereotype.Repository
import kotlin.system.measureTimeMillis

@Repository
class SearchTextRepository(
    private val textRepository: TextRepository,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun searchTexts(
        textType: String?,
        utfall: List<String>,
        enheter: List<String>,
        templateSectionList: List<String>,
        ytelseHjemmelList: List<String>,
    ): List<Text> {
        var texts: MutableList<Text>

        val millis = measureTimeMillis {
            texts = textRepository.findAll()
        }

        logger.debug("searchTexts getting all texts took {} millis. Found {} texts", millis, texts.size)

        return texts.filter { text ->
            val textTypeCondition = if (textType != null) {
                text.textType == textType
            } else true

            textTypeCondition &&
            testSets(utfall, text.utfall) &&
            testSets(enheter, text.enheter) &&
            testCompositeValues(templateSectionList, text.templateSectionList) &&
            testCompositeValues(ytelseHjemmelList, text.ytelseHjemmelList)
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