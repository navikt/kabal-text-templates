package no.nav.klage.texts.api.views

import java.time.LocalDateTime
import java.util.*

data class TextEditorView(
    val navIdent: String,
    val created: LocalDateTime,
    val changeType: ChangeTypeText,
) {
    enum class ChangeTypeMaltekstseksjon {
        MALTEKSTSEKSJON,
        MALTEKSTSEKSJON_TITLE,
        MALTEKSTSEKSJON_TEXTS,
        MALTEKSTSEKSJON_VERSION_CREATED,
        SMART_EDITOR_VERSION,
        MALTEKSTSEKSJON_UTFALL,
        MALTEKSTSEKSJON_ENHETER,
        MALTEKSTSEKSJON_SECTIONS,
        MALTEKSTSEKSJON_YTELSE_HJEMMEL,
        UNKNOWN,
    }

    enum class ChangeTypeText {
        RICH_TEXT_NB,
        RICH_TEXT_NN,
        RICH_TEXT_UNTRANSLATED,
        PLAIN_TEXT_NB,
        PLAIN_TEXT_NN,
        TEXT_TYPE,
        TEXT_VERSION_CREATED,
        TEXT_TITLE,
        TEXT_UTFALL,
        TEXT_SECTIONS,
        TEXT_YTELSE_HJEMMEL,
        SMART_EDITOR_VERSION,
        TEXT_ENHETER,
        UNKNOWN,
    }
}

data class MaltekstseksjonEditorView(
    val navIdent: String,
    val created: LocalDateTime,
    val changeType: ChangeTypeMaltekstseksjon,
) {
    enum class ChangeTypeMaltekstseksjon {
        MALTEKSTSEKSJON_TITLE,
        MALTEKSTSEKSJON_TEXTS,
        MALTEKSTSEKSJON_VERSION_CREATED,
        SMART_EDITOR_VERSION,
        MALTEKSTSEKSJON_UTFALL,
        MALTEKSTSEKSJON_ENHETER,
        MALTEKSTSEKSJON_SECTIONS,
        MALTEKSTSEKSJON_YTELSE_HJEMMEL,
        UNKNOWN,
    }
}

data class DeletedText(
    val maltekstseksjonVersions: List<MaltekstseksjonVersionWithId>,
) {
    data class MaltekstseksjonVersionWithId(
        val maltekstseksjonId: UUID,
        val maltekstseksjonVersions: List<MaltekstseksjonView>,
    )
}