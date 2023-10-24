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
import java.time.LocalDateTime
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
    ): MaltekstView {
        logMethodDetails(
            methodName = ::createMaltekst.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstService.createMaltekst(
                maltekstseksjonVersion = input.toDomainModel(),
            )
        )
    }

    @Operation(
        summary = "Update title",
        description = "Update title"
    )
    @PutMapping("/{maltekstId}/title")
    fun updateTitle(
        @PathVariable("maltekstId") maltekstId: UUID,
        @RequestBody input: TitleInput
    ): MaltekstView {
        logMethodDetails(
            methodName = ::updateTitle.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstService.updateTitle(
                input = input.title,
                maltekstId = maltekstId,
            )
        )
    }

    @Operation(
        summary = "Update textIdList",
        description = "Update textIdList"
    )
    @PutMapping("/{maltekstId}/textVersion-id-list")
    fun updateTextIdList(
        @PathVariable("maltekstId") maltekstId: UUID,
        @RequestBody input: TextIdListInput
    ): MaltekstView {
        logMethodDetails(
            methodName = ::updateTextIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstService.updateTextIdList(
                input = input.textIdList,
                maltekstId = maltekstId,
            )
        )
    }

    @Operation(
        summary = "Update utfall",
        description = "Update utfall"
    )
    @PutMapping("/{maltekstId}/utfall-id-list")
    fun updateUtfallIdList(
        @PathVariable("maltekstId") maltekstId: UUID,
        @RequestBody input: UtfallIdListInput
    ): MaltekstView {
        logMethodDetails(
            methodName = ::updateUtfallIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstService.updateUtfallIdList(
                input = input.utfallIdList,
                maltekstId = maltekstId,
            )
        )
    }

    @Operation(
        summary = "Update enheter",
        description = "Update enheter"
    )
    @PutMapping("/{maltekstId}/enhet-id-list")
    fun updateEnhetIdList(
        @PathVariable("maltekstId") maltekstId: UUID,
        @RequestBody input: EnhetIdListInput
    ): MaltekstView {
        logMethodDetails(
            methodName = ::updateEnhetIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstService.updateEnhetIdList(
                input = input.enhetIdList,
                maltekstId = maltekstId,
            )
        )
    }

    @Operation(
        summary = "Update templateSectionList",
        description = "Update templateSectionList"
    )
    @PutMapping("/{maltekstId}/template-section-id-list")
    fun updateTemplateSectionIdList(
        @PathVariable("maltekstId") maltekstId: UUID,
        @RequestBody input: TemplateSectionIdListInput
    ): MaltekstView {
        logMethodDetails(
            methodName = ::updateTemplateSectionIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstService.updateTemplateSectionIdList(
                input = input.templateSectionIdList,
                maltekstId = maltekstId,
            )
        )
    }

    @Operation(
        summary = "Update ytelseHjemmelList",
        description = "Update ytelseHjemmelList"
    )
    @PutMapping("/{maltekstId}/ytelse-hjemmel-id-list")
    fun updateYtelseHjemmelIdList(
        @PathVariable("maltekstId") maltekstId: UUID,
        @RequestBody input: YtelseHjemmelIdListInput
    ): MaltekstView {
        logMethodDetails(
            methodName = ::updateYtelseHjemmelIdList.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstId,
            logger = logger,
        )

        return mapToMaltekstView(
            maltekstService.updateYtelseHjemmelIdList(
                input = input.ytelseHjemmelIdList,
                maltekstId = maltekstId,
            )
        )
    }

    @Operation(
        summary = "Delete maltekstseksjon",
        description = "Delete maltekstseksjon"
    )
    @DeleteMapping("/{maltekstId}")
    fun deleteMaltekst(
        @PathVariable("maltekstId") maltekstId: UUID,
    ) {
        logMethodDetails(
            methodName = ::deleteMaltekst.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstId,
            logger = logger,
        )

        maltekstService.deleteMaltekst(
            maltekstId = maltekstId,
        )
    }

    @Operation(
        summary = "Search malteksts",
        description = "Search malteksts"
    )
    @GetMapping
    fun searchMalteksts(
        searchQueryParams: SearchQueryParams
    ): List<MaltekstView> {
        logMethodDetails(
            methodName = ::searchMalteksts.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        logger.debug("searchMalteksts called with params {}", searchQueryParams)

        //TODO
//        val malteksts = maltekstService.searchMalteksts(
//            maltekstType = searchQueryParams.maltekstType,
//            utfall = searchQueryParams.utfall ?: emptyList(),
//            enheter = searchQueryParams.enheter ?: emptyList(),
//            templateSectionList = searchQueryParams.templateSectionList ?: emptyList(),
//            ytelseHjemmelList = searchQueryParams.ytelseHjemmelList ?: emptyList(),
//
//        )
        return maltekstService.getAllMalteksts().map {
            mapToMaltekstView(it)
        }
    }

    @Operation(
        summary = "Get maltekstseksjon",
        description = "Get maltekstseksjon"
    )
    @GetMapping("/{maltekstId}")
    fun getMaltekst(
        @PathVariable("maltekstId") maltekstId: UUID,
    ): MaltekstView {
        logMethodDetails(
            methodName = ::getMaltekst.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstId,
            logger = logger,
        )
        return mapToMaltekstView(maltekstService.getMaltekst(maltekstId))
    }

    private fun MaltekstInput.toDomainModel(): MaltekstseksjonVersion {
        val now = LocalDateTime.now()
        return MaltekstseksjonVersion(
            title = title,
            texts = textService.getTextsById(textIdList.map { UUID.fromString(it) }),
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
            created = now,
            modified = now,
        )
    }
}

fun mapToMaltekstView(maltekstseksjonVersion: MaltekstseksjonVersion): MaltekstView =
    MaltekstView(
        id = maltekstseksjonVersion.id,
        title = maltekstseksjonVersion.title,
        textIdList = maltekstseksjonVersion.texts.map { it.id.toString() },
        utfallIdList = maltekstseksjonVersion.utfallIdList,
        enhetIdList = maltekstseksjonVersion.enhetIdList,
        templateSectionIdList = maltekstseksjonVersion.templateSectionIdList,
        ytelseHjemmelIdList = maltekstseksjonVersion.ytelseHjemmelIdList,
        created = maltekstseksjonVersion.created,
        modified = maltekstseksjonVersion.modified,
    )

