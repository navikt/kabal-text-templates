package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Maltekst
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MaltekstRepository : JpaRepository<Maltekst, UUID> {

    @EntityGraph(attributePaths = [
        "texts",
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    override fun findAll(): MutableList<Maltekst>

}