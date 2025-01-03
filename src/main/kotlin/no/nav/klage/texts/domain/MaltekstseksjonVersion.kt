package no.nav.klage.texts.domain

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "maltekstseksjon_version", schema = "klage")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "MaltekstseksjonVersion.full",
        attributeNodes = [
            NamedAttributeNode("utfallIdList"),
            NamedAttributeNode("enhetIdList"),
            NamedAttributeNode("templateSectionIdList"),
            NamedAttributeNode("ytelseHjemmelIdList"),
            NamedAttributeNode("texts"),
            NamedAttributeNode("editors"),
            NamedAttributeNode("maltekstseksjon"),
        ]
    ),
)
class MaltekstseksjonVersion(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title")
    var title: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "maltekstseksjon_id", nullable = false, updatable = false)
    var maltekstseksjon: Maltekstseksjon,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        schema = "klage",
        name = "maltekstseksjon_version_text",
        joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")],
        inverseJoinColumns = [JoinColumn(name = "text_id")]
    )
    @OrderColumn(name = "index", nullable = false)
    val texts: MutableList<Text> = mutableListOf(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "klage", name = "utfall", joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")])
    @Column(name = "utfall")
    var utfallIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "klage", name = "enhet", joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")])
    @Column(name = "enhet")
    var enhetIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        schema = "klage",
        name = "template_section",
        joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")]
    )
    @Column(name = "template_section")
    var templateSectionIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        schema = "klage",
        name = "ytelse_hjemmel",
        joinColumns = [JoinColumn(name = "maltekstseksjon_version_id")]
    )
    @Column(name = "ytelse_hjemmel")
    var ytelseHjemmelIdList: Set<String> = emptySet(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "maltekstseksjon_version_id", referencedColumnName = "id", nullable = false)
    val editors: MutableSet<Editor> = mutableSetOf(),

    @Column(name = "published_date_time")
    var publishedDateTime: LocalDateTime?,
    @Column(name = "published_by")
    var publishedBy: String?,
    @Column(name = "published_by_name")
    var publishedByName: String?,
    /** Is it currently published? */
    @Column(name = "published")
    var published: Boolean,

    @Column(name = "created")
    var created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime,
): Serializable {

    fun isDepublished(): Boolean {
        return !published && publishedDateTime != null
    }

    fun createDraft(saksbehandlerIdent: String, saksbehandlerName: String, newMaltekstseksjonParent: Maltekstseksjon? = null): MaltekstseksjonVersion {
        val now = LocalDateTime.now()
        return MaltekstseksjonVersion(
            title = title,
            texts = texts.toMutableList(),
            utfallIdList = utfallIdList,
            enhetIdList = enhetIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
            maltekstseksjon = newMaltekstseksjonParent ?: maltekstseksjon,
            publishedDateTime = null,
            published = false,
            publishedBy = null,
            publishedByName = null,
            created = now,
            modified = now,
            editors = mutableSetOf(
                Editor(
                    navIdent = saksbehandlerIdent,
                    name = saksbehandlerName,
                    changeType = Editor.ChangeType.MALTEKSTSEKSJON_VERSION_CREATED
                )
            )
        )
    }

    fun resetDraftWithValuesFrom(source: MaltekstseksjonVersion) {
        val now = LocalDateTime.now()
        title = source.title
        maltekstseksjon = source.maltekstseksjon
        texts.clear()
        texts.addAll(source.texts)
        utfallIdList = source.utfallIdList
        enhetIdList = source.enhetIdList
        templateSectionIdList = source.templateSectionIdList
        ytelseHjemmelIdList = source.ytelseHjemmelIdList
        publishedDateTime = null
        published = false
        publishedBy = null
        publishedByName = null
        created = now
        modified = now
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MaltekstseksjonVersion

        if (id != other.id) return false
        if (title != other.title) return false
        if (maltekstseksjon != other.maltekstseksjon) return false
        if (texts != other.texts) return false
        if (utfallIdList != other.utfallIdList) return false
        if (enhetIdList != other.enhetIdList) return false
        if (templateSectionIdList != other.templateSectionIdList) return false
        if (ytelseHjemmelIdList != other.ytelseHjemmelIdList) return false
        if (editors != other.editors) return false
        if (publishedBy != other.publishedBy) return false
        if (publishedByName != other.publishedByName) return false
        if (published != other.published) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + maltekstseksjon.hashCode()
        result = 31 * result + texts.hashCode()
        result = 31 * result + utfallIdList.hashCode()
        result = 31 * result + enhetIdList.hashCode()
        result = 31 * result + templateSectionIdList.hashCode()
        result = 31 * result + ytelseHjemmelIdList.hashCode()
        result = 31 * result + editors.hashCode()
        result = 31 * result + (publishedBy?.hashCode() ?: 0)
        result = 31 * result + (publishedByName?.hashCode() ?: 0)
        result = 31 * result + published.hashCode()
        return result
    }

    override fun toString(): String {
        return "MaltekstseksjonVersion(id=$id, title='$title', maltekstseksjon=$maltekstseksjon, texts=$texts, utfallIdList=$utfallIdList, enhetIdList=$enhetIdList, templateSectionIdList=$templateSectionIdList, ytelseHjemmelIdList=$ytelseHjemmelIdList, editors=$editors, publishedDateTime=$publishedDateTime, publishedBy=$publishedBy, publishedByName=$publishedByName, published=$published, created=$created, modified=$modified)"
    }
}