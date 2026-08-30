package no.nav.klage.texts.service

import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_SEARCH
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_TEXTS
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT_SEARCH
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_MALTEKSTSEKSJON_VERSIONS
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_TEXT_VERSIONS
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepository
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepositoryStreamingFacade
import no.nav.klage.texts.repositories.TextVersionRepository
import no.nav.klage.texts.repositories.TextVersionRepositoryStreamingFacade
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository,
    private val maltekstseksjonVersionRepositoryStreamingFacade: MaltekstseksjonVersionRepositoryStreamingFacade,
    private val textVersionRepository: TextVersionRepository,
    private val textVersionRepositoryStreamingFacade: TextVersionRepositoryStreamingFacade,
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
        allEntries = true,
    )
    fun evictAllCaches() {
    }

    fun refillCaches() {
        maltekstseksjonVersionRepositoryStreamingFacade.findByPublishedIsTrueForConsumer()
        textVersionRepositoryStreamingFacade.findByPublishedIsTrueForConsumer()
    }

    @CacheEvict(
        cacheNames = [
            CONSUMER_TEXT_SEARCH,
            CONSUMER_MALTEKSTSEKSJON_SEARCH,
            CONSUMER_MALTEKSTSEKSJON_TEXTS,
            CONSUMER_TEXT,
            PUBLISHED_TEXT_VERSIONS,
            PUBLISHED_MALTEKSTSEKSJON_VERSIONS,
        ],
        allEntries = true,
    )
    @Transactional
    fun fixNavSpelling() {
        // Replace NAV with Nav in all titles and texts

        val navFrom = "NAV"
        val navTo = "Nav"

        val navKlageinstansFrom = "NAV Klageinstans"
        val navKlageinstansTo = "Nav klageinstans"

        val nayFrom = "NAV Arbeid og ytelser"
        val nayTo = "Nav arbeid og ytelser"

        textVersionRepository.findAll().forEach {
            // NAV Klageinstans til Nav klageinstans

            it.title = it.title.replace(oldValue = navKlageinstansFrom, newValue = navKlageinstansTo)

            it.richTextNB = it.richTextNB?.replace(oldValue = navKlageinstansFrom, newValue = navKlageinstansTo)
            it.richTextNN = it.richTextNN?.replace(oldValue = navKlageinstansFrom, newValue = navKlageinstansTo)

            it.plainTextNB = it.plainTextNB?.replace(oldValue = navKlageinstansFrom, newValue = navKlageinstansTo)
            it.plainTextNN = it.plainTextNN?.replace(oldValue = navKlageinstansFrom, newValue = navKlageinstansTo)

            it.richTextUntranslated = it.richTextUntranslated?.replace(oldValue = navKlageinstansFrom, newValue = navKlageinstansTo)

            // NAV Arbeid og ytelser til Nav arbeid og ytelser

            it.title = it.title.replace(oldValue = nayFrom, newValue = nayTo)

            it.richTextNB = it.richTextNB?.replace(oldValue = nayFrom, newValue = nayTo)
            it.richTextNN = it.richTextNN?.replace(oldValue = nayFrom, newValue = nayTo)

            it.plainTextNB = it.plainTextNB?.replace(oldValue = nayFrom, newValue = nayTo)
            it.plainTextNN = it.plainTextNN?.replace(oldValue = nayFrom, newValue = nayTo)

            it.richTextUntranslated = it.richTextUntranslated?.replace(oldValue = nayFrom, newValue = nayTo)

            // The rest

            it.title = it.title.replace(oldValue = navFrom, newValue = navTo)

            it.richTextNB = it.richTextNB?.replace(oldValue = navFrom, newValue = navTo)
            it.richTextNN = it.richTextNN?.replace(oldValue = navFrom, newValue = navTo)

            it.plainTextNB = it.plainTextNB?.replace(oldValue = navFrom, newValue = navTo)
            it.plainTextNN = it.plainTextNN?.replace(oldValue = navFrom, newValue = navTo)

            it.richTextUntranslated = it.richTextUntranslated?.replace(oldValue = navFrom, newValue = navTo)
        }

        maltekstseksjonVersionRepository.findAll().forEach {
            // NAV Klageinstans til Nav klageinstans
            it.title = it.title.replace(oldValue = navKlageinstansFrom, newValue = navKlageinstansTo)

            // NAV Arbeid og ytelser til Nav arbeid og ytelser
            it.title = it.title.replace(oldValue = nayFrom, newValue = nayTo)

            // The rest
            it.title = it.title.replace(oldValue = navFrom, newValue = navTo)
        }
    }
}
