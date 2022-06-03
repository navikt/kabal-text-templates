package no.nav.klage.texts.domain

import java.time.LocalDateTime

object TextAggregateFunctions {

    fun Text.logCreation(
        input: Text,
        saksbehandlerident: String
    ): TextChangedEvent {
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.TEXT,
            fromValue = null,
            toValue = input.id.toString(),
            created = now,
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
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.TEXT,
            fromValue = input.id.toString(),
            toValue = null,
            created = now,
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
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueTitle = title
        title = newValueTitle
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.TITLE,
            fromValue = oldValueTitle,
            toValue = newValueTitle,
            created = now,
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
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueTextType = textType
        textType = newValueTextType
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.TEXT_TYPE,
            fromValue = oldValueTextType,
            toValue = newValueTextType,
            created = now,
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
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueContent = content
        content = newValueContent
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.CONTENT,
            fromValue = oldValueContent,
            toValue = newValueContent,
            created = now,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updatePlainText(
        newValueContent: String,
        saksbehandlerident: String
    ): TextChangedEvent {
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueContent = plainText
        plainText = newValueContent
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.PLAIN_TEXT,
            fromValue = oldValueContent,
            toValue = newValueContent,
            created = now,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateSmartEditorVersion(
        newValueContent: Int,
        saksbehandlerident: String
    ): TextChangedEvent {
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueContent = smartEditorVersion
        smartEditorVersion = newValueContent
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.SMARTEDITOR_VERSION,
            fromValue = oldValueContent?.toString(),
            toValue = newValueContent.toString(),
            created = now,
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
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueHjemler = hjemler
        hjemler = newValueHjemler
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.HJEMLER,
            fromValue = oldValueHjemler.joinToString(),
            toValue = newValueHjemler.joinToString(),
            created = now,
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
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueYtelser = ytelser
        ytelser = newValueYtelser
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.YTELSER,
            fromValue = oldValueYtelser.joinToString(),
            toValue = newValueYtelser.joinToString(),
            created = now,
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
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueUtfall = utfall
        utfall = newValueUtfall
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.UTFALL,
            fromValue = oldValueUtfall.joinToString(),
            toValue = newValueUtfall.joinToString(),
            created = now,
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
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueEnheter = enheter
        enheter = newValueEnheter
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.ENHETER,
            fromValue = oldValueEnheter.joinToString(),
            toValue = newValueEnheter.joinToString(),
            created = now,
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
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueSections = sections
        sections = newValueSections
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.SECTIONS,
            fromValue = oldValueSections.joinToString(),
            toValue = newValueSections.joinToString(),
            created = now,
        )?.let { changelogEntries.add(it) }

        return TextChangedEvent(
            text = this,
            changelogEntries = changelogEntries,
        )
    }

    fun Text.updateTemplates(
        newValueSections: Set<String>,
        saksbehandlerident: String
    ): TextChangedEvent {
        val now = LocalDateTime.now()
        val changelogEntries = mutableListOf<ChangelogEntry>()
        val oldValueSections = templates
        templates = newValueSections
        modified = now

        changelog(
            saksbehandlerident = saksbehandlerident,
            field = Field.TEMPLATES,
            fromValue = oldValueSections.joinToString(),
            toValue = newValueSections.joinToString(),
            created = now,
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
        created: LocalDateTime
    ): ChangelogEntry? {
        return ChangelogEntry.changelog(
            saksbehandlerident = saksbehandlerident,
            field = field,
            fromValue = fromValue,
            toValue = toValue,
            textId = this.id,
            created = created,
        )
    }
}