package no.nav.klage.texts.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import no.nav.klage.texts.api.views.SearchQueryParams
import no.nav.klage.texts.api.views.TextInput
import no.nav.klage.texts.api.views.TextView
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.service.TextService
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@Api(tags = ["kabal-text-templates"])
@RequestMapping("/texts")
class TextController(
    private val textService: TextService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @ApiOperation(
        value = "Create text",
        notes = "Create text"
    )
    @PostMapping
    fun createText(
        @RequestBody input: TextInput
    ): TextView {
        logger.debug("createText: $input")
        return mapToTextView(
            textService.createText(input.toDomainModel())
        )
    }

    @ApiOperation(
        value = "Update text",
        notes = "Update text"
    )
    @PutMapping("/{textId}")
    fun updateText(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: TextInput
    ): TextView {
        logger.debug("updateText called with id $textId and $input")

        val text = textService.getText(textId).apply {
            title = input.title
            type = input.type
            content = input.content
            hjemler = input.hjemler
            ytelser = input.ytelser
            utfall = input.utfall
            enheter = input.enheter
            sections = input.sections
            modified = LocalDateTime.now()
        }

        return mapToTextView(textService.updateText(text))
    }

    @ApiOperation(
        value = "Delete text",
        notes = "Delete text"
    )
    @DeleteMapping("/{textId}")
    fun deleteText(
        @PathVariable("textId") textId: UUID,
    ) {
        logger.debug("deleteText called with id $textId")
        textService.deleteText(textId)
    }

    @ApiOperation(
        value = "Search texts",
        notes = "Search texts"
    )
    @GetMapping
    fun searchTexts(
        searchQueryParams: SearchQueryParams
    ): List<TextView> {
        logger.debug("searchTexts called with params: $searchQueryParams")
        val texts = textService.searchTexts(
            type = searchQueryParams.type,
            utfall = searchQueryParams.utfall ?: emptyList(),
            ytelser = searchQueryParams.ytelser ?: emptyList(),
            hjemler = searchQueryParams.hjemler ?: emptyList(),
            enheter = searchQueryParams.enheter ?: emptyList(),
            sections = searchQueryParams.sections ?: emptyList(),
        )
        return texts.map {
            mapToTextView(it)
        }
    }

    @ApiOperation(
        value = "Get text",
        notes = "Get text"
    )
    @GetMapping("/{textId}")
    fun getText(
        @PathVariable("textId") textId: UUID,
    ): TextView {
        logger.debug("getText called with id $textId")
        return mapToTextView(textService.getText(textId))
    }

    private fun mapToTextView(text: Text): TextView =
        TextView(
            id = text.id,
            title = text.title,
            type = text.type,
            content = jsonMapper().readTree(text.content),
            hjemler = text.hjemler,
            ytelser = text.ytelser,
            utfall = text.utfall,
            enheter = text.enheter,
            sections = text.sections,
            created = text.created,
            modified = text.modified,
        )

    private fun TextInput.toDomainModel() = Text(
        title = title,
        type = type,
        content = content,
        hjemler = hjemler,
        ytelser = ytelser,
        utfall = utfall,
        enheter = enheter,
        sections = sections,
        created = LocalDateTime.now(),
    )
}

