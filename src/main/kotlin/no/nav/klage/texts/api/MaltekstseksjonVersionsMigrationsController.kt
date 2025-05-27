package no.nav.klage.texts.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.texts.api.views.MaltekstseksjonInput
import no.nav.klage.texts.api.views.MaltekstseksjonView
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.repositories.TextRepository
import no.nav.klage.texts.service.MaltekstseksjonService
import no.nav.klage.texts.service.mapToMaltekstseksjonView
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.logMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@Tag(name = "kabal-text-templates")
@RequestMapping("/migrations/maltekstseksjon-versions")
@ProtectedWithClaims(issuer = ISSUER_AAD)
class MaltekstseksjonVersionsMigrationsController(
    private val maltekstseksjonService: MaltekstseksjonService,
    private val textRepository: TextRepository,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Operation(
        summary = "Get all maltekstseksjon versions",
        description = "Get all maltekstseksjon versions"
    )
    @GetMapping
    fun getMaltekstseksjonVersions(): List<MaltekstseksjonView> {
        //TODO must be admin
        logMethodDetails(
            methodName = ::getMaltekstseksjonVersions.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        return maltekstseksjonService.getAllMaltekstseksjonVersions().map {
            mapToMaltekstseksjonView(
                maltekstseksjonVersion = it,
                modifiedOrTextsModified = null,
            )
        }
    }

    @Operation(
        summary = "Update maltekstseksjon versions",
        description = "Update maltekstseksjon versions"
    )
    @PutMapping
    fun updateMaltekstseksjonVersions(
        @RequestBody input: List<MaltekstseksjonInput>
    ): List<MaltekstseksjonView> {
        logMethodDetails(
            methodName = ::updateMaltekstseksjonVersions.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        val updatedMaltekstseksjonVersions = maltekstseksjonService.getMaltekstseksjonVersionsById(input.map { requireNotNull(it.id) }).map { maltekstseksjonVersion ->

            val currentMaltekstseksjonInput = input.find { it.id == maltekstseksjonVersion.id } ?: error("No matching input for id ${maltekstseksjonVersion.id}")

            maltekstseksjonVersion.apply {
                title = currentMaltekstseksjonInput.title
                texts.clear()
                texts.addAll(currentMaltekstseksjonInput.textIdList.map { textRepository.getReferenceById(UUID.fromString(it)) })
                utfallIdList = currentMaltekstseksjonInput.utfallIdList
                enhetIdList = currentMaltekstseksjonInput.enhetIdList
                templateSectionIdList = currentMaltekstseksjonInput.templateSectionIdList
                ytelseHjemmelIdList = currentMaltekstseksjonInput.ytelseHjemmelIdList

                modified = LocalDateTime.now()
            }
        }

        return maltekstseksjonService.updateAll(updatedMaltekstseksjonVersions).map {
            mapToMaltekstseksjonView(
                maltekstseksjonVersion = it,
                modifiedOrTextsModified = null,
            )
        }
    }
}

