package no.nav.klage.texts.service

import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_SEARCH
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_TEXTS
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT_SEARCH
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_MALTEKSTSEKSJON_VERSIONS
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_TEXT_VERSIONS
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepository
import no.nav.klage.texts.repositories.TextVersionRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository,
    private val textVersionRepository: TextVersionRepository,
) {
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

    fun refillCaches() {
        maltekstseksjonVersionRepository.findByPublishedIsTrueForConsumer()
        textVersionRepository.findByPublishedIsTrueForConsumer()
    }
}