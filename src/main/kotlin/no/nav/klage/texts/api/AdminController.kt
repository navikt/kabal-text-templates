package no.nav.klage.texts.api;

import no.nav.klage.texts.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.texts.exceptions.MissingTilgangException
import no.nav.klage.texts.service.AdminService
import no.nav.klage.texts.util.TokenUtil
import no.nav.klage.texts.util.getLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = ISSUER_AAD)
class AdminController(
    private val tokenUtil: TokenUtil,
    private val adminService: AdminService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/admin/evictcaches")
    @ResponseStatus(HttpStatus.OK)
    fun evictCaches() {
        krevAdminTilgang()
        logger.debug("Evicting all caches")
        adminService.evictAllCaches()
        logger.debug("Evicted all caches")
    }

    @GetMapping("/admin/refillcaches")
    @ResponseStatus(HttpStatus.OK)
    fun refillCaches() {
        krevAdminTilgang()
        logger.debug("Refilling caches")
        adminService.refillCaches()
        logger.debug("Refilled caches")
    }

    private fun krevAdminTilgang() {
        if (!tokenUtil.isAdmin()) {
            throw MissingTilgangException("Not an admin")
        }
    }
}
