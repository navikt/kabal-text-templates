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
import org.springframework.transaction.annotation.Transactional

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

    @Transactional
    fun fixNavSpelling() {
        //Replace NAV with Nav in all titles and texts

        val navFrom = "NAV"
        val navTo = "Nav"

        val navKlageinstansFrom = "NAV Klageinstans"
        val navKlageinstansTo = "Nav klageinstans"

        val nayFrom = "NAV Arbeid og ytelser"
        val nayTo = "Nav arbeid og ytelser"

        textVersionRepository.findByPublishedIsTrue().forEach {
            //NAV Klageinstans til Nav klageinstans

            it.title = it.title.replace(navKlageinstansFrom, navKlageinstansTo)

            it.richTextNB = it.richTextNB?.replace(navKlageinstansFrom, navKlageinstansTo)
            it.richTextNN = it.richTextNN?.replace(navKlageinstansFrom, navKlageinstansTo)

            it.plainTextNB = it.plainTextNB?.replace(navKlageinstansFrom, navKlageinstansTo)
            it.plainTextNN = it.plainTextNN?.replace(navKlageinstansFrom, navKlageinstansTo)

            it.richTextUntranslated = it.richTextUntranslated?.replace(navKlageinstansFrom, navKlageinstansTo)

            //NAV Arbeid og ytelser til Nav arbeid og ytelser

            it.title = it.title.replace(nayFrom, nayTo)

            it.richTextNB = it.richTextNB?.replace(nayFrom, nayTo)
            it.richTextNN = it.richTextNN?.replace(nayFrom, nayTo)

            it.plainTextNB = it.plainTextNB?.replace(nayFrom, nayTo)
            it.plainTextNN = it.plainTextNN?.replace(nayFrom, nayTo)

            it.richTextUntranslated = it.richTextUntranslated?.replace(nayFrom, nayTo)

            //The rest

            it.title = it.title.replace(navFrom, navTo)

            it.richTextNB = it.richTextNB?.replace(navFrom, navTo)
            it.richTextNN = it.richTextNN?.replace(navFrom, navTo)

            it.plainTextNB = it.plainTextNB?.replace(navFrom, navTo)
            it.plainTextNN = it.plainTextNN?.replace(navFrom, navTo)

            it.richTextUntranslated = it.richTextUntranslated?.replace(navFrom, navTo)
        }

        maltekstseksjonVersionRepository.findByPublishedIsTrue().forEach {
            //NAV Klageinstans til Nav klageinstans
            it.title = it.title.replace(navKlageinstansFrom, navKlageinstansTo)

            //NAV Arbeid og ytelser til Nav arbeid og ytelser
            it.title = it.title.replace(nayFrom, nayTo)

            //The rest
            it.title = it.title.replace(navFrom, navTo)
        }
    }
}