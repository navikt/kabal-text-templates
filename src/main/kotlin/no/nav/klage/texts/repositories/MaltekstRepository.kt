package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.MaltekstseksjonVersion
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MaltekstRepository : JpaRepository<MaltekstseksjonVersion, UUID> {

    @EntityGraph(attributePaths = [
        "texts",
        "utfallIdList",
        "enhetIdList",
        "templateSectionIdList",
        "ytelseHjemmelIdList",
    ])
    override fun findAll(): MutableList<MaltekstseksjonVersion>

}