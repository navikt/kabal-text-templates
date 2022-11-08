package no.nav.klage.texts.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "kabal-text-templates", description = "API for template texts")
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

    @Operation(
        summary = "Create text",
        description = "Create text"
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

    @Operation(
        summary = "Update text",
        description = "Update text"
    )
    @PutMapping("/{textId}")
    fun updateText(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: TextInput
    ): TextView {
        logTextMethodDetails(
            methodName = ::updateText.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateText(
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
                title = input.title,
                textType = input.textType,
                content = input.content,
                plainText = input.plainText,
                hjemler = input.hjemler,
                ytelser = input.ytelser,
                utfall = input.utfall,
                enheter = input.enheter,
                sections = input.sections,
                templates = input.templates,
            )
        )
    }

    @Operation(
        summary = "Update title",
        description = "Update title"
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

    @Operation(
        summary = "Update text type",
        description = "Update text type"
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

    @Operation(
        summary = "Update content",
        description = "Update content"
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

    @Operation(
        summary = "Update plainText",
        description = "Update plainText"
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

    @Operation(
        summary = "Update smartEditorVersion",
        description = "Update smartEditorVersion"
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

    @Operation(
        summary = "Update hjemler",
        description = "Update hjemler"
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

    @Operation(
        summary = "Update ytelser",
        description = "Update ytelser"
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

    @Operation(
        summary = "Update utfall",
        description = "Update utfall"
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

    @Operation(
        summary = "Update enheter",
        description = "Update enheter"
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

    @Operation(
        summary = "Update sections",
        description = "Update sections"
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

    @Operation(
        summary = "Update templates",
        description = "Update templates"
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

    @Operation(
        summary = "Delete text",
        description = "Delete text"
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

    @Operation(
        summary = "Search texts",
        description = "Search texts"
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

    @Operation(
        summary = "Get text",
        description = "Get text"
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

    private fun TextInput.toDomainModel(): Text {
        val now = LocalDateTime.now()
        return Text(
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
            created = now,
            modified = now,
        )
    }
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

