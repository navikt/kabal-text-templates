package no.nav.klage.texts.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.service.TextService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.logMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "kabal-text-templates", description = "API for text templates")
@RequestMapping(value = ["/texts"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
class TextController(
    private val textService: TextService,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Operation(
        summary = "Get versions for text",
        description = "Get versions for text"
    )
    @GetMapping("/{textId}/versions")
    fun getTextVersions(
        @PathVariable("textId") textId: UUID,
    ): List<TextView> {
        logMethodDetails(
            methodName = ::getTextVersions.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )
        return textService.getTextVersions(textId)
    }

    @Operation(
        summary = "Publish text",
        description = "Publish text using current draft"
    )
    @PostMapping("/{textId}/publish")
    fun publishText(
        @PathVariable("textId") textId: UUID,
    ): TextView {
        logMethodDetails(
            methodName = ::publishText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return textService.publishTextVersion(
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
        )
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

        return textService.createNewText(
            textInput = input,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
        )
    }

    @Operation(
        summary = "Create text draft",
        description = "Create text draft, possibly based on existing version"
    )
    @PostMapping("/{textId}/draft")
    fun createDraft(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: VersionInput?
    ): TextView {
        logMethodDetails(
            methodName = ::createDraft.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return textService.createNewDraft(
            textId = textId,
            versionInput = input,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
        )
    }

    @Operation(
        summary = "Create duplicate of text as draft",
        description = "Create duplicate of text as draft, possibly based on existing version"
    )
    @PostMapping("/{textId}/duplicate")
    fun createDuplicate(
        @PathVariable("textId") textId: UUID,
        @RequestBody input: VersionInput?
    ): TextView {
        logMethodDetails(
            methodName = ::createDuplicate.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return textService.createDuplicate(
            textId = textId,
            versionInput = input,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
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

        return textService.updateTitle(
            input = input.title,
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
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

        return textService.updateTextType(
            input = input.textType,
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
        )
    }

    @Operation(
        summary = "Update richtext",
        description = "Update richtext"
    )
    @PutMapping("/{textId}/{language}/richtext")
    fun updateRichText(
        @PathVariable("textId") textId: UUID,
        @PathVariable("language") language: Language,
        @RequestBody input: RichTextInput
    ): TextView {
        logMethodDetails(
            methodName = ::updateRichText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return textService.updateRichText(
            input = input.richText.toString(),
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
            language = language,
        )
    }

    @Operation(
        summary = "Update plainText",
        description = "Update plainText"
    )
    @PutMapping("/{textId}/{language}/plaintext")
    fun updatePlainText(
        @PathVariable("textId") textId: UUID,
        @PathVariable("language") language: Language,
        @RequestBody input: PlainTextInput
    ): TextView {
        logMethodDetails(
            methodName = ::updatePlainText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return textService.updatePlainText(
            input = input.plainText,
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
            language = language,
        )
    }

    @Operation(
        summary = "Update utfall",
        description = "Update utfall"
    )
    @PutMapping("/{textId}/utfall-id-list")
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

        return textService.updateUtfall(
            input = input.utfallIdList ?: input.utfall ?: emptySet(),
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
        )
    }

    @Operation(
        summary = "Update enheter",
        description = "Update enheter"
    )
    @PutMapping("/{textId}/enhet-id-list")
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

        return textService.updateEnheter(
            input = input.enheter ?: input.enhetIdList ?: emptySet(),
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
        )
    }

    @Operation(
        summary = "Update templateSectionList",
        description = "Update templateSectionList"
    )
    @PutMapping("/{textId}/template-section-id-list")
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

        return textService.updateTemplateSectionList(
            input = input.templateSectionIdList ?: input.templateSectionList ?: emptySet(),
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
        )
    }

    @Operation(
        summary = "Update ytelseHjemmelList",
        description = "Update ytelseHjemmelList"
    )
    @PutMapping("/{textId}/ytelse-hjemmel-id-list")
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

        return textService.updateYtelseHjemmelList(
            input = input.ytelseHjemmelIdList ?: input.ytelseHjemmelList ?: emptySet(),
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
        )
    }

    @Operation(
        summary = "Unpublish text",
        description = "Unpublish text"
    )
    @PostMapping("/{textId}/unpublish")
    fun unpublishText(
        @PathVariable("textId") textId: UUID,
    ): DeletedText {
        logMethodDetails(
            methodName = ::unpublishText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )
        return textService.unpublishText(
            textId = textId,
            saksbehandlerIdent = tokenUtil.getIdent(),
            saksbehandlerName = tokenUtil.getName(),
        )
    }

    @Operation(
        summary = "Delete text draft version",
        description = "Delete text draft version"
    )
    @DeleteMapping("/{textId}/draft")
    fun deleteTextDraftVersion(
        @PathVariable("textId") textId: UUID,
    ): DeletedText {
        logMethodDetails(
            methodName = ::deleteTextDraftVersion.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )

        return textService.deleteTextDraftVersion(
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
        searchTextQueryParams: SearchTextQueryParams
    ): List<TextViewForLists> {
        logMethodDetails(
            methodName = ::searchTexts.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        logger.debug("searchTexts called with params {}", searchTextQueryParams)

        return textService.searchTextVersions(
            textType = searchTextQueryParams.textType,
            utfallIdList = searchTextQueryParams.utfallIdList ?: emptyList(),
            enhetIdList = searchTextQueryParams.enhetIdList ?: emptyList(),
            templateSectionIdList = searchTextQueryParams.templateSectionIdList ?: emptyList(),
            ytelseHjemmelIdList = searchTextQueryParams.ytelseHjemmelIdList ?: emptyList(),
            trash = searchTextQueryParams.trash,
        ).sortedByDescending { it.created }

    }

    @Operation(
        summary = "Get current text version",
        description = "Get current text version"
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
        return textService.getCurrentTextVersionAsView(textId)
    }
}