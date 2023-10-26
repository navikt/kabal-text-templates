package no.nav.klage.texts.service

import no.nav.klage.texts.api.views.MaltekstseksjonInput
import no.nav.klage.texts.api.views.VersionInput
import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.exceptions.ClientErrorException
import no.nav.klage.texts.exceptions.MaltekstseksjonNotFoundException
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
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

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

    fun getMaltekstseksjonVersions(maltekstseksjonId: UUID): List<MaltekstseksjonVersion> {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)
        return maltekstseksjonVersionRepository.findByMaltekstseksjonId(maltekstseksjonId)
            .sortedByDescending { it.publishedDateTime ?: LocalDateTime.now() }
    }

    fun createNewMaltekstseksjon(
        maltekstseksjonInput: MaltekstseksjonInput,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val now = LocalDateTime.now()

        val maltekstseksjon = maltekstseksjonRepository.save(
            Maltekstseksjon(
                created = now,
                modified = now,
            )
        )

        return maltekstseksjonVersionRepository.save(
            MaltekstseksjonVersion(
                title = maltekstseksjonInput.title,
                utfallIdList = maltekstseksjonInput.utfallIdList,
                enhetIdList = maltekstseksjonInput.enhetIdList,
                templateSectionIdList = maltekstseksjonInput.templateSectionIdList,
                ytelseHjemmelIdList = maltekstseksjonInput.ytelseHjemmelIdList,
                editors = setOf(saksbehandlerIdent),
                maltekstseksjon = maltekstseksjon,
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
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

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

        if (existingDraft != null) {
            maltekstseksjonVersionRepository.delete(existingDraft)
        }

        return maltekstseksjonVersionRepository.save(
            existingVersion.createDraft()
        )
    }

    fun deleteMaltekstseksjon(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ) {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjon = maltekstseksjonRepository.getReferenceById(maltekstseksjonId)
        maltekstseksjon.deleted = true
    }

    fun deleteMaltekstseksjonDraftVersion(maltekstseksjonId: UUID, saksbehandlerIdent: String) {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)
        val existingDraft = maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        )
        if (existingDraft != null) {
            maltekstseksjonVersionRepository.delete(existingDraft)
        }
    }

    fun getCurrentMaltekstseksjonVersion(maltekstseksjonId: UUID): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        return maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(
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
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)

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
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.title = input
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateTexts(
        input: List<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.texts = input.map { textRepository.getReferenceById(UUID.fromString(it)) }
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateUtfall(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.utfallIdList = input
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateEnheter(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.enhetIdList = input
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateTemplateSectionList(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.templateSectionIdList = input
        maltekstseksjonVersion.editors += saksbehandlerIdent
        return maltekstseksjonVersion
    }

    fun updateYtelseHjemmelList(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
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
            maltekstseksjonVersions =
                maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonDeletedIsFalse()
        }

        logger.debug(
            "searchMaltekstseksjoner getting all texts took {} millis. Found {} texts",
            millis,
            maltekstseksjonVersions.size
        )

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
            val drafts =
                maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonDeletedIsFalse()
            //get published
            val published = maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonDeletedIsFalse()

            val draftsMaltekstseksjonList = drafts.map { it.maltekstseksjon }

            val publishedWithNoDrafts = published.filter { maltekstseksjonVersion ->
                maltekstseksjonVersion.maltekstseksjon !in draftsMaltekstseksjonList
            }

            maltekstseksjonVersions = drafts + publishedWithNoDrafts
        }

        logger.debug(
            "searchMaltekstseksjoner getting all maltekstseksjonVersions took {} millis. Found {} maltekstseksjonVersions",
            millis,
            maltekstseksjonVersions.size
        )

        return searchMaltekstseksjonService.searchMaltekstseksjoner(
            maltekstseksjonVersions = maltekstseksjonVersions,
            textIdList = textIdList,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
        )
    }

    fun getAllMaltekstseksjonVersions(): List<MaltekstseksjonVersion> = maltekstseksjonVersionRepository.findAll()
    fun getMaltekstseksjonVersionsById(ids: List<UUID>): MutableList<MaltekstseksjonVersion> =
        maltekstseksjonVersionRepository.findAllById(ids)

    fun updateAll(maltekstseksjonVersions: List<MaltekstseksjonVersion>): MutableList<MaltekstseksjonVersion> =
        maltekstseksjonVersionRepository.saveAll(maltekstseksjonVersions)

    private fun getCurrentDraft(maltekstseksjonId: UUID): MaltekstseksjonVersion {
        return maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        ) ?: throw ClientErrorException("no draft was found")
    }

    private fun validateIfMaltekstseksjonIsDeleted(maltekstseksjonId: UUID) {
        if (maltekstseksjonRepository.getReferenceById(maltekstseksjonId).deleted) {
            throw MaltekstseksjonNotFoundException("Maltekstseksjon $maltekstseksjonId is deleted.")
        }
    }
}