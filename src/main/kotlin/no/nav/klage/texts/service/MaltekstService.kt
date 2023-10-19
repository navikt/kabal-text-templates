package no.nav.klage.texts.service

import no.nav.klage.texts.domain.Maltekst
import no.nav.klage.texts.exceptions.MaltekstNotFoundException
import no.nav.klage.texts.repositories.MaltekstRepository
import no.nav.klage.texts.repositories.TextRepository
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
    private val textRepository: TextRepository,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun createMaltekst(
        maltekst: Maltekst,
    ): Maltekst = maltekstRepository.save(maltekst)

    fun deleteMaltekst(
        maltekstId: UUID,
    ) = maltekstRepository.deleteById(maltekstId)

    fun getMaltekst(maltekstId: UUID): Maltekst = maltekstRepository.findById(maltekstId)
        .orElseThrow { MaltekstNotFoundException("Maltekst with id $maltekstId not found") }

//    fun updateMaltekst(
//        maltekstId: UUID,
//        title: String,
//        textIdList: Set<String>,
//        utfallIdList: Set<String>,
//        enhetIdList: Set<String>,
//        templateSectionIdList: Set<String>,
//        ytelseHjemmelIdList: Set<String>,
//    ): Maltekst {
//        val maltekst = getMaltekst(maltekstId)
//
//        maltekst.title = title
//        maltekst.texts = textIdList
//        maltekst.utfallIdList = utfallIdList
//        maltekst.enhetIdList = enhetIdList
//        maltekst.templateSectionIdList = templateSectionIdList
//        maltekst.ytelseHjemmelIdList = ytelseHjemmelIdList
//
//        maltekst.modified = LocalDateTime.now()
//        return maltekst
//    }

    fun updateTitle(
        input: String,
        maltekstId: UUID,
    ): Maltekst {
        val maltekst = getMaltekst(maltekstId)
        maltekst.title = input
        maltekst.modified = LocalDateTime.now()
        return maltekst
    }

    fun updateTextIdList(
        input: Set<String>,
        maltekstId: UUID,
    ): Maltekst {
        val maltekst = getMaltekst(maltekstId)
        maltekst.texts = textRepository.findAllById(input.map { UUID.fromString(it) }).toSet()
        maltekst.modified = LocalDateTime.now()
        return maltekst
    }

    fun updateUtfallIdList(
        input: Set<String>,
        maltekstId: UUID,
    ): Maltekst {
        val maltekst = getMaltekst(maltekstId)
        maltekst.utfallIdList = input
        maltekst.modified = LocalDateTime.now()
        return maltekst
    }

    fun updateTemplateSectionIdList(
        input: Set<String>,
        maltekstId: UUID,
    ): Maltekst {
        val maltekst = getMaltekst(maltekstId)
        maltekst.templateSectionIdList = input
        maltekst.modified = LocalDateTime.now()
        return maltekst
    }

    fun updateYtelseHjemmelIdList(
        input: Set<String>,
        maltekstId: UUID,
    ): Maltekst {
        val maltekst = getMaltekst(maltekstId)
        maltekst.ytelseHjemmelIdList = input
        maltekst.modified = LocalDateTime.now()
        return maltekst
    }

    fun updateEnhetIdList(
        input: Set<String>,
        maltekstId: UUID,
    ): Maltekst {
        val maltekst = getMaltekst(maltekstId)
        maltekst.enhetIdList = input
        maltekst.modified = LocalDateTime.now()
        return maltekst
    }

//    fun searchMalteksts(
//        maltekstType: String?,
//        utfall: List<String>,
//        enheter: List<String>,
//        templateSectionList: List<String>,
//        ytelseHjemmelList: List<String>,
//    ): List<Maltekst> {
//        return searchMaltekstRepository.searchMalteksts(
//            maltekstType = maltekstType,
//            utfall = utfall,
//            enheter = enheter,
//            templateSectionList = templateSectionList,
//            ytelseHjemmelList = ytelseHjemmelList,
//        )
//    }

    fun getAllMalteksts(): List<Maltekst> = maltekstRepository.findAll()
}