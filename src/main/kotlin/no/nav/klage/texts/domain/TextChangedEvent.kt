package no.nav.klage.texts.domain

data class TextChangedEvent (
    val text: Text,
    val changelogEntries: List<ChangelogEntry>
)
