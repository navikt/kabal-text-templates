package no.nav.klage.texts.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import no.nav.klage.texts.api.views.EditorView
import no.nav.klage.texts.api.views.MaltekstseksjonView
import no.nav.klage.texts.api.views.TextView
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.TextVersion
import java.util.*

fun mapToMaltekstView(maltekstseksjonVersion: MaltekstseksjonVersion): MaltekstseksjonView =
    MaltekstseksjonView(
        id = maltekstseksjonVersion.maltekstseksjon.id,
        title = maltekstseksjonVersion.title,
        versionId = maltekstseksjonVersion.id,
        textIdList = maltekstseksjonVersion.texts.map { it.id.toString() },
        utfallIdList = maltekstseksjonVersion.utfallIdList,
        enhetIdList = maltekstseksjonVersion.enhetIdList,
        templateSectionIdList = maltekstseksjonVersion.templateSectionIdList,
        ytelseHjemmelIdList = maltekstseksjonVersion.ytelseHjemmelIdList,
        created = maltekstseksjonVersion.created,
        modified = maltekstseksjonVersion.modified,
        editors = maltekstseksjonVersion.editors.map {
            EditorView(
                navIdent = it.navIdent,
                created = it.created,
                changeType = it.changeType,

            )
        }.sortedByDescending { it.created },
        publishedDateTime = maltekstseksjonVersion.publishedDateTime,
        publishedBy = maltekstseksjonVersion.publishedBy,
        published = maltekstseksjonVersion.published,
        createdBy = maltekstseksjonVersion.maltekstseksjon.createdBy,
    )

fun mapToTextView(textVersion: TextVersion, connectedMaltekstseksjonIdList: Pair<List<UUID>, List<UUID>>): TextView =
    TextView(
        id = textVersion.text.id,
        versionId = textVersion.id,
        title = textVersion.title,
        textType = textVersion.textType,
        richTextNN = if (textVersion.richTextNN != null) jsonMapper().readTree(textVersion.richTextNN) else null,
        richTextNB = if (textVersion.richTextNB != null) jsonMapper().readTree(textVersion.richTextNB) else null,
        richTextUntranslated = if (textVersion.richTextUntranslated != null) jsonMapper().readTree(textVersion.richTextUntranslated) else null,
        plainTextNN = textVersion.plainTextNN,
        plainTextNB = textVersion.plainTextNB,
        version = textVersion.smartEditorVersion,
        created = textVersion.created,
        modified = textVersion.modified,
        utfallIdList = textVersion.utfallIdList,
        enhetIdList = textVersion.enhetIdList,
        templateSectionIdList = textVersion.templateSectionIdList,
        ytelseHjemmelIdList = textVersion.ytelseHjemmelIdList,
        editors = textVersion.editors.map {
            EditorView(
                navIdent = it.navIdent,
                created = it.created,
                changeType = it.changeType,
            )
        }.sortedByDescending { it.created },
        publishedDateTime = textVersion.publishedDateTime,
        publishedBy = textVersion.publishedBy,
        published = textVersion.published,
        publishedMaltekstseksjonIdList = connectedMaltekstseksjonIdList.first,
        draftMaltekstseksjonIdList = connectedMaltekstseksjonIdList.second,
        createdBy = textVersion.text.createdBy,
    )
