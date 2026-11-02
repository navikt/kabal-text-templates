package no.nav.klage.texts.util

import no.nav.klage.auth.currentAzureToken
import no.nav.klage.auth.displayName
import no.nav.klage.auth.groups
import no.nav.klage.auth.navIdent
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TokenUtil(
    @Value($$"${KABAL_ADMIN_ROLE_ID}") private val kabalAdminRoleId: String,
) {
    fun getIdent(): String = currentAzureToken()?.navIdent ?: throw RuntimeException("Ident not found in token")

    fun getName(): String = currentAzureToken()?.displayName ?: throw RuntimeException("Name not found in token")

    fun isAdmin(): Boolean = currentAzureToken()?.groups?.contains(kabalAdminRoleId) ?: false
}
