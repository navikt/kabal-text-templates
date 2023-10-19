package no.nav.klage.texts.service

import com.fasterxml.jackson.databind.JsonNode
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.domain.TextAggregateFunctions.logCreation
import no.nav.klage.texts.domain.TextAggregateFunctions.logDeletion
import no.nav.klage.texts.domain.TextAggregateFunctions.updateContent
import no.nav.klage.texts.domain.TextAggregateFunctions.updateEnheter
import no.nav.klage.texts.domain.TextAggregateFunctions.updatePlainText
import no.nav.klage.texts.domain.TextAggregateFunctions.updateSmartEditorVersion
import no.nav.klage.texts.domain.TextAggregateFunctions.updateTemplateSectionList
import no.nav.klage.texts.domain.TextAggregateFunctions.updateTextType
import no.nav.klage.texts.domain.TextAggregateFunctions.updateTitle
import no.nav.klage.texts.domain.TextAggregateFunctions.updateUtfall
import no.nav.klage.texts.domain.TextAggregateFunctions.updateYtelseHjemmelList
import no.nav.klage.texts.exceptions.TextNotFoundException
import no.nav.klage.texts.repositories.SearchTextRepository
import no.nav.klage.texts.repositories.TextRepository
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@Service
class TextService(
    private val textRepository: TextRepository,
    private val searchTextRepository: SearchTextRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun createText(
        text: Text,
        saksbehandlerIdent: String,
    ): Text {
        textRepository.save(text)
        val event = text.logCreation(
            text,
            saksbehandlerIdent
        )

        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun deleteText(
        textId: UUID,
        saksbehandlerIdent: String,
    ) {
        val text = getText(textId)
        val event = text.logDeletion(
            text,
            saksbehandlerIdent
        )
        textRepository.deleteById(textId)
        applicationEventPublisher.publishEvent(event)
    }

    fun getText(textId: UUID): Text = textRepository.findById(textId)
        .orElseThrow { TextNotFoundException("Text with id $textId not found") }

    fun updateText(
        textId: UUID,
        saksbehandlerIdent: String,
        title: String,
        textType: String,
        content: JsonNode?,
        plainText: String?,
        utfall: Set<String>,
        enheter: Set<String>,
        templateSectionList: Set<String>,
        ytelseHjemmelList: Set<String>,
    ): Text {
        if (content != null && plainText != null) {
            error("there can only be one of content or plainText")
        }

        val text = getText(textId)

        applicationEventPublisher.publishEvent(
            text.updateTitle(
                newValueTitle = title,
                saksbehandlerident = saksbehandlerIdent
            )
        )

        applicationEventPublisher.publishEvent(
            text.updateTextType(
                newValueTextType = textType,
                saksbehandlerident = saksbehandlerIdent
            )
        )

        if (content != null) {
            applicationEventPublisher.publishEvent(
                text.updateContent(
                    newValueContent = content.toString(),
                    saksbehandlerident = saksbehandlerIdent
                )
            )
        }

        if (plainText != null) {
            applicationEventPublisher.publishEvent(
                text.updatePlainText(
                    newValuePlainText = plainText,
                    saksbehandlerident = saksbehandlerIdent
                )
            )
        }

        applicationEventPublisher.publishEvent(
            text.updateUtfall(
                newValueUtfall = utfall,
                saksbehandlerident = saksbehandlerIdent
            )
        )

        applicationEventPublisher.publishEvent(
            text.updateEnheter(
                newValueEnheter = enheter,
                saksbehandlerident = saksbehandlerIdent
            )
        )

        applicationEventPublisher.publishEvent(
            text.updateTemplateSectionList(
                newValueTemplateSectionList = templateSectionList,
                saksbehandlerident = saksbehandlerIdent
            )
        )

        applicationEventPublisher.publishEvent(
            text.updateYtelseHjemmelList(
                newValueYtelseHjemmelList = ytelseHjemmelList,
                saksbehandlerident = saksbehandlerIdent
            )
        )

        return text
    }

    fun updateTitle(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateTitle(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun updateTextType(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateTextType(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun updateSmartEditorVersion(
        input: Int,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateSmartEditorVersion(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun updateContent(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateContent(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun updatePlainText(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updatePlainText(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun updateUtfall(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateUtfall(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun updateEnheter(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateEnheter(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun updateTemplateSectionList(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateTemplateSectionList(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun updateYtelseHjemmelList(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateYtelseHjemmelList(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun searchTexts(
        textType: String?,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
    ): List<Text> {
        return searchTextRepository.searchTexts(
            textType = textType,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
        )
    }

    fun getAllTexts(): List<Text> = textRepository.findAll()

    fun getTextsById(ids: List<UUID>): List<Text> = textRepository.findAllById(ids)

    fun updateAll(texts: List<Text>): List<Text> = textRepository.saveAll(texts)
}