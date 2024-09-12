package no.nav.klage.texts.config


import no.nav.klage.texts.util.getLogger
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import java.time.Duration

@EnableCaching
@Configuration
class CacheWithRedisCacheConfiguration(private val environment: Environment) : RedisCacheManagerBuilderCustomizer {

    companion object {

        const val CONSUMER_TEXT_SEARCH = "consumerTextSearch"
        const val CONSUMER_MALTEKSTSEKSJON_SEARCH = "consumerMaltekstseksjonSearch"
        const val CONSUMER_MALTEKSTSEKSJON_TEXTS = "consumerMaltekstseksjonTexts"
        const val CONSUMER_TEXT = "consumerText"
        const val PUBLISHED_TEXT_VERSIONS = "publishedTextVersions"
        const val PUBLISHED_MALTEKSTSEKSJON_VERSIONS = "publishedMaltekstseksjonVersions"

        val cacheKeys =
            listOf(
                CONSUMER_TEXT_SEARCH,
                CONSUMER_MALTEKSTSEKSJON_SEARCH,
                CONSUMER_MALTEKSTSEKSJON_TEXTS,
                CONSUMER_TEXT,
                PUBLISHED_TEXT_VERSIONS,
                PUBLISHED_MALTEKSTSEKSJON_VERSIONS,
            )

        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    override fun customize(builder: RedisCacheManager.RedisCacheManagerBuilder) {
        cacheKeys.forEach { cacheName ->
            builder.withCacheConfiguration(
                cacheName,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(standardDuration(cacheName))
            )
        }
    }

    private fun standardDuration(cacheName: String) =
        if (cacheName in listOf(PUBLISHED_TEXT_VERSIONS, PUBLISHED_MALTEKSTSEKSJON_VERSIONS)) {
            Duration.ofDays(365)
        } else if (environment.activeProfiles.contains("prod-gcp")) {
            Duration.ofMinutes(10L)
        } else {
            Duration.ofMinutes(5L)
        }
}

