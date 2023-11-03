package no.nav.klage.texts.service

import no.nav.klage.texts.api.views.VersionInput
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.exceptions.ClientErrorException
import no.nav.klage.texts.exceptions.MaltekstseksjonNotFoundException
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepository
import no.nav.klage.texts.repositories.TextVersionRepository
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Transactional
@Service
class PublishMaltekstseksjonService(
    private val maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository,
    private val textVersionRepository: TextVersionRepository,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun publishMaltekstseksjonVersion(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        overrideDraft: MaltekstseksjonVersion? = null
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val possiblePublishedVersion =
            maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(maltekstseksjonId)

        if (possiblePublishedVersion != null) {
            possiblePublishedVersion.published = false
        }

        val maltekstseksjonVersionDraft = overrideDraft
            ?: (maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            ) ?: throw ClientErrorException("there was no draft to publish"))

        validateTextsAreNotOnlyDrafts(maltekstseksjonVersionDraft)

        maltekstseksjonVersionDraft.publishedDateTime = LocalDateTime.now()
        maltekstseksjonVersionDraft.published = true
        maltekstseksjonVersionDraft.publishedBy = saksbehandlerIdent

        return maltekstseksjonVersionDraft
    }

    private fun validateTextsAreNotOnlyDrafts(maltekstseksjonVersion: MaltekstseksjonVersion) {
        if (maltekstseksjonVersion.texts.any {
                textVersionRepository.findByPublishedIsTrueAndTextId(it.id) == null
            }
        ) {
            throw ClientErrorException("Kan ikke publisere maltekstseksjon fordi en eller flere av maltekstene ikke har en publisert versjon.")
        }
    }

    fun createNewDraft(
        maltekstseksjonId: UUID,
        versionInput: VersionInput?,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsDeleted(maltekstseksjonId = maltekstseksjonId)

        val existingVersion = if (versionInput != null) {
            maltekstseksjonVersionRepository.getReferenceById(versionInput.versionId)
        } else {
            maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            ) ?: throw ClientErrorException("must exist a published version before a draft is created")
        }

        val existingDraft = maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        )

        return if (existingDraft != null) {
            //Reset draft
            existingDraft.resetDraftWithValuesFrom(existingVersion)
            existingDraft
        } else {
            maltekstseksjonVersionRepository.save(
                existingVersion.createDraft()
            )
        }
    }

    private fun validateIfMaltekstseksjonIsDeleted(maltekstseksjonId: UUID) {
        if (maltekstseksjonVersionRepository.findByMaltekstseksjonId(maltekstseksjonId)
                .none { it.published || it.publishedDateTime == null }
        ) {
            throw MaltekstseksjonNotFoundException("Maltekstseksjon $maltekstseksjonId er avpublisert eller finnes ikke.")
        }
    }
}