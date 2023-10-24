package no.nav.klage.texts.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "maltekstseksjon_version", schema = "klage")
class MaltekstseksjonVersion(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title")
    var title: String,
    @Column(name = "maltekstseksjon_id")
    val maltekstseksjonId: UUID,

    @OneToMany
    @JoinColumn(name = "maltekstseksjon_version_id")
    @JoinTable(schema = "klage", name = "maltekstseksjon_version_text", inverseJoinColumns = [JoinColumn(name = "text_id")])
    @OrderColumn(name = "index", nullable = false)
    var texts: List<Text> = emptyList(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "klage", name = "utfall", joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")])
    @Column(name = "utfall")
    var utfallIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "klage", name = "enhet", joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")])
    @Column(name = "enhet")
    var enhetIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        schema = "klage",
        name = "template_section",
        joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")]
    )
    @Column(name = "template_section")
    var templateSectionIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        schema = "klage",
        name = "ytelse_hjemmel",
        joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")]
    )
    @Column(name = "ytelse_hjemmel")
    var ytelseHjemmelIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        schema = "klage",
        name = "maltekstseksjon_version_editor",
        joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")]
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

    fun createDraft(id: UUID? = null): MaltekstseksjonVersion {
        val now = LocalDateTime.now()
        return MaltekstseksjonVersion(
            id = id ?: UUID.randomUUID(),
            title = title,
            texts = texts,
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
            maltekstseksjonId = maltekstseksjonId,
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

        other as MaltekstseksjonVersion

        if (id != other.id) return false
        if (title != other.title) return false
        if (texts != other.texts) return false
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
        result = 31 * result + texts.hashCode()
        result = 31 * result + utfallIdList.hashCode()
        result = 31 * result + enhetIdList.hashCode()
        result = 31 * result + templateSectionIdList.hashCode()
        result = 31 * result + ytelseHjemmelIdList.hashCode()
        result = 31 * result + editors.hashCode()
        result = 31 * result + (publishedBy?.hashCode() ?: 0)
        result = 31 * result + published.hashCode()
        return result
    }

    override fun toString(): String {
        return "MaltekstseksjonVersion(id=$id, title='$title', texts=$texts, utfallIdList=$utfallIdList, enhetIdList=$enhetIdList, templateSectionIdList=$templateSectionIdList, ytelseHjemmelIdList=$ytelseHjemmelIdList, editors=$editors, publishedDateTime=$publishedDateTime, publishedBy=$publishedBy, published=$published, created=$created, modified=$modified)"
    }

}