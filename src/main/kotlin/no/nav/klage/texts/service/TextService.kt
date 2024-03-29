package no.nav.klage.texts.service

import com.fasterxml.jackson.databind.JsonNode
import no.nav.klage.texts.api.views.TextInput
import no.nav.klage.texts.api.views.VersionInput
import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.exceptions.ClientErrorException
import no.nav.klage.texts.exceptions.TextNotFoundException
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepository
import no.nav.klage.texts.repositories.TextRepository
import no.nav.klage.texts.repositories.TextVersionRepository
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
class TextService(
    private val textRepository: TextRepository,
    private val textVersionRepository: TextVersionRepository,
    private val searchTextService: SearchTextService,
    private val maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository,
    private val publishService: PublishService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun publishTextVersion(textId: UUID, saksbehandlerIdent: String): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)

        return publishService.publishTextVersion(
            textId = textId,
            saksbehandlerIdent = saksbehandlerIdent,
        )
    }

    fun getTextVersions(textId: UUID): List<TextVersion> {
        return textVersionRepository.findByTextId(textId)
            .sortedByDescending { it.publishedDateTime ?: LocalDateTime.now() }
    }

    fun createNewText(
        textInput: TextInput,
        saksbehandlerIdent: String,
    ): TextVersion {
        val now = LocalDateTime.now()

        val text = textRepository.save(
            Text(
                created = now,
                modified = now,
                maltekstseksjonVersions = mutableListOf(),
                createdBy = saksbehandlerIdent,
            )
        )

        return textVersionRepository.save(
            TextVersion(
                title = textInput.title,
                textType = textInput.textType,
                content = textInput.content?.toString(),
                plainText = textInput.plainText,
                smartEditorVersion = textInput.version,
                utfallIdList = textInput.utfallIdList ?: textInput.utfall,
                enhetIdList = textInput.enhetIdList ?: textInput.enheter,
                templateSectionIdList = textInput.templateSectionIdList ?: textInput.templateSectionList,
                ytelseHjemmelIdList = textInput.ytelseHjemmelIdList ?: textInput.ytelseHjemmelList,
                editors = mutableSetOf(
                    Editor(
                        navIdent = saksbehandlerIdent,
                        created = now,
                        modified = now,
                    )
                ),
                text = text,
                created = now,
                modified = now,
                publishedDateTime = null,
                published = false,
                publishedBy = null,
            )
        )
    }

    fun createNewDraft(
        textId: UUID,
        versionInput: VersionInput?,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)

        val existingVersion = if (versionInput != null) {
            textVersionRepository.getReferenceById(versionInput.versionId)
        } else {
            textVersionRepository.findByPublishedIsTrueAndTextId(
                textId = textId
            ) ?: throw ClientErrorException("det må finnes en publisert versjon før et nytt utkast kan lages")
        }

        val existingDraft = textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        )

        return if (existingDraft != null) {
            //Reset draft
            existingDraft.resetDraftWithValuesFrom(existingVersion)
            existingDraft
        } else {
            textVersionRepository.save(
                existingVersion.createDraft()
            )
        }

    }

    fun unpublishText(
        textId: UUID,
        saksbehandlerIdent: String,
    ): List<MaltekstseksjonVersion> {
        val text = textRepository.getReferenceById(textId)

        val affectedMaltekstseksjonVersionsGroupedByMaltekstseksjonId = text.maltekstseksjonVersions.filter { mv ->
            (mv.publishedDateTime == null || mv.published)
        }.groupBy { it.maltekstseksjon.id }

        val notAffectedMaltekstseksjonVersions = text.maltekstseksjonVersions.filter { mv ->
            (mv.publishedDateTime != null && !mv.published)
        }

        //Only published version is OK when creating new draft, remove text and publish
        val publishedMaltekstseksjonVersionsToReturn =
            affectedMaltekstseksjonVersionsGroupedByMaltekstseksjonId.filter { it.value.size == 1 && it.value.first().published }
                .map { (maltekstseksjonId, _) ->
                    val draft = publishService.createNewDraft(
                        maltekstseksjonId = maltekstseksjonId,
                        versionInput = null,
                        saksbehandlerIdent = saksbehandlerIdent,
                    )

                    draft.texts.removeIf { it.id == textId }

                    publishService.publishMaltekstseksjonVersion(
                        maltekstseksjonId = maltekstseksjonId,
                        saksbehandlerIdent = saksbehandlerIdent
                    )
                }

        //Only draft, should only update draft, not publish
        val maltekstseksjonVersionDraftsToReturn =
            affectedMaltekstseksjonVersionsGroupedByMaltekstseksjonId.filter { it.value.size == 1 && it.value.first().publishedDateTime == null }
                .map { (_, maltekstseksjonVersions) ->
                    val draft = maltekstseksjonVersions.first()
                    draft.texts.removeIf { it.id == textId }
                    draft
                }

        //published AND draft, keep old draft (same id), but also create new draft and publish.
        val publishedAndDraftMaltekstseksjonVersionsToReturn =
            affectedMaltekstseksjonVersionsGroupedByMaltekstseksjonId.filter { it.value.size > 1 }
                .map { (maltekstseksjonId, maltekstseksjonVersions) ->
                    //get current draft
                    val draft = maltekstseksjonVersions.find { it.publishedDateTime == null }!!
                    //fix current draft
                    draft.texts.removeIf { it.id == textId }
                    draft.modified = LocalDateTime.now()

                    //get currently published version
                    val published = maltekstseksjonVersions.find { it.published }!!

                    //Create this draft only to be able to publish changed version with removed text.
                    var tempDraft = published.createDraft()
                    tempDraft.texts.removeIf { it.id == textId }

                    tempDraft = maltekstseksjonVersionRepository.save(tempDraft)

                    listOf(
                        publishService.publishMaltekstseksjonVersion(
                            maltekstseksjonId = maltekstseksjonId,
                            saksbehandlerIdent = saksbehandlerIdent,
                            overrideDraft = tempDraft,
                        ), draft
                    )
                }

        //unpublish text
        val possiblePublishedTextVersion = textVersionRepository.findByPublishedIsTrueAndTextId(textId)
        if (possiblePublishedTextVersion != null) {
            possiblePublishedTextVersion.published = false
            possiblePublishedTextVersion.modified = LocalDateTime.now()
        } else {
            throw ClientErrorException("fant ingen tekst å avpublisere")
        }

        return publishedMaltekstseksjonVersionsToReturn +
                maltekstseksjonVersionDraftsToReturn +
                publishedAndDraftMaltekstseksjonVersionsToReturn.flatten() +
                notAffectedMaltekstseksjonVersions
    }

    fun deleteTextDraftVersion(textId: UUID, saksbehandlerIdent: String) {
        val existingDraft = textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        )

        if (existingDraft != null) {
            val textVersions = getTextVersions(textId)

            textVersionRepository.delete(existingDraft)

            //if only draft, delete text and references to maltekstseksjoner.
            if (textVersions.size == 1) {
                existingDraft.text.maltekstseksjonVersions.filter { mv ->
                    (mv.publishedDateTime == null)
                }.forEach { mv ->
                    mv.texts.removeIf { it.id == textId }
                }

                textRepository.deleteById(textId)
            }

        } else {
            throw ClientErrorException("fant ikke utkast")
        }
    }

    fun getPublishedTextVersion(textId: UUID): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        return textVersionRepository.findByPublishedIsTrueAndTextId(
            textId = textId
        ) ?: throw ClientErrorException("fant ingen publisert tekst")
    }

    fun getCurrentTextVersion(textId: UUID): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)

        return textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        ) ?: textVersionRepository.findByPublishedIsTrueAndTextId(
            textId = textId
        ) ?: throw ClientErrorException("det fins hverken utkast eller publisert versjon")
    }

    fun updateText(
        textId: UUID,
        saksbehandlerIdent: String,
        title: String,
        textType: String,
        content: JsonNode?,
        plainText: String?,
        utfallIdList: Set<String>,
        enhetIdList: Set<String>,
        templateSectionIdList: Set<String>,
        ytelseHjemmelIdList: Set<String>,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        if (content != null && plainText != null) {
            throw ClientErrorException("Kun 'content' eller 'plainText' kan sendes inn. Ikke begge samtidig.")
        }

        val textVersion = getCurrentDraft(textId)

        textVersion.apply {
            this.title = title
            this.textType = textType
            this.content = content.toString()
            this.plainText = plainText
            this.utfallIdList = utfallIdList
            this.enhetIdList = enhetIdList
            this.templateSectionIdList = templateSectionIdList
            this.ytelseHjemmelIdList = ytelseHjemmelIdList
            updateEditors(
                existingEditors = this.editors,
                newEditorNavIdent = saksbehandlerIdent
            )
            this.modified = LocalDateTime.now()
        }

        return textVersion
    }

    fun updateTitle(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.title = input
        textVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = textVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return textVersion
    }

    fun updateTextType(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.textType = input
        textVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = textVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return textVersion
    }

    fun updateSmartEditorVersion(
        input: Int,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.smartEditorVersion = input
        textVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = textVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return textVersion
    }

    fun updateContent(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.content = input
        textVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = textVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return textVersion
    }

    fun updatePlainText(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.plainText = input
        textVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = textVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return textVersion
    }

    fun updateUtfall(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.utfallIdList = input
        textVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = textVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return textVersion
    }

    fun updateEnheter(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.enhetIdList = input
        textVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = textVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return textVersion
    }

    fun updateTemplateSectionList(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.templateSectionIdList = input
        textVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = textVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return textVersion
    }

    fun updateYtelseHjemmelList(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsUnpublishedOrMissingDraft(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.ytelseHjemmelIdList = input
        textVersion.modified = LocalDateTime.now()
        updateEditors(
            existingEditors = textVersion.editors,
            newEditorNavIdent = saksbehandlerIdent
        )
        return textVersion
    }

    fun searchPublishedTextVersions(
        textType: String?,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
    ): List<TextVersion> {
        val textVersions: List<TextVersion>

        val millis = measureTimeMillis {
            textVersions = textVersionRepository.findByPublishedIsTrue()
        }

        logger.debug("findByPublishedIsTrue took {} millis. Found {} texts", millis, textVersions.size)

        return searchTextService.searchTexts(
            texts = textVersions,
            textType = textType,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
        )
    }

    fun searchTextVersion(
        textType: String?,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
    ): List<TextVersion> {
        var texts: List<TextVersion>

        val millis = measureTimeMillis {
            //get all drafts
            val drafts = textVersionRepository.findByPublishedDateTimeIsNull()
            //get published
            val published = textVersionRepository.findByPublishedIsTrue()

            val draftsTextList = drafts.map { it.text }

            val publishedWithNoDrafts = published.filter { textVersion ->
                textVersion.text !in draftsTextList
            }

            texts = drafts + publishedWithNoDrafts
        }

        logger.debug("combining all published texts and all drafts took {} millis. Found {} texts", millis, texts.size)

        return searchTextService.searchTexts(
            texts = texts,
            textType = textType,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
        )
    }

    fun getAllTextVersions(): List<TextVersion> = textVersionRepository.findAll()

    fun getTextVersionsById(ids: List<UUID>): MutableList<TextVersion> = textVersionRepository.findAllById(ids)

    fun updateAll(textVersions: List<TextVersion>): MutableList<TextVersion> =
        textVersionRepository.saveAll(textVersions)

    fun getConnectedMaltekstseksjoner(textId: UUID): Pair<List<UUID>, List<UUID>> {
        val outcome: Pair<List<UUID>, List<UUID>> = maltekstseksjonVersionRepository.findConnectedMaltekstseksjonPublishedIdList(textId) to maltekstseksjonVersionRepository.findConnectedMaltekstseksjonDraftsIdList(
            textId
        )

        if (outcome.first.isNotEmpty() || outcome.second.isNotEmpty()) {
            logger.debug(
                "getConnectedMaltekstseksjoner got results. First size: {}, second size: {}",
                outcome.first.size,
                outcome.second.size
            )
        }

        return outcome
    }

    private fun getCurrentDraft(textId: UUID): TextVersion {
        return textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        ) ?: throw ClientErrorException("Utkast ikke funnet")
    }

    private fun validateIfTextIsUnpublishedOrMissingDraft(textId: UUID) {
        if (textVersionRepository.findByTextId(textId).none { it.published || it.publishedDateTime == null }) {
            throw TextNotFoundException("Teksten $textId er avpublisert eller finnes ikke.")
        }
    }
}