package no.nav.klage.texts.domain

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "text_version", schema = "klage")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "TextVersion.full",
        attributeNodes = [
            NamedAttributeNode("utfallIdList"),
            NamedAttributeNode("enhetIdList"),
            NamedAttributeNode("templateSectionIdList"),
            NamedAttributeNode("ytelseHjemmelIdList"),
            NamedAttributeNode("text"),
            NamedAttributeNode("editors"),
        ]
    ),
)
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "klage", name = "utfall", joinColumns = [JoinColumn(name = "text_version_id")])
    @Column(name = "utfall")
    var utfallIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "klage", name = "enhet", joinColumns = [JoinColumn(name = "text_version_id")])
    @Column(name = "enhet")
    var enhetIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "klage", name = "template_section", joinColumns = [JoinColumn(name = "text_version_id")])
    @Column(name = "template_section")
    var templateSectionIdList: Set<String> = emptySet(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(schema = "klage", name = "ytelse_hjemmel", joinColumns = [JoinColumn(name = "text_version_id")])
    @Column(name = "ytelse_hjemmel")
    var ytelseHjemmelIdList: Set<String> = emptySet(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "text_version_id", referencedColumnName = "id", nullable = false)
    val editors: MutableSet<Editor> = mutableSetOf(),

    @ManyToOne(optional = false)
    @JoinColumn(name = "text_id", nullable = false, updatable = false)
    var text: Text,

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
) : Serializable {

    fun createDraft(saksbehandlerIdent: String, saksbehandlerName: String): TextVersion {
        val now = LocalDateTime.now()
        return TextVersion(
            title = title,
            textType = textType,
            richTextNN = richTextNN,
            richTextNB = richTextNB,
            richTextUntranslated = richTextUntranslated,
            plainTextNN = plainTextNN,
            plainTextNB = plainTextNB,
            text = text,
            enhetIdList = enhetIdList,
            utfallIdList = utfallIdList,
            templateSectionIdList = templateSectionIdList,
            ytelseHjemmelIdList = ytelseHjemmelIdList,
            publishedDateTime = null,
            published = false,
            publishedBy = null,
            publishedByName = null,
            created = now,
            modified = now,
            editors = mutableSetOf(
                Editor(
                    navIdent = saksbehandlerIdent,
                    editorName = saksbehandlerName,
                    changeType = Editor.ChangeType.TEXT_VERSION_CREATED,
                )
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextVersion

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "TextVersion(id=$id, title='$title', textType='$textType', richTextNN=$richTextNN, richTextNB=$richTextNB, richTextUntranslated=$richTextUntranslated, plainTextNN=$plainTextNN, plainTextNB=$plainTextNB, utfallIdList=$utfallIdList, enhetIdList=$enhetIdList, templateSectionIdList=$templateSectionIdList, ytelseHjemmelIdList=$ytelseHjemmelIdList, editors=$editors, text=$text, publishedDateTime=$publishedDateTime, publishedBy=$publishedBy, publishedByName=$publishedByName, published=$published, created=$created, modified=$modified)"
    }

}