package no.nav.klage.texts.repositories


import jakarta.persistence.EntityManager
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_TEXT_VERSIONS
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.util.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.system.measureTimeMillis

@Component
class TextVersionRepositoryStreamingFacade(
    private val textVersionRepository: TextVersionRepository,
    private val entityManager: EntityManager,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Cacheable(PUBLISHED_TEXT_VERSIONS)
    @Transactional(readOnly = true)
    fun findByPublishedIsTrueForConsumer(): List<TextVersion> {
        val textVersions = mutableListOf<TextVersion>()
        val millis = measureTimeMillis {
            textVersionRepository.findByPublishedIsTrueForConsumer().use { stream ->
                stream.forEach { textVersion ->
                    textVersions += textVersion
                    //detach to avoid memory leaks
                    entityManager.detach(textVersion)
                }
            }
        }

        logger.debug("findByPublishedIsTrueForConsumer streamed took {} millis. Found {} texts", millis, textVersions.size)
        return textVersions
    }

    @Transactional(readOnly = true)
    fun findByPublishedDateTimeIsNull(): List<TextVersion> {
        val textVersions = mutableListOf<TextVersion>()
        val millis = measureTimeMillis {
            textVersionRepository.findByPublishedDateTimeIsNullOrderById().use { stream ->
                stream.forEach { textVersion ->
                    textVersions += textVersion
                    //detach to avoid memory leaks
                    entityManager.detach(textVersion)
                }
            }
        }

        logger.debug("findByPublishedDateTimeIsNull streamed took {} millis. Found {} texts", millis, textVersions.size)

        return textVersions
    }

    @Transactional(readOnly = true)
    fun findByPublishedIsTrue(): List<TextVersion> {
        val textVersions = mutableListOf<TextVersion>()
        val millis = measureTimeMillis {
            textVersionRepository.findByPublishedIsTrueOrderById().use { stream ->
                stream.forEach { textVersion ->
                    textVersions += textVersion
                    //detach to avoid memory leaks
                    entityManager.detach(textVersion)
                }
            }
        }

        logger.debug("findByPublishedIsTrue streamed took {} millis. Found {} texts", millis, textVersions.size)

        return textVersions
    }

}