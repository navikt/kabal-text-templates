package no.nav.klage.texts.util

import no.nav.klage.texts.config.SecurityConfiguration
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Service

@Service
class TokenUtil(
    private val tokenValidationContextHolder: TokenValidationContextHolder,
) {

    fun getIdent(): String =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(SecurityConfiguration.ISSUER_AAD)
            .jwtTokenClaims?.get("NAVident")?.toString()
            ?: throw RuntimeException("Ident not found in token")
}