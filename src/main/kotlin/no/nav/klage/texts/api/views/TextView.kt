package no.nav.klage.texts.api.views

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime
import java.util.*

data class TextView(
    val id: UUID,

    val versionId: UUID,
    val title: String,
    val textType: String,
    val content: JsonNode?,
    val plainText: String?,
    val version: Int?,
    val created: LocalDateTime,
    val modified: LocalDateTime,
    val createdBy: String?,

    val utfallIdList: Set<String>,
    val enhetIdList: Set<String>,
    val templateSectionIdList: Set<String>,
    val ytelseHjemmelIdList: Set<String>,

    val editors: List<EditorView>,
    val publishedDateTime: LocalDateTime?,
    val publishedBy: String?,
    val published: Boolean,

    val draftMaltekstseksjonIdList: List<UUID>,
    val publishedMaltekstseksjonIdList: List<UUID>
)

data class ConsumerTextView(
    val id: UUID,
    val textType: String,
    val content: JsonNode?,
    val plainText: String?,
    val utfallIdList: Set<String>,
    val enhetIdList: Set<String>,
    val templateSectionIdList: Set<String>,
    val ytelseHjemmelIdList: Set<String>,
)