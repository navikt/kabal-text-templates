package no.nav.klage.texts.service

import com.fasterxml.jackson.module.kotlin.jsonMapper
import no.nav.klage.texts.api.views.*
import no.nav.klage.texts.domain.MaltekstseksjonVersion
import no.nav.klage.texts.domain.TextVersion
import java.time.LocalDateTime
import java.util.*

fun mapToMaltekstseksjonView(maltekstseksjonVersion: MaltekstseksjonVersion, modifiedOrTextsModified: LocalDateTime?): MaltekstseksjonView =
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
        edits = maltekstseksjonVersion.editors.map {
            MaltekstseksjonEditView(
                actor = Employee(
                    navIdent = it.navIdent,
                    navn = it.name
                ),
                created = it.created,
                changeType = MaltekstseksjonEditView.ChangeTypeMaltekstseksjon.valueOf(it.changeType.name),
            )
        }.sortedByDescending { it.created },
        publishedDateTime = maltekstseksjonVersion.publishedDateTime,
        publishedBy = maltekstseksjonVersion.publishedBy,
        publishedByActor = if (maltekstseksjonVersion.publishedBy != null && maltekstseksjonVersion.publishedByName != null) {
            Employee(
                navIdent = maltekstseksjonVersion.publishedBy!!,
                navn = maltekstseksjonVersion.publishedByName!!,
            )
        } else null,
        published = maltekstseksjonVersion.published,
        createdBy = maltekstseksjonVersion.maltekstseksjon.createdBy,
        createdByActor = Employee(
            navIdent = maltekstseksjonVersion.maltekstseksjon.createdBy,
            navn = maltekstseksjonVersion.maltekstseksjon.createdByName,
        ),
        modifiedOrTextsModified = modifiedOrTextsModified,
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
        edits = textVersion.editors.map {
            TextEditView(
                actor = Employee(
                    navIdent = it.navIdent,
                    navn = it.name,
                ),
                created = it.created,
                changeType = TextEditView.ChangeTypeText.valueOf(it.changeType.name),
            )
        }.sortedByDescending { it.created },
        publishedDateTime = textVersion.publishedDateTime,
        publishedBy = textVersion.publishedBy,
        publishedByActor = if (textVersion.publishedBy != null && textVersion.publishedByName != null) {
            Employee(
                navIdent = textVersion.publishedBy!!,
                navn = textVersion.publishedByName!!,
            )
        } else null,
        published = textVersion.published,
        publishedMaltekstseksjonIdList = connectedMaltekstseksjonIdList.first,
        draftMaltekstseksjonIdList = connectedMaltekstseksjonIdList.second,
        createdBy = textVersion.text.createdBy,
        createdByActor = Employee(
            navIdent = textVersion.text.createdBy,
            navn = textVersion.text.createdByName,
        ),
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