package no.nav.klage.texts.service

import no.nav.klage.texts.api.views.MaltekstseksjonInput
import no.nav.klage.texts.api.views.MaltekstseksjonView
import no.nav.klage.texts.api.views.MaltekstseksjonWithTextsView
import no.nav.klage.texts.api.views.VersionInput
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_SEARCH
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_MALTEKSTSEKSJON_TEXTS
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT
import no.nav.klage.texts.config.CacheConfiguration.Companion.CONSUMER_TEXT_SEARCH
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_MALTEKSTSEKSJON_VERSIONS
import no.nav.klage.texts.config.CacheConfiguration.Companion.PUBLISHED_TEXT_VERSIONS
import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.Maltekstseksjon
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.exceptions.ClientErrorException
import no.nav.klage.texts.exceptions.MaltekstseksjonNotFoundException
import no.nav.klage.texts.repositories.MaltekstseksjonRepository
import no.nav.klage.texts.repositories.MaltekstseksjonVersionRepository
import no.nav.klage.texts.repositories.TextRepository
import no.nav.klage.texts.util.getLogger
import no.nav.klage.texts.util.getSecureLogger
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import kotlin.system.measureTimeMillis

@Transactional
@Service
class MaltekstseksjonService(
    private val maltekstseksjonRepository: MaltekstseksjonRepository,
    private val maltekstseksjonVersionRepository: MaltekstseksjonVersionRepository,
    private val textRepository: TextRepository,
    private val textService: TextService,
    private val searchMaltekstseksjonService: SearchMaltekstseksjonService,
    private val publishService: PublishService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    fun publishMaltekstseksjonVersion(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        saksbehandlerName: String
    ): MaltekstseksjonView {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)
        return mapToMaltekstseksjonView(
            maltekstseksjonVersion = publishService.publishMaltekstseksjonVersion(
                maltekstseksjonId = maltekstseksjonId,
                saksbehandlerIdent = saksbehandlerIdent,
                saksbehandlerName = saksbehandlerName,
            ),
        )
    }

    fun publishMaltekstseksjonVersionWithTexts(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        saksbehandlerName: String,
    ): MaltekstseksjonWithTextsView {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)
        val (maltekstseksjonVersion, textVersions) = publishService.publishMaltekstseksjonVersionWithTexts(
            maltekstseksjonId = maltekstseksjonId,
            saksbehandlerIdent = saksbehandlerIdent,
            saksbehandlerName = saksbehandlerName,
        )

        return MaltekstseksjonWithTextsView(
            maltekstseksjon = mapToMaltekstseksjonView(
                maltekstseksjonVersion = maltekstseksjonVersion,
            ),
            publishedTexts = textVersions.map { textVersion ->
                mapToTextView(
                    textVersion = textVersion,
                    connectedMaltekstseksjonIdList = textService.getConnectedMaltekstseksjoner(textVersion.text.id)
                )
            }
        )
    }

    fun getMaltekstseksjonVersions(maltekstseksjonId: UUID): List<MaltekstseksjonView> {
        return maltekstseksjonVersionRepository.findByMaltekstseksjonId(maltekstseksjonId)
            .sortedByDescending { it.publishedDateTime ?: LocalDateTime.now() }.map {
                mapToMaltekstseksjonView(
                    maltekstseksjonVersion = it,
                )
            }
    }

    fun createNewMaltekstseksjon(
        maltekstseksjonInput: MaltekstseksjonInput,
        saksbehandlerIdent: String,
        saksbehandlerName: String,
    ): MaltekstseksjonView {
        val now = LocalDateTime.now()

        val maltekstseksjon = maltekstseksjonRepository.save(
            Maltekstseksjon(
                created = now,
                modified = now,
                createdBy = saksbehandlerIdent,
                createdByName = saksbehandlerName,
            )
        )

        return mapToMaltekstseksjonView(
            maltekstseksjonVersionRepository.save(
                MaltekstseksjonVersion(
                    title = maltekstseksjonInput.title,
                    utfallIdList = maltekstseksjonInput.utfallIdList,
                    enhetIdList = maltekstseksjonInput.enhetIdList,
                    templateSectionIdList = maltekstseksjonInput.templateSectionIdList,
                    ytelseHjemmelIdList = maltekstseksjonInput.ytelseHjemmelIdList,
                    editors = mutableSetOf(
                        Editor(
                            navIdent = saksbehandlerIdent,
                            name = saksbehandlerName,
                            changeType = Editor.ChangeType.MALTEKSTSEKSJON_VERSION_CREATED,
                        )
                    ),
                    maltekstseksjon = maltekstseksjon,
                    created = now,
                    modified = now,
                    publishedDateTime = null,
                    published = false,
                    publishedBy = null,
                    publishedByName = null,
                )
            )
        )
    }

    fun createNewDraft(
        maltekstseksjonId: UUID,
        versionInput: VersionInput?,
        saksbehandlerIdent: String,
        saksbehandlerName: String,
    ): MaltekstseksjonView {
        if (maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            ) != null
        ) {
            throw ClientErrorException("Utkast finnes allerede.")
        }

        return mapToMaltekstseksjonView(
            publishService.createNewDraft(
                maltekstseksjonId = maltekstseksjonId,
                versionInput = versionInput,
                saksbehandlerIdent = saksbehandlerIdent,
                saksbehandlerName = saksbehandlerName,
            )
        )
    }

    @CacheEvict(
        cacheNames = [
            PUBLISHED_MALTEKSTSEKSJON_VERSIONS,
            PUBLISHED_TEXT_VERSIONS,
            CONSUMER_TEXT_SEARCH,
            CONSUMER_MALTEKSTSEKSJON_SEARCH,
            CONSUMER_MALTEKSTSEKSJON_TEXTS,
            CONSUMER_TEXT,
        ],
        allEntries = true
    )
    fun unpublishMaltekstseksjon(
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
    ) {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val possiblePublishedTextVersion =
            maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(maltekstseksjonId)

        if (possiblePublishedTextVersion != null) {
            possiblePublishedTextVersion.published = false
            possiblePublishedTextVersion.modified = LocalDateTime.now()
        } else {
            throw ClientErrorException("fant ingen maltekstseksjon å avpublisere")
        }
    }

    fun deleteMaltekstseksjonDraftVersion(maltekstseksjonId: UUID, saksbehandlerIdent: String) {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)
        val existingDraft = maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        )
        if (existingDraft != null) {
            val maltekstseksjonVersions = getMaltekstseksjonVersions(maltekstseksjonId = maltekstseksjonId)

            maltekstseksjonVersionRepository.delete(existingDraft)

            if (maltekstseksjonVersions.size == 1) {
                maltekstseksjonRepository.deleteById(maltekstseksjonId)
            }
        }
    }

    fun getPublishedMaltekstseksjonVersion(maltekstseksjonId: UUID): MaltekstseksjonVersion {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        return maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        ) ?: throw ClientErrorException("det fins hverken utkast eller publisert versjon")
    }


    fun getCurrentMaltekstseksjonVersion(maltekstseksjonId: UUID): MaltekstseksjonView {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        return mapToMaltekstseksjonView(
            maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            ) ?: maltekstseksjonVersionRepository.findByPublishedIsTrueAndMaltekstseksjonId(
                maltekstseksjonId = maltekstseksjonId
            ) ?: throw ClientErrorException("det fins hverken utkast eller publisert versjon")
        )
    }

    fun updateTitle(
        input: String,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        saksbehandlerName: String,
    ): MaltekstseksjonView {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.title = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        maltekstseksjonVersion.editors += Editor(
            navIdent = saksbehandlerIdent,
            name = saksbehandlerName,
            changeType = Editor.ChangeType.MALTEKSTSEKSJON_TITLE,
        )
        return mapToMaltekstseksjonView(maltekstseksjonVersion)
    }

    fun updateTexts(
        input: List<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        saksbehandlerName: String,
    ): MaltekstseksjonView {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)

        maltekstseksjonVersion.texts.clear()
        maltekstseksjonVersion.texts.addAll(input.map { textRepository.getReferenceById(UUID.fromString(it)) })

        maltekstseksjonVersion.modified = LocalDateTime.now()
        maltekstseksjonVersion.editors += Editor(
            navIdent = saksbehandlerIdent,
            name = saksbehandlerName,
            changeType = Editor.ChangeType.MALTEKSTSEKSJON_TEXTS,
        )
        return mapToMaltekstseksjonView(maltekstseksjonVersion)
    }

    fun updateUtfall(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        saksbehandlerName: String,
    ): MaltekstseksjonView {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.utfallIdList = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        maltekstseksjonVersion.editors += Editor(
            navIdent = saksbehandlerIdent,
            name = saksbehandlerName,
            changeType = Editor.ChangeType.MALTEKSTSEKSJON_UTFALL,
        )
        return mapToMaltekstseksjonView(maltekstseksjonVersion)
    }

    fun updateEnheter(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        saksbehandlerName: String,
    ): MaltekstseksjonView {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.enhetIdList = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        maltekstseksjonVersion.editors += Editor(
            navIdent = saksbehandlerIdent,
            name = saksbehandlerName,
            changeType = Editor.ChangeType.MALTEKSTSEKSJON_ENHETER,
        )
        return mapToMaltekstseksjonView(maltekstseksjonVersion)
    }

    fun updateTemplateSectionList(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        saksbehandlerName: String,
    ): MaltekstseksjonView {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.templateSectionIdList = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        maltekstseksjonVersion.editors += Editor(
            navIdent = saksbehandlerIdent,
            name = saksbehandlerName,
            changeType = Editor.ChangeType.MALTEKSTSEKSJON_SECTIONS,
        )
        return mapToMaltekstseksjonView(maltekstseksjonVersion)
    }

    fun updateYtelseHjemmelList(
        input: Set<String>,
        maltekstseksjonId: UUID,
        saksbehandlerIdent: String,
        saksbehandlerName: String,
    ): MaltekstseksjonView {
        validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId = maltekstseksjonId)

        val maltekstseksjonVersion = getCurrentDraft(maltekstseksjonId)
        maltekstseksjonVersion.ytelseHjemmelIdList = input
        maltekstseksjonVersion.modified = LocalDateTime.now()
        maltekstseksjonVersion.editors += Editor(
            navIdent = saksbehandlerIdent,
            name = saksbehandlerName,
            changeType = Editor.ChangeType.MALTEKSTSEKSJON_YTELSE_HJEMMEL,
        )
        return mapToMaltekstseksjonView(maltekstseksjonVersion)
    }

    fun searchPublishedMaltekstseksjoner(
        textIdList: List<String>,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
    ): List<MaltekstseksjonVersion> {
        var maltekstseksjonVersions: List<MaltekstseksjonVersion>

        val millis = measureTimeMillis {
            maltekstseksjonVersions =
                maltekstseksjonVersionRepository.findByPublishedIsTrueForConsumer()
        }

        logger.debug(
            "searchMaltekstseksjoner getting all texts took {} millis. Found {} texts",
            millis,
            maltekstseksjonVersions.size
        )

        return searchMaltekstseksjonService.searchMaltekstseksjoner(
            maltekstseksjonVersions = maltekstseksjonVersions,
            textIdList = textIdList,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
        )
    }

    fun searchMaltekstseksjoner(
        textIdList: List<String>,
        utfallIdList: List<String>,
        enhetIdList: List<String>,
        templateSectionIdList: List<String>,
        ytelseHjemmelIdList: List<String>,
        trash: Boolean?,
    ): List<MaltekstseksjonView> {
        val maltekstseksjonVersions = if (trash == true) {
            getAllHiddenMaltekstsekjsonVersions()
        } else {
            getAllCurrentMaltekstseksjonVersions()
        }

        return searchMaltekstseksjonService.searchMaltekstseksjoner(
            maltekstseksjonVersions = maltekstseksjonVersions,
            textIdList = textIdList,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
        ).map {
            mapToMaltekstseksjonView(
                maltekstseksjonVersion = it,
            )
        }
    }

    private fun getAllHiddenMaltekstsekjsonVersions(): List<MaltekstseksjonVersion> {
        var maltekstseksjonVersions: List<MaltekstseksjonVersion>

        val millis = measureTimeMillis {
            val hiddenMaltekstseksjonVersions = maltekstseksjonVersionRepository.findHiddenMaltekstseksjonVersions()

            maltekstseksjonVersions = hiddenMaltekstseksjonVersions.groupBy { it.maltekstseksjon }
                .map { (_, maltekstseksjonVersions) ->
                    maltekstseksjonVersions.maxByOrNull { maltekstseksjonVersion ->
                        maltekstseksjonVersion.created
                    }!!
                }
        }

        logger.debug(
            "getting hidden maltekstseksjon versions took {} millis. Found {} maltekstseksjon versions",
            millis,
            maltekstseksjonVersions.size
        )
        return maltekstseksjonVersions
    }

    private fun getAllCurrentMaltekstseksjonVersions(): List<MaltekstseksjonVersion> {
        var maltekstseksjonVersions: List<MaltekstseksjonVersion>

        val millis = measureTimeMillis {
            //get all drafts
            val drafts =
                maltekstseksjonVersionRepository.findByPublishedDateTimeIsNull()
            //get published
            val published = maltekstseksjonVersionRepository.findByPublishedIsTrue()

            val draftsMaltekstseksjonList = drafts.map { it.maltekstseksjon }

            val publishedWithNoDrafts = published.filter { maltekstseksjonVersion ->
                maltekstseksjonVersion.maltekstseksjon !in draftsMaltekstseksjonList
            }

            maltekstseksjonVersions = drafts + publishedWithNoDrafts
        }

        logger.debug(
            "searchMaltekstseksjoner getting all maltekstseksjonVersions took {} millis. Found {} maltekstseksjonVersions",
            millis,
            maltekstseksjonVersions.size
        )
        return maltekstseksjonVersions
    }

    fun getAllMaltekstseksjonVersions(): List<MaltekstseksjonVersion> = maltekstseksjonVersionRepository.findAll()

    fun getMaltekstseksjonVersionsById(ids: List<UUID>): List<MaltekstseksjonVersion> =
        maltekstseksjonVersionRepository.findAllById(ids)

    fun updateAll(maltekstseksjonVersions: List<MaltekstseksjonVersion>): MutableList<MaltekstseksjonVersion> =
        maltekstseksjonVersionRepository.saveAll(maltekstseksjonVersions)

    private fun getCurrentDraft(maltekstseksjonId: UUID): MaltekstseksjonVersion {
        return maltekstseksjonVersionRepository.findByPublishedDateTimeIsNullAndMaltekstseksjonId(
            maltekstseksjonId = maltekstseksjonId
        ) ?: throw ClientErrorException("ikke noe utkast funnet")
    }

    private fun validateIfMaltekstseksjonIsUnpublished(maltekstseksjonId: UUID) {
        if (maltekstseksjonVersionRepository.findByMaltekstseksjonId(maltekstseksjonId)
                .none { it.published || it.publishedDateTime == null }
        ) {
            throw MaltekstseksjonNotFoundException("Maltekstseksjon $maltekstseksjonId er avpublisert eller finnes ikke.")
        }
    }
}