package no.nav.klage.texts.api.views

import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

data class MaltekstseksjonView(
    val id: UUID,
    val title: String,
    val versionId: UUID,
    val textIdList: List<String> = emptyList(),
    val utfallIdList: Set<String> = emptySet(),
    val enhetIdList: Set<String> = emptySet(),
    val templateSectionIdList: Set<String> = emptySet(),
    val ytelseHjemmelIdList: Set<String> = emptySet(),
    val created: LocalDateTime,
    var modified: LocalDateTime,
    val createdBy: String?,

    val editors: List<MaltekstseksjonEditorView>,
    val publishedDateTime: LocalDateTime?,
    val publishedBy: String?,
    val published: Boolean,
)

data class ConsumerMaltekstseksjonView(
    val id: UUID,
    val textIdList: List<String> = emptyList(),
    val utfallIdList: Set<String> = emptySet(),
    val enhetIdList: Set<String> = emptySet(),
    val templateSectionIdList: Set<String> = emptySet(),
    val ytelseHjemmelIdList: Set<String> = emptySet(),
) : Serializable

data class MaltekstseksjonWithTextsView(
    val maltekstseksjon: MaltekstseksjonView,
    val publishedTexts: List<TextView>,
)