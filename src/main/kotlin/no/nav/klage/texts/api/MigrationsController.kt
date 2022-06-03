package no.nav.klage.texts.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import no.nav.klage.texts.api.views.TextInput
import no.nav.klage.texts.api.views.TextView
import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.service.TextService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import no.nav.klage.texts.util.logTextMethodDetails
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@Api(tags = ["kabal-text-templates"])
@RequestMapping("/migrations/texts")
@ProtectedWithClaims(issuer = ISSUER_AAD)
class MigrationsController(
    private val textService: TextService,
    private val tokenUtil: TokenUtil,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @ApiOperation(
        value = "Get all texts",
        notes = "Get all texts"
    )
    @GetMapping
    fun getTexts(): List<TextView> {
        //TODO must be admin
        logTextMethodDetails(
            methodName = ::getTexts.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = null,
            logger = logger,
        )

        return textService.getAllTexts().map {
            mapToTextView(it)
        }
    }

    @ApiOperation(
        value = "Update texts",
        notes = "Update texts"
    )
    @PutMapping
    fun updateTexts(
        @RequestBody input: List<TextInput>
    ): List<TextView> {
        //TODO must be admin
        logTextMethodDetails(
            methodName = ::updateTexts.name,
            innloggetIdent = tokenUtil.getIdent(),
            textId = null,
            logger = logger,
        )

        val updatedTexts = textService.getTextsById(input.map { requireNotNull(it.id) }).map { text ->
            val currentTextInput = input.find { it.id == text.id } ?: error("No matching input for id ${text.id}")
            text.apply {
                title = currentTextInput.title
                textType = currentTextInput.textType
                content = currentTextInput.content.toString()
                plainText = currentTextInput.plainText
                hjemler = currentTextInput.hjemler
                ytelser = currentTextInput.ytelser
                utfall = currentTextInput.utfall
                enheter = currentTextInput.enheter
                sections = currentTextInput.sections
                modified = LocalDateTime.now()
                smartEditorVersion = currentTextInput.version
            }
        }

        return textService.updateAll(updatedTexts).map {
            mapToTextView(it)
        }
    }
}

