package no.nav.klage.texts.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

fun getSecureLogger(): Logger = LoggerFactory.getLogger("secure")

fun logTextMethodDetails(methodName: String, innloggetIdent: String, textId: UUID?, logger: Logger) {
    logger.debug(
        "{} is requested by ident {} for textId {}",
        methodName,
        innloggetIdent,
        textId ?: "null",
    )
}