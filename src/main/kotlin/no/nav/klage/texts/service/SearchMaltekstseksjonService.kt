package no.nav.klage.texts.service

import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepository
import no.nav.klage.texts.util.getLogger
import org.springframework.stereotype.Service

@Service
class SearchMaltekstseksjonService(
    private val maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun searchMaltekstseksjoner(
        maltekstseksjonVersions: List<MaltekstseksjonVersion>,
        textIdList: List<String>,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
    ): List<MaltekstseksjonVersion> {

        return maltekstseksjonVersions.filter { maltekstseksjonVersion ->
            testSets(textIdList, maltekstseksjonVersion.texts.map { it.id.toString() }.toSet()) &&
            testSets(utfallIdList, maltekstseksjonVersion.utfallIdList) &&
            testSets(enhetIdList, maltekstseksjonVersion.enhetIdList) &&
            testCompositeValues(templateSectionIdList, maltekstseksjonVersion.templateSectionIdList) &&
            testCompositeValues(ytelseHjemmelIdList, maltekstseksjonVersion.ytelseHjemmelIdList)
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