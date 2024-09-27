package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Editor
import no.nav.klage.texts.domain.Text
import no.nav.klage.texts.domain.TextVersion
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TextVersionRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var textVersionRepository: TextVersionRepository

    @Test
    fun `add textVersion works`() {
        val text = getText()
        val textVersion = getUnpublishedTextVersion(text)

        textVersionRepository.save(textVersion)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundText = textVersionRepository.findById(textVersion.id).get()
        assertThat(foundText).isEqualTo(textVersion)
    }

    @Test
    fun `findByPublishedDateTimeIsNull works`() {
        val text = getText()
        val text2 = getText()
        val unpublishedTextVersion1 = getUnpublishedTextVersion(text)
        val unpublishedTextVersion2 = getUnpublishedTextVersion(text2)

        textVersionRepository.save(unpublishedTextVersion1)
        textVersionRepository.save(unpublishedTextVersion2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundUnpublishedTextVersions = textVersionRepository.findByPublishedDateTimeIsNull()
        assertThat(foundUnpublishedTextVersions.size).isEqualTo(2)
    }

    @Test
    fun `findByPublishedIsTrue works`() {
        val text = getText()
        val text2 = getText()
        val publishedTextVersion1 = getPublishedTextVersion(text)
        val publishedTextVersion2 = getPublishedTextVersion(text2)

        textVersionRepository.save(publishedTextVersion1)
        textVersionRepository.save(publishedTextVersion2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundPublishedTextVersions = textVersionRepository.findByPublishedIsTrue()
        assertThat(foundPublishedTextVersions.size).isEqualTo(2)
    }

    @Test
    fun `find hidden texts works`() {
        val text = getText()
        val text2 = getText()
        val publishedTextVersion1 = getPublishedTextVersion(text)
        publishedTextVersion1.published = false

        val publishedTextVersion2 = getPublishedTextVersion(text2)

        textVersionRepository.save(publishedTextVersion1)
        textVersionRepository.save(publishedTextVersion2)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundHiddenTexts = textVersionRepository.findHiddenTextVersions()
        assertThat(foundHiddenTexts).containsExactlyInAnyOrder(publishedTextVersion1)
    }

    private fun getUnpublishedTextVersion(
        text: Text,
    ): TextVersion {
        val now = LocalDateTime.now()
        return TextVersion(
            title = "title",
            textType = "type",
            richTextNN = null,
            richTextNB = "{}",
            richTextUntranslated = null,
            plainTextNN = null,
            plainTextNB = null,
            publishedDateTime = null,
            publishedBy = null,
            publishedByName = null,
            published = false,
            text = text,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    name = "saksbehandlerName",
                    created = now,
                    changeType = Editor.ChangeType.TEXT_VERSION_CREATED,
                )
            ),
            created = now,
            modified = now,
        )
    }

    private fun getPublishedTextVersion(
        text: Text,
    ): TextVersion {
        val now = LocalDateTime.now()
        return TextVersion(
            title = "title",
            textType = "type",
            richTextNN = null,
            richTextNB = null,
            richTextUntranslated = null,
            plainTextNN = null,
            plainTextNB = null,
            publishedDateTime = now,
            publishedBy = "ident",
            publishedByName = "name",
            published = true,
            text = text,
            utfallIdList = setOf("1"),
            enhetIdList = setOf("1"),
            templateSectionIdList = setOf("1"),
            ytelseHjemmelIdList = setOf("1"),
            editors = mutableSetOf(
                Editor(
                    navIdent = "saksbehandlerIdent",
                    name = "saksbehandlerName",
                    created = now,
                    changeType = Editor.ChangeType.TEXT_VERSION_CREATED,
                )
            ),
            created = now,
            modified = now,
        )
    }

    private fun getText(): Text {
        val now = LocalDateTime.now()
        val text = testEntityManager.persist(
            Text(
                created = now,
                modified = now,
                maltekstseksjonVersions = mutableListOf(),
                createdBy = "abc",
                createdByName = "abc"
            )
        )
        return text
    }
}
