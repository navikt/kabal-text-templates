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
        validateIfTextIsUnpublished(textId)

        return publishService.publishTextVersion(
            textId = textId,
            saksbehandlerIdent = saksbehandlerIdent,
        )
    }

    fun getTextVersions(textId: UUID): List<TextVersion> {
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)

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

    fun deleteOrUnpublishText(
        textId: UUID,
        saksbehandlerIdent: String,
    ): List<MaltekstseksjonVersion> {
        validateIfTextIsUnpublished(textId)
        val text = textRepository.getReferenceById(textId)

        val affectedMaltekstseksjonVersionsGroupedByMaltekstseksjonId = text.maltekstseksjonVersions.filter { mv ->
            (mv.publishedDateTime == null || mv.published)
        }.groupBy { it.maltekstseksjon.id }

        val notAffectedMaltekstseksjonVersions = text.maltekstseksjonVersions.filter { mv ->
            (mv.publishedDateTime != null && !mv.published)
        }

        //Only published version is OK when creating new draft, remove text and publish
        val publishedToReturn =
            affectedMaltekstseksjonVersionsGroupedByMaltekstseksjonId.filter { it.value.size == 1 && it.value.first().published }
                .map { (maltekstseksjonId, _) ->
                    val draft = publishService.createNewDraft(
                        maltekstseksjonId = maltekstseksjonId,
                        versionInput = null,
                        saksbehandlerIdent = saksbehandlerIdent,
                    )

                    draft.texts.removeIf { it.id == text.id }

                    publishService.publishMaltekstseksjonVersion(
                        maltekstseksjonId = maltekstseksjonId,
                        saksbehandlerIdent = saksbehandlerIdent
                    )
                }

        //Only draft, should only update draft, not publish
        val draftsToReturn =
            affectedMaltekstseksjonVersionsGroupedByMaltekstseksjonId.filter { it.value.size == 1 && it.value.first().publishedDateTime == null }
                .map { (_, maltekstseksjonVersions) ->
                    val draft = maltekstseksjonVersions.first()
                    draft.texts.removeIf { it.id == text.id }
                    draft
                }

        //published AND draft, keep old draft (same id), but also create new draft and publish.
        val publishedAndDraftToReturn =
            affectedMaltekstseksjonVersionsGroupedByMaltekstseksjonId.filter { it.value.size > 1 }
                .map { (maltekstseksjonId, maltekstseksjonVersions) ->
                    //get current draft
                    val draft = maltekstseksjonVersions.find { it.publishedDateTime == null }!!
                    //fix current draft
                    draft.texts.removeIf { it.id == text.id }
                    draft.modified = LocalDateTime.now()

                    //get currently published version
                    val published = maltekstseksjonVersions.find { it.published }!!

                    //Create this draft only to be able to publish changed version with removed text.
                    var tempDraft = published.createDraft()
                    tempDraft.texts.removeIf { it.id == text.id }

                    tempDraft = maltekstseksjonVersionRepository.save(tempDraft)

                    listOf(
                        publishService.publishMaltekstseksjonVersion(
                            maltekstseksjonId = maltekstseksjonId,
                            saksbehandlerIdent = saksbehandlerIdent,
                            overrideDraft = tempDraft,
                        ), draft
                    )
                }

        //unpublish and or delete draft text
        val possibleDraft = textVersionRepository.findByPublishedDateTimeIsNullAndTextId(textId)
        val possiblePublishedTextVersion = textVersionRepository.findByPublishedIsTrueAndTextId(textId)

        if (possibleDraft != null) {
            textVersionRepository.delete(possibleDraft)
        }

        if (possiblePublishedTextVersion != null) {
            possiblePublishedTextVersion.published = false
        } else if (notAffectedMaltekstseksjonVersions.isEmpty()) {
            //If no published version of text, and no references to previously published maltekstseksjoner (this is weird for drafts), delete the whole text.
            textRepository.deleteById(textId)
        }

        return publishedToReturn + draftsToReturn + publishedAndDraftToReturn.flatten()
    }

    fun deleteTextDraftVersion(textId: UUID, saksbehandlerIdent: String) {
        validateIfTextIsUnpublished(textId = textId)
        val existingDraft = textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        )
        if (existingDraft != null) {
            textVersionRepository.delete(existingDraft)
        }
    }

    fun getPublishedTextVersion(textId: UUID): TextVersion {
        validateIfTextIsUnpublished(textId)
        return textVersionRepository.findByPublishedIsTrueAndTextId(
            textId = textId
        ) ?: throw ClientErrorException("fant ingen publisert maltekst")
    }

    fun getCurrentTextVersion(textId: UUID): TextVersion {
        validateIfTextIsUnpublished(textId)

        return textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        ) ?: textVersionRepository.findByPublishedIsTrueAndTextId(
            textId = textId
        ) ?: throw ClientErrorException("det finnes ikke hverken utkast eller en publisert versjon")
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
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)
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
        validateIfTextIsUnpublished(textId)
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
        var textVersions: List<TextVersion>

        val millis = measureTimeMillis {
            textVersions = textVersionRepository.findByPublishedIsTrue()
        }

        logger.debug("searchTexts getting all texts took {} millis. Found {} texts", millis, textVersions.size)

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

        logger.debug("searchTexts getting all texts took {} millis. Found {} texts", millis, texts.size)

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
        return maltekstseksjonVersionRepository.findConnectedMaltekstseksjonPublishedIdList(textId) to maltekstseksjonVersionRepository.findConnectedMaltekstseksjonDraftsIdList(
            textId
        )
    }

    private fun getCurrentDraft(textId: UUID): TextVersion {
        return textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        ) ?: throw ClientErrorException("Utkast ikke funnet")
    }

    private fun validateIfTextIsUnpublished(textId: UUID) {
        if (textVersionRepository.findByTextId(textId).none { it.published || it.publishedDateTime == null }) {
            throw TextNotFoundException("Teksten $textId er avpublisert eller finnes ikke.")
        }
    }
}