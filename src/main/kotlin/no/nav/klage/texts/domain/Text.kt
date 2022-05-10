package no.nav.klage.texts.domain

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "text", schema = "klage")
class Text(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "title")
    var title: String,
    @Column(name = "type")
    var type: String,
    @Column(name = "content")
    var content: String,

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

    @Column(name = "created")
    val created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Text

        if (id != other.id) return false
        if (title != other.title) return false
        if (type != other.type) return false
        if (content != other.content) return false
        if (hjemler != other.hjemler) return false
        if (ytelser != other.ytelser) return false
        if (utfall != other.utfall) return false
        if (enheter != other.enheter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + hjemler.hashCode()
        result = 31 * result + ytelser.hashCode()
        result = 31 * result + utfall.hashCode()
        result = 31 * result + enheter.hashCode()
        return result
    }

    override fun toString(): String {
        return "Text(id=$id, title='$title', type='$type', content='$content', hjemler=$hjemler, ytelser=$ytelser, utfall=$utfall, enheter=$enheter, created=$created, modified=$modified)"
    }


}