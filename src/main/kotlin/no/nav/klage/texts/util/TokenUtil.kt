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

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun getIdent(): String =
        tokenValidationContextHolder.getTokenValidationContext().getJwtToken(SecurityConfiguration.ISSUER_AAD)
            ?.jwtTokenClaims?.get("NAVident")?.toString()
            ?: throw RuntimeException("Ident not found in token")

    fun getName(): String =
        tokenValidationContextHolder.getTokenValidationContext().getJwtToken(SecurityConfiguration.ISSUER_AAD)
            ?.jwtTokenClaims?.get("name")?.toString()
            ?: throw RuntimeException("Name not found in token")

    fun isAdmin(): Boolean {
        logger.debug("Checking if admin is available")
        val validationContext = tokenValidationContextHolder.getTokenValidationContext()
        secureLogger.debug("Validation context: $validationContext")
        val jwtToken = validationContext.getJwtToken(SecurityConfiguration.ISSUER_AAD)
        secureLogger.debug("jwt token: ${jwtToken.toString()}")
        val jwtTokenClaims = jwtToken?.jwtTokenClaims
        secureLogger.debug("jwt claims: ${jwtTokenClaims.toString()}")
        val groups = jwtTokenClaims?.get("groups")
        secureLogger.debug("groups: $groups")
        val groupsAsList = jwtTokenClaims?.getAsList("groups")
        secureLogger.debug("groupsAsList: $groupsAsList")

        return groupsAsList?.contains(kabalAdminRoleId) ?: false
    }
}