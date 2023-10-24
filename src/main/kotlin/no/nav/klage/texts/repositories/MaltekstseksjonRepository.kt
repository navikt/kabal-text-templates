package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Maltekstseksjon
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MaltekstseksjonRepository : JpaRepository<Maltekstseksjon, UUID>