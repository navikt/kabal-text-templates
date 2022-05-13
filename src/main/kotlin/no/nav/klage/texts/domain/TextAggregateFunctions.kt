package no.nav.klage.texts.domain

import java.time.LocalDateTime

object TextAggregateFunctions {

    fun Text.logCreation(
        input: Text,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.TEXT,
            fromValue = null,
            toValue = input.id.toString(),
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.logDeletion(
        input: Text,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.DELETION,
            fromValue = input.id.toString(),
            toValue = null,
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateTitle(
        newValueTitle: String,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueTitle = title
        title = newValueTitle
        modified = timestamp

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.TITLE,
            fromValue = oldValueTitle,
            toValue = newValueTitle,
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateTextType(
        newValueTextType: String,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueTextType = textType
        textType = newValueTextType
        modified = timestamp

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.TEXT_TYPE,
            fromValue = oldValueTextType,
            toValue = newValueTextType,
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateContent(
        newValueContent: String,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueContent = content
        content = newValueContent
        modified = timestamp

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.CONTENT,
            fromValue = oldValueContent,
            toValue = newValueContent,
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateHjemler(
        newValueHjemler: Set<String>,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueHjemler = hjemler
        hjemler = newValueHjemler
        modified = timestamp

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.HJEMLER,
            fromValue = oldValueHjemler.joinToString(),
            toValue = newValueHjemler.joinToString(),
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateYtelser(
        newValueYtelser: Set<String>,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueYtelser = ytelser
        ytelser = newValueYtelser
        modified = timestamp

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.YTELSER,
            fromValue = oldValueYtelser.joinToString(),
            toValue = newValueYtelser.joinToString(),
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateUtfall(
        newValueUtfall: Set<String>,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueUtfall = utfall
        utfall = newValueUtfall
        modified = timestamp

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.UTFALL,
            fromValue = oldValueUtfall.joinToString(),
            toValue = newValueUtfall.joinToString(),
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateEnheter(
        newValueEnheter: Set<String>,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueEnheter = enheter
        enheter = newValueEnheter
        modified = timestamp

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.UTFALL,
            fromValue = oldValueEnheter.joinToString(),
            toValue = newValueEnheter.joinToString(),
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateSections(
        newValueSections: Set<String>,
        saksbehandlerident: String
    ): TextChangedEvent {
        val timestamp = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueSections = sections
        sections = newValueSections
        modified = timestamp

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.UTFALL,
            fromValue = oldValueSections.joinToString(),
            toValue = newValueSections.joinToString(),
            timestamp = timestamp,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    private fun Text.changelog(
        saksbehandlerident: String,
        field: Field,
        fromValue: String?,
        toValue: String?,
        timestamp: LocalDateTime
    ): ChangelogEntry? {
        return ChangelogEntry.changelog(
            saksbehandlerident,
            field,
            fromValue,
            toValue,
            this.id,
            timestamp
        )
    }
}