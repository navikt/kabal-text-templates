package no.nav.klage.texts.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.texts.api.views.ConsumerMaltekstseksjonView
import no.nav.klage.texts.api.views.ConsumerTextView
import no.nav.klage.texts.api.views.SearchMaltekstseksjonQueryParams
import no.nav.klage.texts.api.views.SearchTextQueryParams
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.service.MaltekstseksjonService
import no.nav.klage.texts.service.TextService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import no.nav.klage.texts.util.logMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@Tag(name = "kabal-text-templates", description = "API for text templates")
@RequestMapping(value = ["/consumer"])
@ProtectedWithClaims(issuer = ISSUER_AAD)
class ConsumerController(
    private val textService: TextService,
    private val maltekstseksjonService: MaltekstseksjonService,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Operation(
        summary = "Search texts",
        description = "Search texts"
    )
    @GetMapping("/texts")
    fun searchTexts(
        searchTextQueryParams: SearchTextQueryParams
    ): List<ConsumerTextView> {
        logMethodDetails(
            methodName = ::searchTexts.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        logger.debug("searchTexts called with params {}", searchTextQueryParams)

        val textVersions =
            textService.searchPublishedTextVersions(
                textType = searchTextQueryParams.textType,
                utfallIdList = searchTextQueryParams.utfallIdList ?: emptyList(),
                enhetIdList = searchTextQueryParams.enhetIdList ?: emptyList(),
                templateSectionIdList = searchTextQueryParams.templateSectionIdList ?: emptyList(),
                ytelseHjemmelIdList = searchTextQueryParams.ytelseHjemmelIdList ?: emptyList(),
            )

        return textVersions.map {
            mapToConsumerTextView(it)
        }
    }

    @Operation(
        summary = "Search maltekstseksjoner",
        description = "Search maltekstseksjoner"
    )
    @GetMapping("/maltekstseksjoner")
    fun searchMalteksts(
        searchMaltekstseksjonQueryParams: SearchMaltekstseksjonQueryParams
    ): List<ConsumerMaltekstseksjonView> {
        logMethodDetails(
            methodName = ::searchMalteksts.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        logger.debug("searchMalteksts called with params {}", searchMaltekstseksjonQueryParams)

        val maltekstseksjonsVersions =
            maltekstseksjonService.searchPublishedMaltekstseksjoner(
                textIdList = searchMaltekstseksjonQueryParams.textIdList ?: emptyList(),
                utfallIdList = searchMaltekstseksjonQueryParams.utfallIdList ?: emptyList(),
                enhetIdList = searchMaltekstseksjonQueryParams.enhetIdList ?: emptyList(),
                templateSectionIdList = searchMaltekstseksjonQueryParams.templateSectionIdList ?: emptyList(),
                ytelseHjemmelIdList = searchMaltekstseksjonQueryParams.ytelseHjemmelIdList ?: emptyList(),
            )

        return maltekstseksjonsVersions.map {
            mapToConsumerMaltekstView(it)
        }
    }

    @Operation(
        summary = "Get current maltekstseksjon texts",
        description = "Get current maltekstseksjon texts"
    )
    @GetMapping("/maltekstseksjoner/{maltekstseksjonId}/texts")
    fun getMaltekst(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
    ): List<ConsumerTextView> {
        logMethodDetails(
            methodName = ::getMaltekst.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )
        return maltekstseksjonService.getPublishedMaltekstseksjonVersion(maltekstseksjonId).texts.map {
            val textVersion = textService.getPublishedTextVersion(it.id)
            mapToConsumerTextView(textVersion)
        }
    }

    @Operation(
        summary = "Get published text version",
        description = "Get published text version"
    )
    @GetMapping("/texts/{textId}")
    fun getText(
        @PathVariable("textId") textId: UUID,
    ): ConsumerTextView {
        logMethodDetails(
            methodName = ::getText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )
        return mapToConsumerTextView(
            textVersion = textService.getPublishedTextVersion(textId),
        )
    }

    private fun mapToConsumerTextView(textVersion: TextVersion): ConsumerTextView =
        ConsumerTextView(
            id = textVersion.text.id,
            textType = textVersion.textType,
            content = if (textVersion.content != null) jsonMapper().readTree(textVersion.content) else null,
            plainText = textVersion.plainText,
            utfallIdList = textVersion.utfallIdList,
            enhetIdList = textVersion.enhetIdList,
            templateSectionIdList = textVersion.templateSectionIdList,
            ytelseHjemmelIdList = textVersion.ytelseHjemmelIdList,
        )

    private fun mapToConsumerMaltekstView(maltekstseksjonVersion: MaltekstseksjonVersion): ConsumerMaltekstseksjonView =
        ConsumerMaltekstseksjonView(
            id = maltekstseksjonVersion.maltekstseksjon.id,
            textIdList = maltekstseksjonVersion.texts.map { it.id.toString() },
            utfallIdList = maltekstseksjonVersion.utfallIdList,
            enhetIdList = maltekstseksjonVersion.enhetIdList,
            templateSectionIdList = maltekstseksjonVersion.templateSectionIdList,
            ytelseHjemmelIdList = maltekstseksjonVersion.ytelseHjemmelIdList,
        )

}