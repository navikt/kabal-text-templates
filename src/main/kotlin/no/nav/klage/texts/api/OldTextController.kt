package no.nav.klage.texts.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.service.OldTextService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import no.nav.klage.texts.util.logMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "kabal-text-templates", description = "API for template texts")
@RequestMapping(value = ["/texts"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
class TextController(
    private val oldTextService: OldTextService,
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
            oldTextService.createNewText(
                textInput = input,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @Operation(
        summary = "Update textVersion",
        description = "Update textVersion"
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
            oldTextService.updateText(
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
                title = input.title,
                textType = input.textType,
                content = if (input.content == null || input.content.isNull) null else input.content,
                plainText = input.plainText,
                utfallIdList = input.utfall,
                enhetIdList = input.enheter,
                templateSectionIdList = input.templateSectionList,
                ytelseHjemmelIdList = input.ytelseHjemmelList,
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
            oldTextService.updateTitle(
                input = input.title,
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @Operation(
        summary = "Update textVersion type",
        description = "Update textVersion type"
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
            oldTextService.updateTextType(
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
            oldTextService.updateContent(
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
            oldTextService.updatePlainText(
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
            oldTextService.updateSmartEditorVersion(
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
            oldTextService.updateUtfall(
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
            oldTextService.updateEnheter(
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
            oldTextService.updateTemplateSectionList(
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
            oldTextService.updateYtelseHjemmelList(
                input = input.ytelseHjemmelIdList ?: input.ytelseHjemmelList ?: emptySet(),
                textId = textId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @Operation(
        summary = "Delete textVersion",
        description = "Delete textVersion"
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

        oldTextService.deleteText(
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

        val texts = oldTextService.searchTexts(
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
        summary = "Get textVersion",
        description = "Get textVersion"
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
        return mapToTextView(oldTextService.getCurrentTextVersion(textId))
    }
}

fun mapToTextView(textVersion: TextVersion): TextView =
    TextView(
        id = textVersion.id,
        title = textVersion.title,
        textType = textVersion.textType,
        content = if (textVersion.content != null) jsonMapper().readTree(textVersion.content) else null,
        plainText = textVersion.plainText,
        version = textVersion.smartEditorVersion,
        utfall = textVersion.utfallIdList,
        enheter = textVersion.enhetIdList,
        templateSectionList = textVersion.templateSectionIdList,
        ytelseHjemmelList = textVersion.ytelseHjemmelIdList,
        created = textVersion.created,
        modified = textVersion.modified,
        utfallIdList = textVersion.utfallIdList,
        enhetIdList = textVersion.enhetIdList,
        templateSectionIdList = textVersion.templateSectionIdList,
        ytelseHjemmelIdList = textVersion.ytelseHjemmelIdList,
    )
