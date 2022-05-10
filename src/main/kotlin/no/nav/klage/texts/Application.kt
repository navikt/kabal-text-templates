package no.nav.klage.texts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication


@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
class Application

fun main() {
    runApplication<Application>()
}