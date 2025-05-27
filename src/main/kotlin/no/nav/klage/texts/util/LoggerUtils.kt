package no.nav.klage.texts.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

fun logMethodDetails(methodName: String, innloggetIdent: String, id: UUID?, logger: Logger) {
    logger.debug(
        "{} is requested by ident {} for id {}",
        methodName,
        innloggetIdent,
        id,
    )
}