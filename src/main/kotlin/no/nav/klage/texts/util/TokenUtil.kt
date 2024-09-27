package no.nav.klage.texts.util

import no.nav.klage.texts.config.SecurityConfiguration
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TokenUtil(
    private val tokenValidationContextHolder: TokenValidationContextHolder,
    @Value("\${KABAL_ADMIN_ROLE_ID}") private val kabalAdminRoleId: String,
) {

    fun getIdent(): String =
        tokenValidationContextHolder.getTokenValidationContext().getJwtToken(SecurityConfiguration.ISSUER_AAD)
            ?.jwtTokenClaims?.get("NAVident")?.toString()
            ?: throw RuntimeException("Ident not found in token")

    fun getName(): String =
        tokenValidationContextHolder.getTokenValidationContext().getJwtToken(SecurityConfiguration.ISSUER_AAD)
            ?.jwtTokenClaims?.get("name")?.toString()
            ?: throw RuntimeException("Name not found in token")

    fun isAdmin(): Boolean {
        val roleIds = tokenValidationContextHolder.getTokenValidationContext().getJwtToken(SecurityConfiguration.ISSUER_AAD)
            ?.jwtTokenClaims?.getAsList("groups")

        return roleIds?.contains(kabalAdminRoleId) ?: false
    }
}