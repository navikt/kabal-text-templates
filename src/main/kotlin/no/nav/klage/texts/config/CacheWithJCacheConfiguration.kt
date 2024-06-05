package no.nav.klage.texts.config


import no.nav.klage.texts.util.getLogger
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.util.concurrent.TimeUnit
import javax.cache.CacheManager
import javax.cache.configuration.MutableConfiguration
import javax.cache.expiry.CreatedExpiryPolicy
import javax.cache.expiry.Duration

@EnableCaching
@Configuration
class CacheWithJCacheConfiguration(private val environment: Environment) : JCacheManagerCustomizer {

    companion object {

        const val CONSUMER_TEXT_SEARCH = "consumerTextSearch"
        const val CONSUMER_MALTEKSTSEKSJON_SEARCH = "consumerMaltekstseksjonSearch"
        const val CONSUMER_MALTEKSTSEKSJON_TEXTS = "consumerMaltekstseksjonTexts"
        const val CONSUMER_TEXT = "consumerText"

        val cacheKeys =
            listOf(
                CONSUMER_TEXT_SEARCH,
                CONSUMER_MALTEKSTSEKSJON_SEARCH,
                CONSUMER_MALTEKSTSEKSJON_TEXTS,
                CONSUMER_TEXT,
            )

        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    override fun customize(cacheManager: CacheManager) {
        cacheKeys.forEach { cacheName ->
            cacheManager.createCache(cacheName, cacheConfiguration(standardDuration()))
        }
    }

    private fun cacheConfiguration(duration: Duration) =
        MutableConfiguration<Any, Any>()
            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration))
            .setStoreByValue(false)
            .setStatisticsEnabled(true)

    private fun standardDuration() =
        if (environment.activeProfiles.contains("prod-gcp")) {
            Duration(TimeUnit.MINUTES, 45L)
        } else {
            Duration(TimeUnit.MINUTES, 30L)
        }

}