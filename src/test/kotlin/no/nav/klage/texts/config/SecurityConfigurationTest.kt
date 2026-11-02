package no.nav.klage.texts.config

import no.nav.klage.auth.test.AzureSecurityContractTest
import no.nav.klage.texts.api.AdminController
import no.nav.klage.texts.service.AdminService
import no.nav.klage.texts.util.TokenUtil
import org.junit.jupiter.api.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AdminController::class)
@Import(SecurityConfiguration::class, TokenUtil::class)
@TestPropertySource(properties = ["KABAL_ADMIN_ROLE_ID=$ADMIN_GROUP"])
class SecurityConfigurationTest : AzureSecurityContractTest() {
    @MockitoBean
    lateinit var adminService: AdminService

    override fun protectedRequest() = get("/admin/evictcaches")

    // A valid token without the admin group passes Spring Security and is stopped by the admin check in the controller.
    override fun assertAccepted(result: ResultActions) {
        result.andExpect(status().isForbidden)
    }

    @Test
    fun `admin group in token gives access to admin endpoint`() {
        mockMvc
            .perform(
                get("/admin/evictcaches")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${mockAzure.token(claims = mapOf("groups" to listOf(ADMIN_GROUP)))}"),
            ).andExpect(status().isOk)

        verify(adminService).evictAllCaches()
    }

    @Test
    fun `other groups in token do not give access to admin endpoint`() {
        mockMvc
            .perform(
                get("/admin/evictcaches")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${mockAzure.token(claims = mapOf("groups" to listOf("some-other-group")))}"),
            ).andExpect(status().isForbidden)

        verify(adminService, never()).evictAllCaches()
    }
}

private const val ADMIN_GROUP = "kabal-admin-group-id"
