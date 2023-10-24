package no.nav.klage.texts.service

import no.nav.klage.texts.api.views.MaltekstInput
import no.nav.klage.texts.api.views.VersionInput
import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.exceptions.ClientErrorException
import no.nav.klage.texts.repositories.MaltekstseksjonRepository
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepository
import no.nav.klage.texts.repositories.TextRepository
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import kotlin.system.measureTimeMillis

@Transactional
@Service
class MaltekstseksjonService(
    private val maltekstseksjonRepository: MaltekstseksjonRepository,
    private val maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository,
    private val textRepository: TextRepository,
    private val searchMaltekstseksjonService: SearchMaltekstseksjonService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun publishMaltekstseksjonVersion(maltekstseksjonId: UUID, saksbehandlerIdent: String): MaltekstseksjonVersion {
         maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(maltekstseksjonId).published = false

        val maltekstseksjonVersionDraft =
             maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            ) ?: throw ClientErrorException("there was no draft to publish")

        maltekstseksjonVersionDraft.publishedDateTime = LocalDateTime.now()
        maltekstseksjonVersionDraft.published = true
        maltekstseksjonVersionDraft.publishedBy = saksbehandlerIdent

        return maltekstseksjonVersionDraft
    }

    fun getMaltekstseksjonVersions(maltekstseksjonId: UUID): List<MaltekstseksjonVersion> =
         maltekstseksjonVersionRepository.findByMaltekstseksjonId(maltekstseksjonId)

    fun createNewMaltekstseksjon(
        maltekstseksjonInput: MaltekstInput,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val now = LocalDateTime.now()

        val maltekstseksjon = maltekstseksjonRepository.save(
            Maltekstseksjon(
                created = now,
                modified = now,
            )
        )

        return  maltekstseksjonVersionRepository.save(
            MaltekstseksjonVersion(
                title = maltekstseksjonInput.title,
                utfallIdList = maltekstseksjonInput.utfallIdList,
                enhetIdList = maltekstseksjonInput.enhetIdList,
                templateSectionIdList = maltekstseksjonInput.templateSectionIdList,
                ytelseHjemmelIdList = maltekstseksjonInput.ytelseHjemmelIdList,
                editors = setOf(saksbehandlerIdent),
                maltekstseksjonId = maltekstseksjon.id,
                created = now,
                modified = now,
                publishedDateTime = null,
                published = false,
                publishedBy = null,
            )
        )
    }

    fun createNewDraft(
        maltekstseksjonId: UUID,
        versionInput: VersionInput?,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val existingVersion = if (versionInput != null) {
            maltekstseksjonVersionRepository.getReferenceById(versionInput.versionId)
        } else {
            maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            )
        }

        val existingDraft = maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        )

        return maltekstseksjonVersionRepository.save(
            existingVersion.createDraft(existingDraft?.id)
        )
    }

    fun deleteMaltekstseksjon(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ) {
        val maltekstseksjon = maltekstseksjonRepository.getReferenceById(maltekstseksjonId)
        maltekstseksjon.deleted = true
    }

    fun getCurrentMaltekstseksjonVersion(maltekstseksjonId: UUID): MaltekstseksjonVersion {
        return  maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        )
    }

    fun updateMaltekstseksjon(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        title: String,
        textIdList: List<String>,
        utfallIdList: Set<String>,
        enhetIdList: Set<String>,
        templateSectionIdList: Set<String>,
        ytelseHjemmelIdList: Set<String>,
    ): MaltekstseksjonVersion {
        val maltekstseksjonVersion = getOrCreateCurrentDraft(maltekstseksjonId)

        maltekstseksjonVersion.apply {
            this.title = title
            this.texts = textIdList.map { textRepository.getReferenceById(UUID.fromString(it)) }
            this.utfallIdList = utfallIdList
            this.enhetIdList = enhetIdList
            this.templateSectionIdList = templateSectionIdList
            this.ytelseHjemmelIdList = ytelseHjemmelIdList
            this.editors += saksbehandlerIdent
        }

        return maltekstseksjonVersion
    }

    fun updateTitle(
        input: String,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val maltekstseksjonVersion = getOrCreateCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.title = input
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateTexts(
        input: List<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val maltekstseksjonVersion = getOrCreateCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.texts = input.map { textRepository.getReferenceById(UUID.fromString(it)) }
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateUtfall(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val maltekstseksjonVersion = getOrCreateCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.utfallIdList = input
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateEnheter(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val maltekstseksjonVersion = getOrCreateCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.enhetIdList = input
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateTemplateSectionList(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val maltekstseksjonVersion = getOrCreateCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.templateSectionIdList = input
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateYtelseHjemmelList(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val maltekstseksjonVersion = getOrCreateCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.ytelseHjemmelIdList = input
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun searchPublishedMaltekstseksjoner(
        textIdList: List<String>,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
    ): List<MaltekstseksjonVersion> {
        var maltekstseksjonVersions: List<MaltekstseksjonVersion>

        val millis = measureTimeMillis {
            maltekstseksjonVersions =  maltekstseksjonVersionRepository.findByPublishedIsTrue()
        }

        logger.debug("searchMaltekstseksjoner getting all texts took {} millis. Found {} texts", millis, maltekstseksjonVersions.size)

        return searchMaltekstseksjonService.searchMaltekstseksjoner(
            maltekstseksjonVersions = maltekstseksjonVersions,
            textIdList = textIdList,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
        )
    }

    fun searchMaltekstseksjoner(
        textIdList: List<String>,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
    ): List<MaltekstseksjonVersion> {
        var maltekstseksjonVersions: List<MaltekstseksjonVersion>

        val millis = measureTimeMillis {
            //get all drafts
            val drafts = maltekstseksjonVersionRepository.findByPublishedDateTimeIsNull()
            //get published
            val published = maltekstseksjonVersionRepository.findByPublishedIsTrue()

            val draftsMaltekstseksjonIdList = drafts.map { it.maltekstseksjonId }

            val publishedWithNoDrafts = published.filter { maltekstseksjonVersion ->
                maltekstseksjonVersion.maltekstseksjonId !in draftsMaltekstseksjonIdList
            }

            maltekstseksjonVersions = drafts + publishedWithNoDrafts
        }

        logger.debug("searchMaltekstseksjoner getting all texts took {} millis. Found {} texts", millis, maltekstseksjonVersions.size)

        return searchMaltekstseksjonService.searchMaltekstseksjoner(
            maltekstseksjonVersions = maltekstseksjonVersions,
            textIdList = textIdList,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
        )
    }

    private fun getOrCreateCurrentDraft(maltekstseksjonId: UUID): MaltekstseksjonVersion {
        val maltekstseksjonVersionDraft =
             maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            )

        return if (maltekstseksjonVersionDraft != null) {
            maltekstseksjonVersionDraft
        } else {
            //Pick latest published version as template for the draft
            val template =  maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(maltekstseksjonId = maltekstseksjonId)

             maltekstseksjonVersionRepository.save(
                template.createDraft()
            )
        }
    }
}