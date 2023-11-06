package no.nav.klage.texts.service

import no.nav.klage.texts.api.views.VersionInput
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.TextVersion
import no.nav.klage.texts.exceptions.ClientErrorException
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
class PublishService(
    private val maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository,
    private val textVersionRepository: TextVersionRepository,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun publishMaltekstseksjonVersionWithTexts(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        overrideDraft: MaltekstseksjonVersion? = null
    ): Pair<MaltekstseksjonVersion, List<TextVersion>> {
        val possiblePublishedVersion =
            maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(maltekstseksjonId)

        if (possiblePublishedVersion != null) {
            possiblePublishedVersion.published = false
        }

        val maltekstseksjonVersionDraft = overrideDraft
            ?: (maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            ) ?: throw ClientErrorException("there was no draft to publish"))

        validateTextsAreNotEmptyWhenPublishingTogetherWithMaltekstseksjon(maltekstseksjonVersionDraft)

        maltekstseksjonVersionDraft.publishedDateTime = LocalDateTime.now()
        maltekstseksjonVersionDraft.published = true
        maltekstseksjonVersionDraft.publishedBy = saksbehandlerIdent

        val textVersions = maltekstseksjonVersionDraft.texts.filter {
            textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
                textId = it.id
            ) != null
        }.map {
            publishTextVersion(textId = it.id, saksbehandlerIdent = saksbehandlerIdent)
        }

        return maltekstseksjonVersionDraft to textVersions
    }

    fun publishMaltekstseksjonVersion(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        overrideDraft: MaltekstseksjonVersion? = null
    ): MaltekstseksjonVersion {
        val possiblePublishedVersion =
            maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(maltekstseksjonId)

        if (possiblePublishedVersion != null) {
            possiblePublishedVersion.published = false
        }

        val maltekstseksjonVersionDraft = overrideDraft
            ?: (maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            ) ?: throw ClientErrorException("there was no draft to publish"))

        validateTextsAreNotEmptyOrOnlyDrafts(maltekstseksjonVersionDraft)

        maltekstseksjonVersionDraft.publishedDateTime = LocalDateTime.now()
        maltekstseksjonVersionDraft.published = true
        maltekstseksjonVersionDraft.publishedBy = saksbehandlerIdent

        return maltekstseksjonVersionDraft
    }

    private fun validateTextsAreNotEmptyOrOnlyDrafts(maltekstseksjonVersion: MaltekstseksjonVersion) {
        if (maltekstseksjonVersion.texts.isEmpty() || maltekstseksjonVersion.texts.any {
                textVersionRepository.findByPublishedIsTrueAndTextId(it.id) == null
            }
        ) {
            throw ClientErrorException("Kan ikke publisere maltekstseksjon fordi det mangler en publisert versjon av en eller flere maltekster.")
        }
    }

    private fun validateTextsAreNotEmptyWhenPublishingTogetherWithMaltekstseksjon(maltekstseksjonVersion: MaltekstseksjonVersion) {
        if (maltekstseksjonVersion.texts.isEmpty()) {
            throw ClientErrorException("Kan ikke publisere maltekstseksjon sammen med maltekstseksjon fordi det mangler maltekster.")
        }
    }

    fun createNewDraft(
        maltekstseksjonId: UUID,
        versionInput: VersionInput?,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
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

    fun publishTextVersion(textId: UUID, saksbehandlerIdent: String): TextVersion {
        val possiblePreviouslyPublishedVersion = textVersionRepository.findByPublishedIsTrueAndTextId(textId)
        if (possiblePreviouslyPublishedVersion != null) {
            possiblePreviouslyPublishedVersion.published = false
        }

        val textVersionDraft =
            textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
                textId = textId
            ) ?: throw ClientErrorException("there was no draft to publish")

        textVersionDraft.publishedDateTime = LocalDateTime.now()
        textVersionDraft.published = true
        textVersionDraft.publishedBy = saksbehandlerIdent

        return textVersionDraft
    }
}