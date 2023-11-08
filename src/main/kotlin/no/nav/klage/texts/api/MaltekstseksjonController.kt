package no.nav.klage.texts.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.service.MaltekstseksjonService
import no.nav.klage.texts.service.TextService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import no.nav.klage.texts.util.logMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "kabal-text-templates", description = "API for template texts")
@RequestMapping(value = ["/maltekstseksjoner"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
class MaltekstseksjonController(
    private val maltekstseksjonService: MaltekstseksjonService,
    private val textService: TextService,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Operation(
        summary = "Get versions for maltekstseksjon",
        description = "Get versions for maltekstseksjon"
    )
    @GetMapping("/{maltekstseksjonId}/versions")
    fun getMaltekstseksjonVersions(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
    ): List<MaltekstseksjonView> {
        logMethodDetails(
            methodName = ::getMaltekstseksjonVersions.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        return maltekstseksjonService.getMaltekstseksjonVersions(maltekstseksjonId).map {
            mapToMaltekstView(
                maltekstseksjonVersion = it,
            )
        }
    }

    @Operation(
        summary = "Publish maltekstseksjon",
        description = "Publish maltekstseksjon using current draft"
    )
    @PostMapping("/{maltekstseksjonId}/publish")
    fun publishMaltekstseksjon(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::publishMaltekstseksjon.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.publishMaltekstseksjonVersion(
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            ),
        )
    }

    @Operation(
        summary = "Publish maltekstseksjon and belonging texts (drafts)",
        description = "Publish maltekstseksjon and belonging texts (drafts)"
    )
    @PostMapping("/{maltekstseksjonId}/publish-with-texts")
    fun publishMaltekstseksjonWithTexts(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
    ): MaltekstseksjonWithTextsView {
        logMethodDetails(
            methodName = ::publishMaltekstseksjon.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        val (maltekstseksjonVersion, textVersions) = maltekstseksjonService.publishMaltekstseksjonVersionWithTexts(
            maltekstseksjonId = maltekstseksjonId,
            saksbehandlerIdent = tokenUtil.getIdent(),
        )

        return MaltekstseksjonWithTextsView(
            maltekstseksjon = mapToMaltekstView(
                maltekstseksjonVersion = maltekstseksjonVersion,
            ),
            publishedTexts = textVersions.map { textVersion ->
                mapToTextView(
                    textVersion = textVersion,
                    connectedMaltekstseksjonIdList = textService.getConnectedMaltekstseksjoner(textVersion.text.id)
                )
            }
        )
    }

    @Operation(
        summary = "Create maltekstseksjon",
        description = "Create maltekstseksjon"
    )
    @PostMapping
    fun createMaltekst(
        @RequestBody input: MaltekstseksjonInput
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::createMaltekst.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.createNewMaltekstseksjon(
                maltekstseksjonInput = input,
                saksbehandlerIdent = tokenUtil.getIdent(),
            ),
        )
    }

    @Operation(
        summary = "Create maltekstseksjon draft",
        description = "Create maltekstseksjon draft, possibly based on existing version"
    )
    @PostMapping("/{maltekstseksjonId}/draft")
    fun createDraft(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
        @RequestBody input: VersionInput?
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::createDraft.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.createNewDraft(
                maltekstseksjonId = maltekstseksjonId,
                versionInput = input,
                saksbehandlerIdent = tokenUtil.getIdent(),
            ),
        )
    }

    @Operation(
        summary = "Update title",
        description = "Update title"
    )
    @PutMapping("/{maltekstseksjonId}/title")
    fun updateTitle(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
        @RequestBody input: TitleInput
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::updateTitle.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.updateTitle(
                input = input.title,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            ),
        )
    }

    @Operation(
        summary = "Update textIdList",
        description = "Update textIdList"
    )
    @PutMapping("/{maltekstseksjonId}/text-id-list")
    fun updateTextIdList(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
        @RequestBody input: TextIdListInput
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::updateTextIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.updateTexts(
                input = input.textIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            ),
        )
    }

    @Operation(
        summary = "Update utfall",
        description = "Update utfall"
    )
    @PutMapping("/{maltekstseksjonId}/utfall-id-list")
    fun updateUtfallIdList(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
        @RequestBody input: UtfallIdListInput
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::updateUtfallIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.updateUtfall(
                input = input.utfallIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            ),
        )
    }

    @Operation(
        summary = "Update enheter",
        description = "Update enheter"
    )
    @PutMapping("/{maltekstseksjonId}/enhet-id-list")
    fun updateEnhetIdList(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
        @RequestBody input: EnhetIdListInput
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::updateEnhetIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.updateEnheter(
                input = input.enhetIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            ),
        )
    }

    @Operation(
        summary = "Update templateSectionList",
        description = "Update templateSectionList"
    )
    @PutMapping("/{maltekstseksjonId}/template-section-id-list")
    fun updateTemplateSectionIdList(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
        @RequestBody input: TemplateSectionIdListInput
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::updateTemplateSectionIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.updateTemplateSectionList(
                input = input.templateSectionIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            ),
        )
    }

    @Operation(
        summary = "Update ytelseHjemmelList",
        description = "Update ytelseHjemmelList"
    )
    @PutMapping("/{maltekstseksjonId}/ytelse-hjemmel-id-list")
    fun updateYtelseHjemmelIdList(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
        @RequestBody input: YtelseHjemmelIdListInput
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::updateYtelseHjemmelIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.updateYtelseHjemmelList(
                input = input.ytelseHjemmelIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            ),
        )
    }

    @Operation(
        summary = "Unpublish maltekstseksjon",
        description = "Unpublish maltekstseksjon"
    )
    @PostMapping("/{maltekstseksjonId}/unpublish")
    fun unpublishMaltekstseksjon(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
    ) {
        logMethodDetails(
            methodName = ::unpublishMaltekstseksjon.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        maltekstseksjonService.unpublishMaltekstseksjon(
            maltekstseksjonId = maltekstseksjonId,
            saksbehandlerIdent = tokenUtil.getIdent(),
        )
    }

    @Operation(
        summary = "Delete maltekstseksjon draft version",
        description = "Delete maltekstseksjon draft version"
    )
    @DeleteMapping("/{maltekstseksjonId}/draft")
    fun deleteMaltekstDraftVersion(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
    ) {
        logMethodDetails(
            methodName = ::deleteMaltekstDraftVersion.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        maltekstseksjonService.deleteMaltekstseksjonDraftVersion(
            maltekstseksjonId = maltekstseksjonId,
            saksbehandlerIdent = tokenUtil.getIdent(),
        )
    }

    @Operation(
        summary = "Search maltekstseksjoner",
        description = "Search maltekstseksjoner"
    )
    @GetMapping
    fun searchMaltekstseksjoner(
        searchMaltekstseksjonQueryParams: SearchMaltekstseksjonQueryParams
    ): List<MaltekstseksjonView> {
        logMethodDetails(
            methodName = ::searchMaltekstseksjoner.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        logger.debug("searchMaltekstseksjoner called with params {}", searchMaltekstseksjonQueryParams)

        val maltekstseksjonsVersions =
            maltekstseksjonService.searchMaltekstseksjoner(
                textIdList = searchMaltekstseksjonQueryParams.textIdList ?: emptyList(),
                utfallIdList = searchMaltekstseksjonQueryParams.utfallIdList ?: emptyList(),
                enhetIdList = searchMaltekstseksjonQueryParams.enhetIdList ?: emptyList(),
                templateSectionIdList = searchMaltekstseksjonQueryParams.templateSectionIdList ?: emptyList(),
                ytelseHjemmelIdList = searchMaltekstseksjonQueryParams.ytelseHjemmelIdList ?: emptyList(),
            ).sortedByDescending { it.created }

        return maltekstseksjonsVersions.map {
            mapToMaltekstView(
                maltekstseksjonVersion = it,
            )
        }
    }

    @Operation(
        summary = "Get current maltekstseksjon",
        description = "Get current maltekstseksjon. Draft or published."
    )
    @GetMapping("/{maltekstseksjonId}")
    fun getMaltekst(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::getMaltekst.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )
        return mapToMaltekstView(
            maltekstseksjonVersion = maltekstseksjonService.getCurrentMaltekstseksjonVersion(
                maltekstseksjonId
            ),
        )
    }
}
