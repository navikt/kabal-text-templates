package no.nav.klage.texts.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "text_version", schema = "klage")
class TextVersion(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "title")
    var title: String,
    @Column(name = "text_type")
    var textType: String,
    @Column(name = "rich_text_nn")
    var richTextNN: String?,
    @Column(name = "rich_text_nb")
    var richTextNB: String?,
    @Column(name = "rich_text_untranslated")
    var richTextUntranslated: String?,
    @Column(name = "plain_text_nn")
    var plainTextNN: String?,
    @Column(name = "plain_text_nb")
    var plainTextNB: String?,
    @Column(name = "smarteditor_version")
    var smartEditorVersion: Int?,

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

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "text_version_id", referencedColumnName = "id", nullable = false)
    val editors: MutableSet<Editor> = mutableSetOf(),

    @ManyToOne(optional = false)
    @JoinColumn(name = "text_id", nullable = false, updatable = false)
    var text: Text,

    @Column(name = "published_date_time")
    var publishedDateTime: LocalDateTime?,
    @Column(name = "published_by")
    var publishedBy: String?,
    /** Is it currently published? */
    @Column(name = "published")
    var published: Boolean,

    @Column(name = "created")
    var created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime,
) {

    fun createDraft(): TextVersion {
        val now = LocalDateTime.now()
        return TextVersion(
            title = title,
            textType = textType,
            richTextNN = richTextNN,
            richTextNB = richTextNB,
            richTextUntranslated = richTextUntranslated,
            plainTextNN = plainTextNN,
            plainTextNB = plainTextNB,
            smartEditorVersion = smartEditorVersion,
            text = text,
            enhetIdList = enhetIdList,
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
        if (richTextNN != other.richTextNN) return false
        if (richTextNB != other.richTextNB) return false
        if (richTextUntranslated != other.richTextUntranslated) return false
        if (plainTextNN != other.plainTextNN) return false
        if (plainTextNB != other.plainTextNB) return false
        if (smartEditorVersion != other.smartEditorVersion) return false
        if (utfallIdList != other.utfallIdList) return false
        if (enhetIdList != other.enhetIdList) return false
        if (templateSectionIdList != other.templateSectionIdList) return false
        if (ytelseHjemmelIdList != other.ytelseHjemmelIdList) return false
        if (editors != other.editors) return false
        if (text != other.text) return false
        if (publishedDateTime != other.publishedDateTime) return false
        if (publishedBy != other.publishedBy) return false
        if (published != other.published) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + textType.hashCode()
        result = 31 * result + (richTextNN?.hashCode() ?: 0)
        result = 31 * result + (richTextNB?.hashCode() ?: 0)
        result = 31 * result + (richTextUntranslated?.hashCode() ?: 0)
        result = 31 * result + (plainTextNN?.hashCode() ?: 0)
        result = 31 * result + (plainTextNB?.hashCode() ?: 0)
        result = 31 * result + (smartEditorVersion ?: 0)
        result = 31 * result + utfallIdList.hashCode()
        result = 31 * result + enhetIdList.hashCode()
        result = 31 * result + templateSectionIdList.hashCode()
        result = 31 * result + ytelseHjemmelIdList.hashCode()
        result = 31 * result + editors.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + (publishedDateTime?.hashCode() ?: 0)
        result = 31 * result + (publishedBy?.hashCode() ?: 0)
        result = 31 * result + published.hashCode()
        return result
    }

    override fun toString(): String {
        return "TextVersion(id=$id, title='$title', textType='$textType', richTextNN=$richTextNN, richTextNB=$richTextNB, richTextUntranslated=$richTextUntranslated, plainTextNN=$plainTextNN, plainTextNB=$plainTextNB, smartEditorVersion=$smartEditorVersion, utfallIdList=$utfallIdList, enhetIdList=$enhetIdList, templateSectionIdList=$templateSectionIdList, ytelseHjemmelIdList=$ytelseHjemmelIdList, editors=$editors, text=$text, publishedDateTime=$publishedDateTime, publishedBy=$publishedBy, published=$published, created=$created, modified=$modified)"
    }

}