package no.nav.klage.texts.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.exceptions.ClientErrorException
import no.nav.klage.texts.service.TextService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import no.nav.klage.texts.util.logTextMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@Api(tags = ["kabal-text-templates"])
@RequestMapping("/texts")
@ProtectedWithClaims(issuer = ISSUER_AAD)
class TextController(
    private val textService: TextService,
    private val tokenUtil: TokenUtil,
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
        logTextMethodDetails(
            methodName = ::createText.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = null,
            logger = logger,
        )

        return mapToTextView(
            textService.createText(
                text = input.toDomainModel(),
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update title",
        notes = "Update title"
    )
    @PutMapping("/{textId}/title")
    fun updateTitle(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: TitleInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateTitle.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateTitle(
                input = input.title,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update text type",
        notes = "Update text type"
    )
    @PutMapping("/{textId}/texttype")
    fun updateTextType(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: TextTypeInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateTextType.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateTextType(
                input = input.textType,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update content",
        notes = "Update content"
    )
    @PutMapping("/{textId}/content")
    fun updateContent(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: ContentInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateContent.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateContent(
                input = input.content.toString(),
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update plainText",
        notes = "Update plainText"
    )
    @PutMapping("/{textId}/plaintext")
    fun updatePlainText(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: PlainTextInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updatePlainText.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updatePlainText(
                input = input.plainText,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update smartEditorVersion",
        notes = "Update smartEditorVersion"
    )
    @PutMapping("/{textId}/version")
    fun updateSmartEditorVersion(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: SmartEditorVersionInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateSmartEditorVersion.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateSmartEditorVersion(
                input = input.version,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update hjemler",
        notes = "Update hjemler"
    )
    @PutMapping("/{textId}/hjemler")
    fun updateHjemler(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: HjemlerInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateHjemler.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateHjemler(
                input = input.hjemler,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update ytelser",
        notes = "Update ytelser"
    )
    @PutMapping("/{textId}/ytelser")
    fun updateYtelser(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: YtelserInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateYtelser.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateYtelser(
                input = input.ytelser,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update utfall",
        notes = "Update utfall"
    )
    @PutMapping("/{textId}/utfall")
    fun updateUtfall(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: UtfallInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateUtfall.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateUtfall(
                input = input.utfall,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update enheter",
        notes = "Update enheter"
    )
    @PutMapping("/{textId}/enheter")
    fun updateEnheter(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: EnheterInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateEnheter.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateEnheter(
                input = input.enheter,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update sections",
        notes = "Update sections"
    )
    @PutMapping("/{textId}/sections")
    fun updateSections(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: SectionsInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateSections.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateSections(
                input = input.sections,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Update templates",
        notes = "Update templates"
    )
    @PutMapping("/{textId}/templates")
    fun updateTemplates(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: TemplatesInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateTemplates.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateTemplates(
                input = input.templates,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @ApiOperation(
        value = "Delete text",
        notes = "Delete text"
    )
    @DeleteMapping("/{textId}")
    fun deleteText(
        @PathVariable("textId") textId: UUID,
    ) {
        logTextMethodDetails(
            methodName = ::deleteText.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        textService.deleteText(
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
        )
    }

    @ApiOperation(
        value = "Search texts",
        notes = "Search texts"
    )
    @GetMapping
    fun searchTexts(
        searchQueryParams: SearchQueryParams
    ): List<TextView> {
        logTextMethodDetails(
            methodName = ::searchTexts.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = null,
            logger = logger,
        )

        if (searchQueryParams.requiredSection != null && !searchQueryParams.sections.isNullOrEmpty()) {
            throw ClientErrorException("Cannot use both 'requiredSection' and 'sections' when searching.")
        }

        val texts = textService.searchTexts(
            textType = searchQueryParams.textType,
            requiredSection = searchQueryParams.requiredSection,
            utfall = searchQueryParams.utfall ?: emptyList(),
            ytelser = searchQueryParams.ytelser ?: emptyList(),
            hjemler = searchQueryParams.hjemler ?: emptyList(),
            enheter = searchQueryParams.enheter ?: emptyList(),
            sections = searchQueryParams.sections ?: emptyList(),
            templates = searchQueryParams.templates ?: emptyList(),
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
        logTextMethodDetails(
            methodName = ::getText.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )
        return mapToTextView(textService.getText(textId))
    }

    private fun TextInput.toDomainModel() = Text(
        title = title,
        textType = textType,
        content = content?.toString(),
        plainText = plainText,
        smartEditorVersion = version,
        hjemler = hjemler,
        ytelser = ytelser,
        utfall = utfall,
        enheter = enheter,
        sections = sections,
        created = LocalDateTime.now(),
    )
}

fun mapToTextView(text: Text): TextView =
    TextView(
        id = text.id,
        title = text.title,
        textType = text.textType,
        content = if (text.content != null) jsonMapper().readTree(text.content) else null,
        plainText = text.plainText,
        version = text.smartEditorVersion,
        hjemler = text.hjemler,
        ytelser = text.ytelser,
        utfall = text.utfall,
        enheter = text.enheter,
        sections = text.sections,
        templates = text.templates,
        created = text.created,
        modified = text.modified,
    )

