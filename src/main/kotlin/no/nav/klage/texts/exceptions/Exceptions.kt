package no.nav.klage.texts.exceptions

class TextNotFoundException(msg: String) : RuntimeException(msg)

class MaltekstNotFoundException(msg: String) : RuntimeException(msg)

class ClientErrorException(msg: String) : RuntimeException(msg)