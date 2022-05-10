package no.nav.klage.texts.service

import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.repositories.TextRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TextService(private val textRepository: TextRepository) {

    fun createText(text: Text): Text {
        return textRepository.save(text)
    }

    fun deleteText(textId: UUID) {
        textRepository.deleteById(textId)
    }

    fun getText(textId: UUID) = textRepository.getById(textId)

    fun getTexts() = textRepository.findAll()
}