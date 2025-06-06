package no.nav.klage.texts.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_SEARCH
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_TEXTS
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT_SEARCH
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.exceptions.LanguageNotFoundException
import no.nav.klage.texts.service.MaltekstseksjonService
import no.nav.klage.texts.service.TextService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.logMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.cache.annotation.Cacheable
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
    }

    @Cacheable(CONSUMER_TEXT_SEARCH)
    @Operation(
        summary = "Search texts",
        description = "Search texts"
    )
    @GetMapping("/texts/{language}")
    fun searchTexts(
        @PathVariable("language") language: Language,
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
            ).sortedByDescending { it.created }

        return textVersions.mapNotNull {
            mapToConsumerTextView(
                textVersion = it,
                language = language,
            )
        }
    }

    @Cacheable(CONSUMER_MALTEKSTSEKSJON_SEARCH)
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
            ).sortedByDescending { it.created }

        return maltekstseksjonsVersions.map {
            mapToConsumerMaltekstView(it)
        }
    }

    @Cacheable(CONSUMER_MALTEKSTSEKSJON_TEXTS)
    @Operation(
        summary = "Get published maltekstseksjon texts",
        description = "Get published maltekstseksjon texts"
    )
    @GetMapping("/maltekstseksjoner/{maltekstseksjonId}/texts/{language}")
    fun getMaltekstseksjonTexts(
        @PathVariable("maltekstseksjonId") maltekstseksjonId: UUID,
        @PathVariable("language") language: Language,
    ): List<ConsumerTextView> {
        logMethodDetails(
            methodName = ::getMaltekstseksjonTexts.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = maltekstseksjonId,
            logger = logger,
        )
        return maltekstseksjonService.getPublishedMaltekstseksjonVersion(maltekstseksjonId).texts.mapNotNull {
            val textVersion = textService.getPublishedTextVersion(it.id)
            mapToConsumerTextView(
                textVersion = textVersion,
                language = language,
            )
        }
    }

    @Cacheable(CONSUMER_TEXT)
    @Operation(
        summary = "Get published text version",
        description = "Get published text version"
    )
    @GetMapping("/texts/{textId}/{language}")
    fun getText(
        @PathVariable("textId") textId: UUID,
        @PathVariable("language") language: Language,
    ): ConsumerTextView {
        logMethodDetails(
            methodName = ::getText.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = textId,
            logger = logger,
        )
        return mapToConsumerTextView(
            textVersion = textService.getPublishedTextVersion(textId),
            language = language,
        ) ?: throw LanguageNotFoundException("Fant ikke tekst for språk $language")
    }

    private fun mapToConsumerTextView(textVersion: TextVersion, language: Language): ConsumerTextView? {
        when (language) {
            Language.NN -> if (textVersion.richTextNN == null && textVersion.plainTextNN == null) {
                return null
            }

            Language.NB -> if (textVersion.richTextNB == null && textVersion.plainTextNB == null) {
                return null
            }

            Language.UNTRANSLATED -> if (textVersion.richTextUntranslated == null) {
                return null
            }
        }
        return ConsumerTextView(
            id = textVersion.text.id,
            title = textVersion.title,
            textType = textVersion.textType,
            richText = when (language) {
                Language.NN -> if (textVersion.richTextNN != null) jsonMapper().readTree(textVersion.richTextNN) else null
                Language.NB -> if (textVersion.richTextNB != null) jsonMapper().readTree(textVersion.richTextNB) else null
                Language.UNTRANSLATED -> if (textVersion.richTextUntranslated != null) jsonMapper().readTree(textVersion.richTextUntranslated) else null
            },
            plainText = when (language) {
                Language.NN -> textVersion.plainTextNN
                Language.NB -> textVersion.plainTextNB
                Language.UNTRANSLATED -> null
            },
            utfallIdList = textVersion.utfallIdList,
            enhetIdList = textVersion.enhetIdList,
            templateSectionIdList = textVersion.templateSectionIdList,
            ytelseHjemmelIdList = textVersion.ytelseHjemmelIdList,
            language = language,
            publishedDateTime = textVersion.publishedDateTime!!,
        )
    }

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