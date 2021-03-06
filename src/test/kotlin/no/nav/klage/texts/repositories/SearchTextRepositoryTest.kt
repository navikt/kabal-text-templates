package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text
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
class SearchTextRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var textRepository: TextRepository

    @Test
    fun `search text works`() {
        val now = LocalDateTime.now()

        val text1 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            hjemler = setOf("ha", "hb1"),
            ytelser = setOf("ya", "yb1"),
            utfall = setOf("ua", "ub1"),
            enheter = setOf("ea", "eb1"),
            sections = setOf("sa", "sb1"),
            templates = setOf("ta", "tb1"),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            hjemler = setOf("ha", "hb2"),
            ytelser = setOf("ya", "yb2"),
            utfall = setOf("ua", "ub2"),
            enheter = setOf("ea", "eb2"),
            sections = setOf("sa", "sb2"),
            templates = setOf("ta", "tb2"),
            created = now,
            modified = now,
        )

        val text3 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            hjemler = setOf("ha", "hb3"),
            ytelser = setOf("ya", "yb3"),
            utfall = setOf("ua", "ub3"),
            enheter = setOf("ea", "eb3"),
            sections = setOf("sa", "sb3"),
            templates = setOf("ta", "tb3"),
            created = now,
            modified = now,
        )

        val text4 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            hjemler = setOf("ha", "hb4"),
            ytelser = setOf("ya", "yb4"),
            utfall = setOf("ua", "ub4"),
            enheter = setOf("ea", "eb4"),
            sections = setOf("sa", "sb4"),
            templates = setOf("ta", "tb4"),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)
        textRepository.save(text3)
        textRepository.save(text4)

        testEntityManager.flush()
        testEntityManager.clear()

        var foundTexts = textRepository.searchTexts(
            textType = "type",
            requiredSection = null,
            utfall = listOf(),
            ytelser = listOf(),
            hjemler = listOf("hb1"),
            enheter = listOf(),
            sections = listOf(),
            templates = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1)

        foundTexts = textRepository.searchTexts(
            textType = "type",
            requiredSection = "sb4",
            utfall = listOf(),
            ytelser = listOf(),
            hjemler = listOf(),
            enheter = listOf(),
            sections = listOf(),
            templates = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text4)

        foundTexts = textRepository.searchTexts(
            textType = "type",
            requiredSection = null,
            utfall = listOf(),
            ytelser = listOf(),
            hjemler = listOf("hb1", "hb2"),
            enheter = listOf(),
            sections = listOf(),
            templates = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = textRepository.searchTexts(
            textType = null,
            requiredSection = null,
            utfall = listOf("ub1", "ub2"),
            ytelser = listOf("yb1", "yb2"),
            hjemler = listOf("hb1", "hb2"),
            enheter = listOf("eb1", "eb2"),
            sections = listOf("sb1", "sb2"),
            templates = listOf("tb1", "tb2"),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2)

        foundTexts = textRepository.searchTexts(
            textType = "type",
            requiredSection = null,
            utfall = listOf("ua"),
            ytelser = listOf("ya"),
            hjemler = listOf("ha"),
            enheter = listOf("ea"),
            sections = listOf("sa"),
            templates = listOf("ta"),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3, text4)

        foundTexts = textRepository.searchTexts(
            textType = "type",
            requiredSection = null,
            utfall = listOf("ua"),
            ytelser = listOf("ya"),
            hjemler = listOf("hb2"),
            enheter = listOf("ea"),
            sections = listOf("sa"),
            templates = listOf("ta"),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text2)
    }

    @Test
    fun `texts with no specifications are also returned`() {
        val now = LocalDateTime.now()

        val text1 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            hjemler = setOf(),
            ytelser = setOf(),
            utfall = setOf(),
            enheter = setOf(),
            sections = setOf(),
            templates = setOf(),
            created = now,
            modified = now,
        )

        val text2 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            hjemler = setOf("ha", "hb2"),
            ytelser = setOf(),
            utfall = setOf(),
            enheter = setOf(),
            sections = setOf(),
            templates = setOf(),
            created = now,
            modified = now,
        )

        val text3 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            hjemler = setOf(),
            ytelser = setOf(),
            utfall = setOf(),
            enheter = setOf(),
            sections = setOf("sa", "sb2"),
            templates = setOf(),
            created = now,
            modified = now,
        )

        val text4 = Text(
            title = "title",
            textType = "type",
            smartEditorVersion = 1,
            content = "{}",
            plainText = null,
            hjemler = setOf(),
            ytelser = setOf(),
            utfall = setOf(),
            enheter = setOf(),
            sections = setOf("s10"),
            templates = setOf(),
            created = now,
            modified = now,
        )

        textRepository.save(text1)
        textRepository.save(text2)
        textRepository.save(text3)
        textRepository.save(text4)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundTexts = textRepository.searchTexts(
            textType = "type",
            requiredSection = null,
            utfall = listOf(),
            ytelser = listOf(),
            hjemler = listOf("ha"),
            enheter = listOf(),
            sections = listOf("sa"),
            templates = listOf(),
        )
        assertThat(foundTexts).containsExactlyInAnyOrder(text1, text2, text3)
    }

}
