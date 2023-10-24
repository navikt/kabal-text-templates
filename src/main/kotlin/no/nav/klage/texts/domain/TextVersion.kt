package no.nav.klage.texts.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "textVersion", schema = "klage")
class TextVersion(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "title")
    var title: String,
    @Column(name = "text_type")
    var textType: String,
    @Column(name = "content")
    var content: String?,
    @Column(name = "plain_text")
    var plainText: String?,
    @Column(name = "smarteditor_version")
    var smartEditorVersion: Int?,
    @Column(name = "text_id")
    val textId: UUID,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "klage", name = "utfall", joinColumns = [JoinColumn(name = "text_version_id")])
    @Column(name = "utfall")
    var utfallIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "klage", name = "enhet", joinColumns = [JoinColumn(name = "text_version_id")])
    @Column(name = "enhet")
    var enhetIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "klage", name = "template_section", joinColumns = [JoinColumn(name = "text_version_id")])
    @Column(name = "template_section")
    var templateSectionIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "klage", name = "ytelse_hjemmel", joinColumns = [JoinColumn(name = "text_version_id")])
    @Column(name = "ytelse_hjemmel")
    var ytelseHjemmelIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        schema = "klage",
        name = "text_version_editor",
        joinColumns = [JoinColumn(name = "text_version_id")]
    )
    @Column(name = "nav_ident")
    var editors: Set<String> = emptySet(),

    @Column(name = "published_date_time")
    var publishedDateTime: LocalDateTime?,
    @Column(name = "published_by")
    var publishedBy: String?,
    /** Is it currently published? */
    @Column(name = "published")
    var published: Boolean,

    @Column(name = "created")
    val created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime,
) {

    fun createDraft(id: UUID? = null): TextVersion {
        val now = LocalDateTime.now()
        return TextVersion(
            id = id ?: UUID.randomUUID(),
            title = title,
            textType = textType,
            content = content,
            plainText = plainText,
            smartEditorVersion = smartEditorVersion,
            textId = textId,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
            publishedDateTime = null,
            published = false,
            publishedBy = null,
            created = now,
            modified = now,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextVersion

        if (id != other.id) return false
        if (title != other.title) return false
        if (textType != other.textType) return false
        if (content != other.content) return false
        if (plainText != other.plainText) return false
        if (smartEditorVersion != other.smartEditorVersion) return false
        if (textId != other.textId) return false
        if (utfallIdList != other.utfallIdList) return false
        if (enhetIdList != other.enhetIdList) return false
        if (templateSectionIdList != other.templateSectionIdList) return false
        if (ytelseHjemmelIdList != other.ytelseHjemmelIdList) return false
        if (editors != other.editors) return false
        if (publishedBy != other.publishedBy) return false
        if (published != other.published) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + textType.hashCode()
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (plainText?.hashCode() ?: 0)
        result = 31 * result + (smartEditorVersion ?: 0)
        result = 31 * result + textId.hashCode()
        result = 31 * result + utfallIdList.hashCode()
        result = 31 * result + enhetIdList.hashCode()
        result = 31 * result + templateSectionIdList.hashCode()
        result = 31 * result + ytelseHjemmelIdList.hashCode()
        result = 31 * result + editors.hashCode()
        result = 31 * result + (publishedBy?.hashCode() ?: 0)
        result = 31 * result + published.hashCode()
        return result
    }

}