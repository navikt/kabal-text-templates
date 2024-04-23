package no.nav.klage.texts.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.texts.api.views.TextInput
import no.nav.klage.texts.api.views.TextView
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.service.TextService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import no.nav.klage.texts.util.logMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "kabal-text-templates")
@RequestMapping("/migrations/text-versions")
@ProtectedWithClaims(issuer = ISSUER_AAD)
class TextVersionsMigrationsController(
    private val textService: TextService,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Operation(
        summary = "Get all texts",
        description = "Get all texts"
    )
    @GetMapping
    fun getTextVersions(): List<TextView> {
        //TODO must be admin
        logMethodDetails(
            methodName = ::getTextVersions.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )

        return textService.getAllTextVersions().map {
            mapToTextView(
                textVersion = it,
                connectedMaltekstseksjonIdList = emptyList<UUID>() to emptyList<UUID>()
            )
        }
    }

    @Operation(
        summary = "Update texts",
        description = "Update texts"
    )
    @PutMapping
    fun updateTextVersions(
        @RequestBody input: List<TextInput>
    ): List<TextView> {
        logMethodDetails(
            methodName = ::updateTextVersions.name,
            innloggetIdent = tokenUtil.getIdent(),
            id = null,
            logger = logger,
        )
        TODO()

//        val updatedTexts = textService.getTextVersionsById(input.map { requireNotNull(it.id) }).map { textVersion ->
//
//            val currentTextInput = input.find { it.id == textVersion.id } ?: error("No matching input for id ${textVersion.id}")
//
//            textVersion.apply {
//                title = currentTextInput.title
//                textType = currentTextInput.textType
//                content = currentTextInput.content.toString()
//                plainText = currentTextInput.plainText
//                smartEditorVersion = currentTextInput.version
//                utfallIdList = currentTextInput.utfall
//                enhetIdList = currentTextInput.enheter
//                templateSectionIdList = currentTextInput.templateSectionList
//                ytelseHjemmelIdList = currentTextInput.ytelseHjemmelList
//
//                modified = LocalDateTime.now()
//            }
//        }
//
//        return textService.updateAll(updatedTexts).map {
//            mapToTextView(
//                textVersion = it,
//                connectedMaltekstseksjonIdList = emptyList<UUID>() to emptyList<UUID>()
//            )
//        }
    }
}

