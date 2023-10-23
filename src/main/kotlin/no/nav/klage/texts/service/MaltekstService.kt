package no.nav.klage.texts.service

import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.exceptions.MaltekstNotFoundException
import no.nav.klage.texts.repositories.MaltekstRepository
import no.nav.klage.texts.repositories.TextVersionRepository
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Transactional
@Service
class MaltekstService(
    private val maltekstRepository: MaltekstRepository,
    private val textVersionRepository: TextVersionRepository,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun createMaltekst(
        maltekstseksjonVersion: MaltekstseksjonVersion,
    ): MaltekstseksjonVersion = maltekstRepository.save(maltekstseksjonVersion)

    fun deleteMaltekst(
        maltekstId: UUID,
    ) = maltekstRepository.deleteById(maltekstId)

    fun getMaltekst(maltekstId: UUID): MaltekstseksjonVersion = maltekstRepository.findById(maltekstId)
        .orElseThrow { MaltekstNotFoundException("Maltekstseksjon with id $maltekstId not found") }

//    fun updateMaltekst(
//        maltekstId: UUID,
//        title: String,
//        textIdList: Set<String>,
//        utfallIdList: Set<String>,
//        enhetIdList: Set<String>,
//        templateSectionIdList: Set<String>,
//        ytelseHjemmelIdList: Set<String>,
//    ): Maltekstseksjon {
//        val maltekstseksjon = getMaltekst(maltekstId)
//
//        maltekstseksjon.title = title
//        maltekstseksjon.texts = textIdList
//        maltekstseksjon.utfallIdList = utfallIdList
//        maltekstseksjon.enhetIdList = enhetIdList
//        maltekstseksjon.templateSectionIdList = templateSectionIdList
//        maltekstseksjon.ytelseHjemmelIdList = ytelseHjemmelIdList
//
//        maltekstseksjon.modified = LocalDateTime.now()
//        return maltekstseksjon
//    }

    fun updateTitle(
        input: String,
        maltekstId: UUID,
    ): MaltekstseksjonVersion {
        val maltekstseksjon = getMaltekst(maltekstId)
        maltekstseksjon.title = input
        maltekstseksjon.modified = LocalDateTime.now()
        return maltekstseksjon
    }

    fun updateTextIdList(
        input: List<String>,
        maltekstId: UUID,
    ): MaltekstseksjonVersion {
        val maltekstseksjon = getMaltekst(maltekstId)
        maltekstseksjon.texts = input.map { textVersionRepository.getReferenceById(UUID.fromString(it)) }
        maltekstseksjon.modified = LocalDateTime.now()
        return maltekstseksjon
    }

    fun updateUtfallIdList(
        input: Set<String>,
        maltekstId: UUID,
    ): MaltekstseksjonVersion {
        val maltekstseksjon = getMaltekst(maltekstId)
        maltekstseksjon.utfallIdList = input
        maltekstseksjon.modified = LocalDateTime.now()
        return maltekstseksjon
    }

    fun updateTemplateSectionIdList(
        input: Set<String>,
        maltekstId: UUID,
    ): MaltekstseksjonVersion {
        val maltekstseksjon = getMaltekst(maltekstId)
        maltekstseksjon.templateSectionIdList = input
        maltekstseksjon.modified = LocalDateTime.now()
        return maltekstseksjon
    }

    fun updateYtelseHjemmelIdList(
        input: Set<String>,
        maltekstId: UUID,
    ): MaltekstseksjonVersion {
        val maltekstseksjon = getMaltekst(maltekstId)
        maltekstseksjon.ytelseHjemmelIdList = input
        maltekstseksjon.modified = LocalDateTime.now()
        return maltekstseksjon
    }

    fun updateEnhetIdList(
        input: Set<String>,
        maltekstId: UUID,
    ): MaltekstseksjonVersion {
        val maltekstseksjon = getMaltekst(maltekstId)
        maltekstseksjon.enhetIdList = input
        maltekstseksjon.modified = LocalDateTime.now()
        return maltekstseksjon
    }

//    fun searchMalteksts(
//        utfall: List<String>,
//        enheter: List<String>,
//        templateSectionList: List<String>,
//        ytelseHjemmelList: List<String>,
//    ): List<Maltekstseksjon> {
//        return searchMaltekstRepository.searchMalteksts(
//            maltekstType = maltekstType,
//            utfall = utfall,
//            enheter = enheter,
//            templateSectionList = templateSectionList,
//            ytelseHjemmelList = ytelseHjemmelList,
//        )
//    }

    fun getAllMalteksts(): List<MaltekstseksjonVersion> = maltekstRepository.findAll()
}