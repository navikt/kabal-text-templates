package no.nav.klage.texts.exceptions

class TextNotFoundException(msg: String) : RuntimeException(msg)

class LanguageNotFoundException(msg: String) : RuntimeException(msg)

class MaltekstseksjonNotFoundException(msg: String) : RuntimeException(msg)

class ClientErrorException(msg: String) : RuntimeException(msg)