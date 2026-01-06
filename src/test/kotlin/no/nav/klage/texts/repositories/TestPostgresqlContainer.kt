package no.nav.klage.texts.repositories

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.postgresql.PostgreSQLContainer

abstract class TestPostgresqlContainer {
    companion object {
        private val postgres = PostgreSQLContainer("postgres:15.4").apply {
            waitingFor(HostPortWaitStrategy())
            withReuse(true)
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }
}