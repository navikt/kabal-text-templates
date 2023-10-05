package no.nav.klage.texts.repositories

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.util.getLogger
import org.springframework.stereotype.Repository
import kotlin.system.measureTimeMillis

@Repository
class SearchTextRepositoryCustomImpl : SearchTextRepositoryCustom {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PersistenceContext
    lateinit var entityManager: EntityManager

    override fun searchTexts(
        textType: String?,
        utfall: List<String>,
        ytelser: List<String>,
        hjemler: List<String>,
        enheter: List<String>,
        templates: List<String>,
        templateSectionList: List<String>,
        ytelseHjemmelList: List<String>,
    ): List<Text> {
        var texts: MutableList<Text>

        val millis = measureTimeMillis {
            texts = entityManager.createQuery("from Text", Text::class.java).resultList
        }

        logger.debug("searchTexts getting all texts took {} millis. Found {} texts", millis, texts.size)

        return texts.filter { text ->
            val textTypeCondition = if (textType != null) {
                text.textType == textType
            } else true

            textTypeCondition &&
            condition(utfall, text.utfall) &&
            condition(ytelser, text.ytelser) &&
            condition(hjemler, text.hjemler) &&
            condition(enheter, text.enheter) &&
            condition(templates, text.templates) &&
            condition(templateSectionList, text.templateSectionList) &&
            condition(ytelseHjemmelList, text.ytelseHjemmelList)
        }
    }

    private fun condition(listOfValues: List<String>, dbValues: Set<String>): Boolean {
        if (dbValues.isEmpty() && listOfValues.contains("NONE")) {
            return true
        }

        val queryValueSets = if (listOfValues.isNotEmpty()) {
            listOfValues.map { valueString ->
                valueString.split(":").toSet()
            }
        } else return true

        val dbValueSets = dbValues.map { utfallString ->
            utfallString.split(":").toSet()
        }

        return dbValueSets.any { dbValueSet ->
            queryValueSets.any { queryValueSet ->
                (queryValueSet.first() == "NONE" && dbValueSet.isEmpty()) || queryValueSet.containsAll(dbValueSet)
            }
        }
    }
}