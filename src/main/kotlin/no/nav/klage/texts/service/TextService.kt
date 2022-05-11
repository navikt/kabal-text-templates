package no.nav.klage.texts.service

import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.repositories.TextRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TextService(private val textRepository: TextRepository) {

    fun createText(text: Text): Text = textRepository.save(text)

    fun deleteText(textId: UUID) = textRepository.deleteById(textId)

    fun getText(textId: UUID): Text = textRepository.getById(textId)

    fun updateText(text: Text): Text = textRepository.save(text)

    fun searchTexts(
        type: String?,
        utfall: List<String>,
        ytelser: List<String>,
        hjemler: List<String>,
        enheter: List<String>,
    ): List<Text> {
        return textRepository.searchTexts(
            type = type,
            utfall = utfall,
            ytelser = ytelser,
            hjemler = hjemler,
            enheter = enheter,
        )
    }
}