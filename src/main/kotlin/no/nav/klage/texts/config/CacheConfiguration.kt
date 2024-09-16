package no.nav.klage.texts.config


import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration

@EnableCaching
@Configuration
class CacheConfiguration {

    companion object {
        const val CONSUMER_TEXT_SEARCH = "consumerTextSearch"
        const val CONSUMER_MALTEKSTSEKSJON_SEARCH = "consumerMaltekstseksjonSearch"
        const val CONSUMER_MALTEKSTSEKSJON_TEXTS = "consumerMaltekstseksjonTexts"
        const val CONSUMER_TEXT = "consumerText"
        const val PUBLISHED_TEXT_VERSIONS = "publishedTextVersions"
        const val PUBLISHED_MALTEKSTSEKSJON_VERSIONS = "publishedMaltekstseksjonVersions"
    }
}