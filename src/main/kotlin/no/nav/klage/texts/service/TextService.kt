package no.nav.klage.texts.service

import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.domain.TextAggregateFunctions.logCreation
import no.nav.klage.texts.domain.TextAggregateFunctions.updateContent
import no.nav.klage.texts.domain.TextAggregateFunctions.updateEnheter
import no.nav.klage.texts.domain.TextAggregateFunctions.updateHjemler
import no.nav.klage.texts.domain.TextAggregateFunctions.updateSections
import no.nav.klage.texts.domain.TextAggregateFunctions.updateTitle
import no.nav.klage.texts.domain.TextAggregateFunctions.updateType
import no.nav.klage.texts.domain.TextAggregateFunctions.updateUtfall
import no.nav.klage.texts.domain.TextAggregateFunctions.updateYtelser
import no.nav.klage.texts.repositories.TextRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@Service
class TextService(
    private val textRepository: TextRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

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

    fun deleteText(textId: UUID) {
        textRepository.deleteById(textId)
    }

    fun getText(textId: UUID): Text = textRepository.getById(textId)

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

    fun updateType(
        input: String,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateType(
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

    fun updateHjemler(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateHjemler(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun updateYtelser(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateYtelser(
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

    fun updateSections(
        input: Set<String>,
        textId: UUID,
        saksbehandlerIdent: String,
    ): Text {
        val text = getText(textId)
        val event =
            text.updateSections(
                input,
                saksbehandlerIdent,
            )
        applicationEventPublisher.publishEvent(event)
        return text
    }

    fun searchTexts(
        textType: String?,
        utfall: List<String>,
        ytelser: List<String>,
        hjemler: List<String>,
        enheter: List<String>,
        sections: List<String>,
    ): List<Text> {
        return textRepository.searchTexts(
            textType = textType,
            utfall = utfall,
            ytelser = ytelser,
            hjemler = hjemler,
            enheter = enheter,
            sections = sections,
        )
    }
}