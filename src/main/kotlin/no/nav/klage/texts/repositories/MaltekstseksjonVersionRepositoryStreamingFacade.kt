package no.nav.klage.texts.repositories

import jakarta.persistence.EntityManager
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_MALTEKSTSEKSJON_VERSIONS
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.util.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.system.measureTimeMillis

@Component
class MaltekstseksjonVersionRepositoryStreamingFacade(
    private val maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository,
    private val entityManager: EntityManager,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Cacheable(PUBLISHED_MALTEKSTSEKSJON_VERSIONS)
    @Transactional(readOnly = true)
    fun findByPublishedIsTrueForConsumer(): List<MaltekstseksjonVersion> {
        val maltekstseksjonVersions = mutableListOf<MaltekstseksjonVersion>()
        val millis = measureTimeMillis {
            maltekstseksjonVersionRepository.findByPublishedIsTrueForConsumer().use { stream ->
                stream.forEach { maltekstseksjonVersion ->
                    maltekstseksjonVersions += maltekstseksjonVersion
                    //detach to avoid memory leaks
                    entityManager.detach(maltekstseksjonVersion)
                }
            }
        }

        logger.debug(
            "findByPublishedIsTrueForConsumer streamed took {} millis. Found {} maltekstseksjonVersions",
            millis,
            maltekstseksjonVersions.size
        )

        return maltekstseksjonVersions
    }

    @Transactional(readOnly = true)
    fun findByPublishedIsTrue(): List<MaltekstseksjonVersion> {
        val maltekstseksjonVersions = mutableListOf<MaltekstseksjonVersion>()
        val millis = measureTimeMillis {
            maltekstseksjonVersionRepository.findByPublishedIsTrueOrderById().use { stream ->
                stream.forEach { maltekstseksjonVersion ->
                    maltekstseksjonVersions += maltekstseksjonVersion
                    //detach to avoid memory leaks
                    entityManager.detach(maltekstseksjonVersion)
                }
            }
        }

        logger.debug(
            "findByPublishedIsTrue streamed took {} millis. Found {} maltekstseksjonVersions",
            millis,
            maltekstseksjonVersions.size
        )

        return maltekstseksjonVersions
    }

    @Transactional(readOnly = true)
    fun findByPublishedDateTimeIsNull(): List<MaltekstseksjonVersion> {
        val maltekstseksjonVersions = mutableListOf<MaltekstseksjonVersion>()
        val millis = measureTimeMillis {
            maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullOrderById().use { stream ->
                stream.forEach { maltekstseksjonVersion ->
                    maltekstseksjonVersions += maltekstseksjonVersion
                    //detach to avoid memory leaks
                    entityManager.detach(maltekstseksjonVersion)
                }
            }
        }

        logger.debug(
            "findByPublishedDateTimeIsNull streamed took {} millis. Found {} maltekstseksjonVersions",
            millis,
            maltekstseksjonVersions.size
        )

        return maltekstseksjonVersions
    }
}