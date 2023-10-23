package no.nav.klage.texts.domain

data class TextChangedEvent (
    val textVersion: TextVersion,
    val changelogEntries: List<ChangelogEntry>
)
