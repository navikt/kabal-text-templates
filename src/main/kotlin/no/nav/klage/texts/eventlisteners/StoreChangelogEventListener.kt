package no.nav.klage.texts.eventlisteners

import no.nav.klage.texts.domain.TextChangedEvent
import no.nav.klage.texts.repositories.ChangelogRepository
import no.nav.klage.texts.util.getLogger
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class StoreChangelogEventListener(private val changelogRepository: ChangelogRepository) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @EventListener
    fun storeChangelog(textChangedEvent: TextChangedEvent) {
        logger.debug("Received TextChangedEvent for textId ${textChangedEvent.text.id} in StoreChangelogEventListener")
        if (textChangedEvent.changelogEntries.isNotEmpty()) {
            changelogRepository.saveAll(textChangedEvent.changelogEntries)
        }
    }
}
