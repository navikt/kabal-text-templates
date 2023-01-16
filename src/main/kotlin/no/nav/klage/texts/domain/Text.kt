package no.nav.klage.texts.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "text", schema = "klage")
class Text(
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

    @ElementCollection
    @CollectionTable(schema = "klage", name = "hjemmel", joinColumns = [JoinColumn(name = "text_id")])
    @Column(name = "hjemmel")
    var hjemler: Set<String> = emptySet(),

    @ElementCollection
    @CollectionTable(schema = "klage", name = "ytelse", joinColumns = [JoinColumn(name = "text_id")])
    @Column(name = "ytelse")
    var ytelser: Set<String> = emptySet(),

    @ElementCollection
    @CollectionTable(schema = "klage", name = "utfall", joinColumns = [JoinColumn(name = "text_id")])
    @Column(name = "utfall")
    var utfall: Set<String> = emptySet(),

    @ElementCollection
    @CollectionTable(schema = "klage", name = "enhet", joinColumns = [JoinColumn(name = "text_id")])
    @Column(name = "enhet")
    var enheter: Set<String> = emptySet(),

    @ElementCollection
    @CollectionTable(schema = "klage", name = "section", joinColumns = [JoinColumn(name = "text_id")])
    @Column(name = "section")
    var sections: Set<String> = emptySet(),

    @ElementCollection
    @CollectionTable(schema = "klage", name = "template", joinColumns = [JoinColumn(name = "text_id")])
    @Column(name = "template")
    var templates: Set<String> = emptySet(),

    @Column(name = "created")
    val created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Text

        if (id != other.id) return false
        if (title != other.title) return false
        if (textType != other.textType) return false
        if (content != other.content) return false
        if (plainText != other.plainText) return false
        if (hjemler != other.hjemler) return false
        if (ytelser != other.ytelser) return false
        if (utfall != other.utfall) return false
        if (enheter != other.enheter) return false
        if (sections != other.sections) return false
        if (templates != other.templates) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + textType.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + plainText.hashCode()
        result = 31 * result + hjemler.hashCode()
        result = 31 * result + ytelser.hashCode()
        result = 31 * result + utfall.hashCode()
        result = 31 * result + enheter.hashCode()
        result = 31 * result + sections.hashCode()
        result = 31 * result + templates.hashCode()
        return result
    }

    override fun toString(): String {
        return "Text(id=$id, title='$title', textType='$textType', content=$content, plainText=$plainText, smartEditorVersion=$smartEditorVersion, hjemler=$hjemler, ytelser=$ytelser, utfall=$utfall, enheter=$enheter, sections=$sections, templates=$templates, created=$created, modified=$modified)"
    }

}