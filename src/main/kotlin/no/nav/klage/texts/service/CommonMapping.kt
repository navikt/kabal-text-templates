package no.nav.klage.texts.service

import com.fasterxml.jackson.module.kotlin.jsonMapper
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.TextVersion
import java.util.*

fun mapToMaltekstseksjonView(maltekstseksjonVersion: MaltekstseksjonVersion): MaltekstseksjonView =
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
            MaltekstseksjonEditorView(
                navIdent = it.navIdent,
                actor = Employee(
                    navIdent = it.navIdent,
                    navn = it.editorName
                ),
                created = it.created,
                changeType = MaltekstseksjonEditorView.ChangeTypeMaltekstseksjon.valueOf(it.changeType.name),
            )
        }.sortedByDescending { it.created },
        publishedDateTime = maltekstseksjonVersion.publishedDateTime,
        publishedBy = maltekstseksjonVersion.publishedBy,
        publishedByActor = maltekstseksjonVersion.publishedBy?.let {
            Employee(
                navIdent = it,
                navn = maltekstseksjonVersion.publishedByName,
            )
        },
        published = maltekstseksjonVersion.published,
        createdBy = maltekstseksjonVersion.maltekstseksjon.createdBy,
        createdByActor = Employee(
            navIdent = maltekstseksjonVersion.maltekstseksjon.createdBy,
            navn = maltekstseksjonVersion.maltekstseksjon.createdByName,
        ),
    )

fun mapToTextView(textVersion: TextVersion, connectedMaltekstseksjonIdList: Pair<List<UUID>, List<UUID>>): TextView =
    TextView(
        id = textVersion.text.id,
        versionId = textVersion.id,
        title = textVersion.title,
        textType = textVersion.textType,
        richText = fillRichText(textVersion),
        plainText = fillPlainText(textVersion),
        created = textVersion.created,
        modified = textVersion.modified,
        utfallIdList = textVersion.utfallIdList,
        enhetIdList = textVersion.enhetIdList,
        templateSectionIdList = textVersion.templateSectionIdList,
        ytelseHjemmelIdList = textVersion.ytelseHjemmelIdList,
        editors = textVersion.editors.map {
            TextEditorView(
                navIdent = it.navIdent,
                actor = Employee(
                    navIdent = it.navIdent,
                    navn = it.navIdent,
                ),
                created = it.created,
                changeType = TextEditorView.ChangeTypeText.valueOf(it.changeType.name),
            )
        }.sortedByDescending { it.created },
        publishedDateTime = textVersion.publishedDateTime,
        publishedBy = textVersion.publishedBy,
        publishedByActor = textVersion.publishedBy?.let {
            Employee(
                navIdent = it,
                navn = textVersion.publishedByName,
            )
        },
        published = textVersion.published,
        publishedMaltekstseksjonIdList = connectedMaltekstseksjonIdList.first,
        draftMaltekstseksjonIdList = connectedMaltekstseksjonIdList.second,
        createdBy = textVersion.text.createdBy,
        createdByActor = textVersion.text.createdBy?.let {
            Employee(
                navIdent = it,
                navn = textVersion.text.createdByName,
            )
        },
    )

fun mapToSearchableListItem(textVersion: TextVersion): SearchableListItem =
    SearchableListItem(
        id = textVersion.text.id,
        title = textVersion.title,
        textType = textVersion.textType,
        richText = fillRichText(textVersion),
        plainText = fillPlainText(textVersion),
        modified = textVersion.editors.maxByOrNull { it.created }?.created ?: textVersion.created,
    )

private fun fillRichText(textVersion: TextVersion): TextView.RichText? =
    if (textVersion.richTextNN != null || textVersion.richTextNB != null || textVersion.richTextUntranslated != null) {
        TextView.RichText(
            nn = if (textVersion.richTextNN != null) jsonMapper().readTree(textVersion.richTextNN) else null,
            nb = if (textVersion.richTextNB != null) jsonMapper().readTree(textVersion.richTextNB) else null,
            untranslated = if (textVersion.richTextUntranslated != null) jsonMapper().readTree(textVersion.richTextUntranslated) else null,
        )
    } else {
        null
    }

private fun fillPlainText(textVersion: TextVersion): TextView.PlainText? =
    if (textVersion.plainTextNN != null || textVersion.plainTextNB != null) {
        TextView.PlainText(
            nn = textVersion.plainTextNN,
            nb = textVersion.plainTextNB,
        )
    } else {
        null
    }