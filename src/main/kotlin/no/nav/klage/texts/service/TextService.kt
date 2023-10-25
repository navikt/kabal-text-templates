package no.nav.klage.texts.service

import com.fasterxml.jackson.databind.JsonNode
import no.nav.klage.texts.api.views.TextInput
import no.nav.klage.texts.api.views.VersionInput
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.exceptions.ClientErrorException
import no.nav.klage.texts.exceptions.TextNotFoundException
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepository
import no.nav.klage.texts.repositories.TextRepository
import no.nav.klage.texts.repositories.TextVersionRepository
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
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
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun publishTextVersion(textId: UUID, saksbehandlerIdent: String): TextVersion {
        validateIfTextIsDeleted(textId)

        textVersionRepository.findByPublishedIsTrueAndTextId(textId).published = false

        val textVersionDraft =
            textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
                textId = textId
            ) ?: throw ClientErrorException("there was no draft to publish")

        textVersionDraft.publishedDateTime = LocalDateTime.now()
        textVersionDraft.published = true
        textVersionDraft.publishedBy = saksbehandlerIdent

        return textVersionDraft
    }

    fun getTextVersions(textId: UUID): List<TextVersion> {
        validateIfTextIsDeleted(textId)
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
//                maltekstseksjonVersionList = emptyList()
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
                editors = setOf(saksbehandlerIdent),
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
        validateIfTextIsDeleted(textId)
        val existingVersion = if (versionInput != null) {
            textVersionRepository.getReferenceById(versionInput.versionId)
        } else {
            textVersionRepository.findByPublishedIsTrueAndTextId(
                textId = textId
            )
        }

        val existingDraft = textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        )

        if (existingDraft != null) {
            textVersionRepository.delete(existingDraft)
        }

        return textVersionRepository.save(
            existingVersion.createDraft()
        )
    }

    fun deleteText(
        textId: UUID,
        saksbehandlerIdent: String,
    ) {
        validateIfTextIsDeleted(textId)
        textRepository.getReferenceById(textId).deleted = true
    }

    fun deleteTextDraftVersion(textId: UUID, saksbehandlerIdent: String) {
        validateIfTextIsDeleted(textId = textId)
        val existingDraft = textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        )
        if (existingDraft != null) {
            textVersionRepository.delete(existingDraft)
        }
    }

    fun getCurrentTextVersion(textId: UUID): TextVersion {
        validateIfTextIsDeleted(textId)
        return textVersionRepository.findByPublishedIsTrueAndTextId(
            textId = textId
        )
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
        validateIfTextIsDeleted(textId)
        if (content != null && plainText != null) {
            error("there can only be one of content or plainText")
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
            this.editors += saksbehandlerIdent
        }

        return textVersion
    }

    fun updateTitle(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsDeleted(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.title = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateTextType(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsDeleted(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.textType = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateSmartEditorVersion(
        input: Int,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsDeleted(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.smartEditorVersion = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateContent(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsDeleted(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.content = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updatePlainText(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsDeleted(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.plainText = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateUtfall(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsDeleted(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.utfallIdList = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateEnheter(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsDeleted(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.enhetIdList = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateTemplateSectionList(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsDeleted(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.templateSectionIdList = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateYtelseHjemmelList(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        validateIfTextIsDeleted(textId)
        val textVersion = getCurrentDraft(textId)
        textVersion.ytelseHjemmelIdList = input
        textVersion.editors += saksbehandlerIdent
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
            textVersions = textVersionRepository.findByPublishedIsTrueAndTextDeletedIsFalse()
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
            val drafts = textVersionRepository.findByPublishedDateTimeIsNullAndTextDeletedIsFalse()
            //get published
            val published = textVersionRepository.findByPublishedIsTrueAndTextDeletedIsFalse()

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
//        return maltekstseksjonVersionRepository.findConnectedMaltekstseksjonPublishedIdList(textId) to maltekstseksjonVersionRepository.findConnectedMaltekstseksjonDraftsIdList(
//            textId
//        )
        return (emptyList<UUID>() to emptyList())
    }

    private fun getCurrentDraft(textId: UUID): TextVersion {
        return textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
            textId = textId
        ) ?: throw ClientErrorException("no draft was found")
    }

    private fun validateIfTextIsDeleted(textId: UUID) {
        if (textRepository.getReferenceById(textId).deleted) {
            throw TextNotFoundException("Text $textId is deleted.")
        }
    }
}