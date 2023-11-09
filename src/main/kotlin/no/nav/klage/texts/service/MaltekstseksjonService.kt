package no.nav.klage.texts.service

import no.nav.klage.texts.api.views.MaltekstseksjonInput
import no.nav.klage.texts.api.views.VersionInput
import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.exceptions.ClientErrorException
import no.nav.klage.texts.exceptions.MaltekstseksjonNotFoundException
import no.nav.klage.texts.repositories.MaltekstseksjonRepository
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepository
import no.nav.klage.texts.repositories.TextRepository
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import no.nav.klage.texts.util.updateEditors
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
    private val publishService: PublishService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun publishMaltekstseksjonVersion(maltekstseksjonId: UUID, saksbehandlerIdent: String): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)
        return publishService.publishMaltekstseksjonVersion(
            maltekstseksjonId = maltekstseksjonId,
            saksbehandlerIdent = saksbehandlerIdent,
        )
    }

    fun publishMaltekstseksjonVersionWithTexts(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String
    ): Pair<MaltekstseksjonVersion, List<TextVersion>> {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)
        return publishService.publishMaltekstseksjonVersionWithTexts(
            maltekstseksjonId = maltekstseksjonId,
            saksbehandlerIdent = saksbehandlerIdent,
        )
    }

    fun getMaltekstseksjonVersions(maltekstseksjonId: UUID): List<MaltekstseksjonVersion> {
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
                createdBy = saksbehandlerIdent,
            )
        )

        return maltekstseksjonVersionRepository.save(
            MaltekstseksjonVersion(
                title = maltekstseksjonInput.title,
                utfallIdList = maltekstseksjonInput.utfallIdList,
                enhetIdList = maltekstseksjonInput.enhetIdList,
                templateSectionIdList = maltekstseksjonInput.templateSectionIdList,
                ytelseHjemmelIdList = maltekstseksjonInput.ytelseHjemmelIdList,
                editors = mutableSetOf(
                    Editor(
                        navIdent = saksbehandlerIdent,
                        created = now,
                        modified = now,
                    )
                ),
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
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)
        return publishService.createNewDraft(
            maltekstseksjonId = maltekstseksjonId,
            versionInput = versionInput,
            saksbehandlerIdent = saksbehandlerIdent,
        )
    }

    fun unpublishMaltekstseksjon(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ) {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val possiblePublishedTextVersion =
            maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(maltekstseksjonId)

        if (possiblePublishedTextVersion != null) {
            possiblePublishedTextVersion.published = false
        } else {
            throw ClientErrorException("fant ingen maltekstseksjon å avpublisere")
        }
    }

    fun deleteMaltekstseksjonDraftVersion(maltekstseksjonId: UUID, saksbehandlerIdent: String) {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)
        val existingDraft = maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        )
        if (existingDraft != null) {
            val maltekstseksjonVersions = getMaltekstseksjonVersions(maltekstseksjonId = maltekstseksjonId)

            maltekstseksjonVersionRepository.delete(existingDraft)

            if (maltekstseksjonVersions.size == 1) {
                maltekstseksjonRepository.deleteById(maltekstseksjonId)
            }
        }
    }

    fun getPublishedMaltekstseksjonVersion(maltekstseksjonId: UUID): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        return maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        ) ?: throw ClientErrorException("det fins hverken utkast eller publisert versjon")
    }


    fun getCurrentMaltekstseksjonVersion(maltekstseksjonId: UUID): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        return maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        ) ?: maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        ) ?: throw ClientErrorException("det fins hverken utkast eller publisert versjon")
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
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)

        maltekstseksjonVersion.apply {
            this.title = title
            this.texts.clear()
            this.texts.addAll(textIdList.map { textRepository.getReferenceById(UUID.fromString(it)) })
            this.utfallIdList = utfallIdList
            this.enhetIdList = enhetIdList
            this.templateSectionIdList = templateSectionIdList
            this.ytelseHjemmelIdList = ytelseHjemmelIdList
            this.modified = LocalDateTime.now()
            updateEditors(
                existingEditors = this.editors,
                newEditorNavIdent = saksbehandlerIdent
            )
        }

        return maltekstseksjonVersion
    }

    fun updateTitle(
        input: String,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.title = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = maltekstseksjonVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return maltekstseksjonVersion
    }

    fun updateTexts(
        input: List<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)

        maltekstseksjonVersion.texts.clear()
        maltekstseksjonVersion.texts.addAll(input.map { textRepository.getReferenceById(UUID.fromString(it)) })

        maltekstseksjonVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = maltekstseksjonVersion.editors,
            newEditorNavIdent = saksbehandlerIdent,
        )
        return maltekstseksjonVersion
    }

    fun updateUtfall(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.utfallIdList = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = maltekstseksjonVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return maltekstseksjonVersion
    }

    fun updateEnheter(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.enhetIdList = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = maltekstseksjonVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return maltekstseksjonVersion
    }

    fun updateTemplateSectionList(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.templateSectionIdList = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = maltekstseksjonVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return maltekstseksjonVersion
    }

    fun updateYtelseHjemmelList(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.ytelseHjemmelIdList = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = maltekstseksjonVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
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
                maltekstseksjonVersionRepository.findByPublishedIsTrue()
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
                maltekstseksjonVersionRepository.findByPublishedDateTimeIsNull()
            //get published
            val published = maltekstseksjonVersionRepository.findByPublishedIsTrue()

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
        ) ?: throw ClientErrorException("ikke noe utkast funnet")
    }

    private fun validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId: UUID) {
        if (maltekstseksjonVersionRepository.findByMaltekstseksjonId(maltekstseksjonId)
                .none { it.published || it.publishedDateTime == null }
        ) {
            throw MaltekstseksjonNotFoundException("Maltekstseksjon $maltekstseksjonId er avpublisert eller finnes ikke.")
        }
    }
}