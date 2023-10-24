package no.nav.klage.texts.service

import com.fasterxml.jackson.databind.JsonNode
import no.nav.klage.texts.api.views.TextInput
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.exceptions.ClientErrorException
import no.nav.klage.texts.repositories.SearchTextService
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
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun publishTextVersion(textId: UUID, saksbehandlerIdent: String): TextVersion {
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

    fun getTextVersions(textId: UUID): List<TextVersion> =
        textVersionRepository.findByPublishedIsFalseAndPublishedDateTimeIsNotNullAndTextId(textId)

    fun createNewText(
        textInput: TextInput,
        saksbehandlerIdent: String,
    ): TextVersion {
        val now = LocalDateTime.now()

        val text = textRepository.save(
            Text(
                created = now,
                modified = now,
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
                textId = text.id,
                created = now,
                modified = now,
                publishedDateTime = null,
                published = false,
                publishedBy = null,
            )
        )
    }

    fun deleteText(
        textId: UUID,
        saksbehandlerIdent: String,
    ) {
        textRepository.getReferenceById(textId).deleted = true
    }

    fun getCurrentTextVersion(textId: UUID): TextVersion {
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
        if (content != null && plainText != null) {
            error("there can only be one of content or plainText")
        }

        val textVersion = getOrCreateCurrentDraft(textId)

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
        val textVersion = getOrCreateCurrentDraft(textId)
        textVersion.title = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateTextType(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        val textVersion = getOrCreateCurrentDraft(textId)
        textVersion.textType = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateSmartEditorVersion(
        input: Int,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        val textVersion = getOrCreateCurrentDraft(textId)
        textVersion.smartEditorVersion = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateContent(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        val textVersion = getOrCreateCurrentDraft(textId)
        textVersion.content = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updatePlainText(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        val textVersion = getOrCreateCurrentDraft(textId)
        textVersion.plainText = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateUtfall(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        val textVersion = getOrCreateCurrentDraft(textId)
        textVersion.utfallIdList = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateEnheter(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        val textVersion = getOrCreateCurrentDraft(textId)
        textVersion.enhetIdList = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateTemplateSectionList(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        val textVersion = getOrCreateCurrentDraft(textId)
        textVersion.templateSectionIdList = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun updateYtelseHjemmelList(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): TextVersion {
        val textVersion = getOrCreateCurrentDraft(textId)
        textVersion.ytelseHjemmelIdList = input
        textVersion.editors += saksbehandlerIdent
        return textVersion
    }

    fun searchTexts(
        textType: String?,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
    ): List<TextVersion> {
        var texts: List<TextVersion>

        val millis = measureTimeMillis {
            texts = textVersionRepository.findByPublishedIsTrue()
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

    fun getAllTexts(): List<TextVersion> = textVersionRepository.findAll()

    fun getTextsById(ids: List<UUID>): List<TextVersion> = textVersionRepository.findAllById(ids)

    private fun getOrCreateCurrentDraft(textId: UUID): TextVersion {
        val textVersionDraft =
            textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
                textId = textId
            )

        return if (textVersionDraft != null) {
            textVersionDraft
        } else {
            //Pick latest published version as template for the draft
            val template = textVersionRepository.findByPublishedIsTrueAndTextId(textId = textId)

            textVersionRepository.save(
                template.createDraft()
            )
        }
    }
}