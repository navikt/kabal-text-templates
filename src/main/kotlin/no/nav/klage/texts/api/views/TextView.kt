package no.nav.klage.texts.api.views

import tools.jackson.databind.JsonNode
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

data class TextView(
    val id: UUID,

    val versionId: UUID,
    val title: String,
    val textType: String,
    val richText: RichText?,
    val plainText: PlainText?,
    val created: LocalDateTime,
    val modified: LocalDateTime,
    val createdBy: String,
    val createdByActor: Employee,

    val utfallIdList: Set<String>,
    val enhetIdList: Set<String>,
    val templateSectionIdList: Set<String>,
    val ytelseHjemmelIdList: Set<String>,

    val edits: List<TextEditView>,
    val publishedDateTime: LocalDateTime?,
    val publishedByActor: Employee?,
    val published: Boolean,

    val draftMaltekstseksjonIdList: List<UUID>,
    val publishedMaltekstseksjonIdList: List<UUID>
)

data class TextViewForLists(
    val id: UUID,

    val title: String,
    val textType: String,
    val richText: RichText?,
    val plainText: PlainText?,
    val created: LocalDateTime,
    val modified: LocalDateTime,

    val publishedDateTime: LocalDateTime?,
    val published: Boolean,

    val draftMaltekstseksjonIdList: List<UUID>,
    val publishedMaltekstseksjonIdList: List<UUID>
)

data class RichText(
    val nn: JsonNode?,
    val nb: JsonNode?,
    val untranslated: JsonNode?,
)

data class PlainText(
    val nn: String?,
    val nb: String?,
)

data class ConsumerTextView(
    val id: UUID,
    val title: String,
    val textType: String,
    val richText: JsonNode?,
    val plainText: String?,
    val enhetIdList: Set<String>,
    val templateSectionIdList: Set<String>,
    val ytelseHjemmelIdList: Set<String>,
    val utfallIdList: Set<String>,
    val language: Language,
    val publishedDateTime: LocalDateTime,
) : Serializable