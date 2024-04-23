package no.nav.klage.texts.api.views

import no.nav.klage.texts.domain.Editor
import java.time.LocalDateTime
import java.util.*

data class EditorView(
    val navIdent: String,
    val created: LocalDateTime,
    val changeType: Editor.ChangeType,
)

data class DeletedText(
    val maltekstseksjonVersions: List<MaltekstseksjonVersionWithId>,
) {
    data class MaltekstseksjonVersionWithId(
        val maltekstseksjonId: UUID,
        val maltekstseksjonVersions: List<MaltekstseksjonView>,
    )
}