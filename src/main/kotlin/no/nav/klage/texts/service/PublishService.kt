package no.nav.klage.texts.service

import no.nav.klage.texts.api.views.VersionInput
import no.nav.klage.texts.domain.Editor
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
            ) ?: throw ClientErrorException("ikke noe utkast funnet som kan publiseres"))

        validateTextsAreNotEmptyWhenPublishingTogetherWithMaltekstseksjon(maltekstseksjonVersionDraft)

        val now = LocalDateTime.now()
        maltekstseksjonVersionDraft.publishedDateTime = now
        maltekstseksjonVersionDraft.published = true
        maltekstseksjonVersionDraft.publishedBy = saksbehandlerIdent

        val textVersions = maltekstseksjonVersionDraft.texts.filter {
            textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
                textId = it.id
            ) != null
        }.map {
            publishTextVersion(textId = it.id, saksbehandlerIdent = saksbehandlerIdent, timestamp = now)
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
            ) ?: throw ClientErrorException("ikke noe utkast funnet som kan publiseres"))

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
            throw ClientErrorException("kan ikke publisere maltekstseksjon fordi det mangler en publisert versjon av en eller flere maltekster")
        }
    }

    private fun validateTextsAreNotEmptyWhenPublishingTogetherWithMaltekstseksjon(maltekstseksjonVersion: MaltekstseksjonVersion) {
        if (maltekstseksjonVersion.texts.isEmpty()) {
            throw ClientErrorException("kan ikke publisere maltekstseksjon sammen med tekster fordi det mangler tekster")
        }
    }

    fun createNewDraft(
        maltekstseksjonId: UUID,
        versionInput: VersionInput?,
        saksbehandlerIdent: String,
    ): MaltekstseksjonVersion {
        val existingVersion = if (versionInput != null) {
            maltekstseksjonVersionRepository.findById(versionInput.versionId).get()
        } else {
            maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            ) ?: throw ClientErrorException("det må finnes en publisert versjon før et nytt utkast kan lages")
        }

        val existingDraft = maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        )

        return if (existingDraft != null) {
            //Reset draft
            existingDraft.resetDraftWithValuesFrom(existingVersion)
            existingDraft.editors += Editor(
                navIdent = saksbehandlerIdent,
                changeType = Editor.ChangeType.MALTEKSTSEKSJON_VERSION_CREATED,
            )
            existingDraft
        } else {
            maltekstseksjonVersionRepository.save(
                existingVersion.createDraft(saksbehandlerIdent = saksbehandlerIdent)
            )
        }
    }

    fun publishTextVersion(textId: UUID, saksbehandlerIdent: String, timestamp: LocalDateTime): TextVersion {
        val possiblePreviouslyPublishedVersion = textVersionRepository.findByPublishedIsTrueAndTextId(textId)
        if (possiblePreviouslyPublishedVersion != null) {
            possiblePreviouslyPublishedVersion.published = false
        }

        val textVersionDraft =
            textVersionRepository.findByPublishedDateTimeIsNullAndTextId(
                textId = textId
            ) ?: throw ClientErrorException("ikke noe utkast funnet som kan publiseres")

        textVersionDraft.publishedDateTime = timestamp
        textVersionDraft.published = true
        textVersionDraft.publishedBy = saksbehandlerIdent

        return textVersionDraft
    }
}