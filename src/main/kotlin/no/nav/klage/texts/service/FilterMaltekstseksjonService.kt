package no.nav.klage.texts.service

import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.testCompositeValues
import no.nav.klage.texts.util.testSets
import org.springframework.stereotype.Service

@Service
class FilterMaltekstseksjonService {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun filterMaltekstseksjoner(
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
}