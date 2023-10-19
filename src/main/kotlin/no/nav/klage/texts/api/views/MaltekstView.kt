package no.nav.klage.texts.api.views

import java.time.LocalDateTime
import java.util.*

data class MaltekstView(
    val id: UUID,
    val title: String,
    val textIdList: Set<String> = emptySet(),
    val utfallIdList: Set<String> = emptySet(),
    val enhetIdList: Set<String> = emptySet(),
    val templateSectionIdList: Set<String> = emptySet(),
    val ytelseHjemmelIdList: Set<String> = emptySet(),
    val created: LocalDateTime,
    var modified: LocalDateTime,
)