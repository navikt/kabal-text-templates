package no.nav.klage.texts.service

import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_SEARCH
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_TEXTS
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT_SEARCH
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_MALTEKSTSEKSJON_VERSIONS
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_TEXT_VERSIONS
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service

@Service
class AdminService {
    @CacheEvict(
        cacheNames = [
            CONSUMER_TEXT_SEARCH,
            CONSUMER_MALTEKSTSEKSJON_SEARCH,
            CONSUMER_MALTEKSTSEKSJON_TEXTS,
            CONSUMER_TEXT,
            PUBLISHED_TEXT_VERSIONS,
            PUBLISHED_MALTEKSTSEKSJON_VERSIONS,
        ],
        allEntries = true
    )
    fun evictAllCaches() {

    }
}