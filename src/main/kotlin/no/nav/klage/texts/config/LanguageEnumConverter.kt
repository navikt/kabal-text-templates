package no.nav.klage.texts.config

import no.nav.klage.texts.api.views.Language
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class Config : WebMvcConfigurer {
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(LanguageEnumConverter())
    }
}

class LanguageEnumConverter : Converter<String, Language> {
    override fun convert(source: String): Language {
        return Language.valueOf(source.uppercase())
    }
}