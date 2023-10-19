package no.nav.klage.texts.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.malteksts.service.MaltekstService
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.domain.Maltekst
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
        summary = "Create maltekst",
        description = "Create maltekst"
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
                maltekst = input.toDomainModel(),
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
    @PutMapping("/{maltekstId}/text-id-list")
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
    @PutMapping("/{maltekstId}/templatesection-id-list")
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
    @PutMapping("/{maltekstId}/ytelsehjemmel-id-list")
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
        summary = "Delete maltekst",
        description = "Delete maltekst"
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
        summary = "Get maltekst",
        description = "Get maltekst"
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

    private fun MaltekstInput.toDomainModel(): Maltekst {
        val now = LocalDateTime.now()
        return Maltekst(
            title = title,
            texts = textService.getTextsById(textIdList.map { UUID.fromString(it) }).toSet(),
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
            created = now,
            modified = now,
        )
    }
}

fun mapToMaltekstView(maltekst: Maltekst): MaltekstView =
    MaltekstView(
        id = maltekst.id,
        title = maltekst.title,
        textIdList = maltekst.texts.map { it.id.toString() }.toSet(),
        utfallIdList = maltekst.utfallIdList,
        enhetIdList = maltekst.enhetIdList,
        templateSectionIdList = maltekst.templateSectionIdList,
        ytelseHjemmelIdList = maltekst.ytelseHjemmelIdList,
        created = maltekst.created,
        modified = maltekst.modified,
    )

