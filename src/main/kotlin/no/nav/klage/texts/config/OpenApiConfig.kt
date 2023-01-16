package no.nav.klage.texts.config

import no.nav.klage.texts.api.TextController
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun apiInternal(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .packagesToScan(TextController::class.java.packageName)
            .group("default")
            .pathsToMatch("/**")
            .build()
    }

}
