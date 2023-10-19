package no.nav.klage.texts.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.service.TextService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import no.nav.klage.texts.util.logMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@Tag(name = "kabal-text-templates", description = "API for template texts")
@RequestMapping(value = ["/texts"])
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
        logMethodDetails(
            methodName = ::createText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
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
        @RequestBody input: UpdateTextInput
    ): TextView {
        logMethodDetails(
            methodName = ::updateText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateText(
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
                title = input.title,
                textType = input.textType,
                content = if (input.content == null || input.content.isNull) null else input.content,
                plainText = input.plainText,
                utfall = input.utfall,
                enheter = input.enheter,
                templateSectionList = input.templateSectionList,
                ytelseHjemmelList = input.ytelseHjemmelList,
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
        logMethodDetails(
            methodName = ::updateTitle.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
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
        logMethodDetails(
            methodName = ::updateTextType.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
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
        logMethodDetails(
            methodName = ::updateContent.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
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
        logMethodDetails(
            methodName = ::updatePlainText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
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
        logMethodDetails(
            methodName = ::updateSmartEditorVersion.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
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
        summary = "Update utfall",
        description = "Update utfall"
    )
    @PutMapping("/{textId}/utfall", "/{textId}/utfall-id-list")
    fun updateUtfall(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: UtfallIdListCompatibleInput
    ): TextView {
        logMethodDetails(
            methodName = ::updateUtfall.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateUtfall(
                input = input.utfallIdList ?: input.utfall ?: emptySet(),
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @Operation(
        summary = "Update enheter",
        description = "Update enheter"
    )
    @PutMapping("/{textId}/enheter", "/{textId}/enhet-id-list")
    fun updateEnheter(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: EnhetIdListCompatibleInput
    ): TextView {
        logMethodDetails(
            methodName = ::updateEnheter.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateEnheter(
                input = input.enheter ?: input.enhetIdList ?: emptySet(),
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @Operation(
        summary = "Update templateSectionList",
        description = "Update templateSectionList"
    )
    @PutMapping("/{textId}/templatesectionlist", "/{textId}/template-section-id-list")
    fun updateTemplateSectionList(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: TemplateSectionIdListCompatibleInput
    ): TextView {
        logMethodDetails(
            methodName = ::updateTemplateSectionList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateTemplateSectionList(
                input = input.templateSectionIdList ?: input.templateSectionList ?: emptySet(),
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @Operation(
        summary = "Update ytelseHjemmelList",
        description = "Update ytelseHjemmelList"
    )
    @PutMapping("/{textId}/ytelsehjemmellist", "/{textId}/ytelse-hjemmel-id-list")
    fun updateYtelseHjemmelList(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: YtelseHjemmelIdListCompatibleInput
    ): TextView {
        logMethodDetails(
            methodName = ::updateYtelseHjemmelList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return mapToTextView(
            textService.updateYtelseHjemmelList(
                input = input.ytelseHjemmelIdList ?: input.ytelseHjemmelList ?: emptySet(),
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
        logMethodDetails(
            methodName = ::deleteText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
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
        logMethodDetails(
            methodName = ::searchTexts.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        logger.debug("searchTexts called with params {}", searchQueryParams)

        val texts = textService.searchTexts(
            textType = searchQueryParams.textType,
            utfallIdList = searchQueryParams.utfallIdList ?: searchQueryParams.utfall ?: emptyList(),
            enhetIdList = searchQueryParams.enhetIdList ?: searchQueryParams.enheter ?: emptyList(),
            templateSectionIdList = searchQueryParams.templateSectionIdList ?: searchQueryParams.templateSectionList ?: emptyList(),
            ytelseHjemmelIdList = searchQueryParams.ytelseHjemmelIdList ?: searchQueryParams.ytelseHjemmelList ?: emptyList(),
            
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
        logMethodDetails(
            methodName = ::getText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
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
            utfallIdList = utfallIdList ?: utfall,
            enhetIdList = enhetIdList ?: enheter,
            templateSectionIdList = templateSectionIdList ?: templateSectionList,
            ytelseHjemmelIdList = ytelseHjemmelIdList ?: ytelseHjemmelList,
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
        utfall = text.utfallIdList,
        enheter = text.enhetIdList,
        templateSectionList = text.templateSectionIdList,
        ytelseHjemmelList = text.ytelseHjemmelIdList,
        created = text.created,
        modified = text.modified,
        utfallIdList = text.utfallIdList,
        enhetIdList = text.enhetIdList,
        templateSectionIdList = text.templateSectionIdList,
        ytelseHjemmelIdList = text.ytelseHjemmelIdList,

    )
