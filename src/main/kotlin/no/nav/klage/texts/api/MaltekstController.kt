package no.nav.klage.texts.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.service.MaltekstService
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
@RequestMapping(value = ["/maltekster"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
class MaltekstController(
    private val textService: TextService,
    private val maltekstService: MaltekstService,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Operation(
        summary = "Create maltekstseksjon",
        description = "Create maltekstseksjon"
    )
    @PostMapping
    fun createMaltekst(
        @RequestBody input: MaltekstInput
    ): MaltekstseksjonView {
        logMethodDetails(
            methodName = ::createMaltekst.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstService.createNewMaltekstseksjon(
                maltekstseksjonInput = input,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
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
            maltekstService.updateTitle(
                input = input.title,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @Operation(
        summary = "Update textIdList",
        description = "Update textIdList"
    )
    @PutMapping("/{maltekstseksjonId}/textVersion-id-list")
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
            maltekstService.updateTexts(
                input = input.textIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
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
            maltekstService.updateUtfall(
                input = input.utfallIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
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
            maltekstService.updateEnheter(
                input = input.enhetIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
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
            maltekstService.updateTemplateSectionList(
                input = input.templateSectionIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
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
            maltekstService.updateYtelseHjemmelList(
                input = input.ytelseHjemmelIdList,
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = tokenUtil.getIdent(),
            )
        )
    }

    @Operation(
        summary = "Delete maltekstseksjon",
        description = "Delete maltekstseksjon"
    )
    @DeleteMapping("/{maltekstseksjonId}")
    fun deleteMaltekst(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
    ) {
        logMethodDetails(
            methodName = ::deleteMaltekst.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )

        maltekstService.deleteMaltekstseksjon(
            maltekstseksjonId = maltekstseksjonId,
            saksbehandlerIdent = tokenUtil.getIdent(),
        )
    }

    @Operation(
        summary = "Search malteksts",
        description = "Search malteksts"
    )
    @GetMapping
    fun searchMalteksts(
        @RequestParam(required = false, defaultValue = "true") published: Boolean = true,
        searchMaltekstseksjonQueryParams: SearchMaltekstseksjonQueryParams
    ): List<MaltekstseksjonView> {
        logMethodDetails(
            methodName = ::searchMalteksts.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        logger.debug("searchMalteksts called with published = {} params {}", published, searchMaltekstseksjonQueryParams)

        val maltekstseksjonsVersions = if (published) {
            maltekstService.searchPublishedMaltekstseksjoner(
                textIdList = searchMaltekstseksjonQueryParams.textIdList ?: emptyList(),
                utfallIdList = searchMaltekstseksjonQueryParams.utfallIdList ?: emptyList(),
                enhetIdList = searchMaltekstseksjonQueryParams.enhetIdList ?: emptyList(),
                templateSectionIdList = searchMaltekstseksjonQueryParams.templateSectionIdList ?: emptyList(),
                ytelseHjemmelIdList = searchMaltekstseksjonQueryParams.ytelseHjemmelIdList ?: emptyList(),
            )
        } else {
            maltekstService.searchMaltekstseksjoner(
                textIdList = searchMaltekstseksjonQueryParams.textIdList ?: emptyList(),
                utfallIdList = searchMaltekstseksjonQueryParams.utfallIdList ?: emptyList(),
                enhetIdList = searchMaltekstseksjonQueryParams.enhetIdList ?: emptyList(),
                templateSectionIdList = searchMaltekstseksjonQueryParams.templateSectionIdList ?: emptyList(),
                ytelseHjemmelIdList = searchMaltekstseksjonQueryParams.ytelseHjemmelIdList ?: emptyList(),
            )
        }
        return maltekstseksjonsVersions.map {
            mapToMaltekstView(it)
        }
    }

    @Operation(
        summary = "Get maltekstseksjon",
        description = "Get maltekstseksjon"
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
        return mapToMaltekstView(maltekstService.getCurrentMaltekstseksjonVersion(maltekstseksjonId))
    }
}

fun mapToMaltekstView(maltekstseksjonVersion: MaltekstseksjonVersion): MaltekstseksjonView =
    MaltekstseksjonView(
        id = maltekstseksjonVersion.maltekstseksjonId,
        title = maltekstseksjonVersion.title,
        maltekstseksjonId = maltekstseksjonVersion.id,
        textIdList = maltekstseksjonVersion.texts.map { it.id.toString() },
        utfallIdList = maltekstseksjonVersion.utfallIdList,
        enhetIdList = maltekstseksjonVersion.enhetIdList,
        templateSectionIdList = maltekstseksjonVersion.templateSectionIdList,
        ytelseHjemmelIdList = maltekstseksjonVersion.ytelseHjemmelIdList,
        created = maltekstseksjonVersion.created,
        modified = maltekstseksjonVersion.modified,
        editors = maltekstseksjonVersion.editors,
        publishedDateTime = maltekstseksjonVersion.publishedDateTime,
        publishedBy = maltekstseksjonVersion.publishedBy,
        published = maltekstseksjonVersion.published,
    )
