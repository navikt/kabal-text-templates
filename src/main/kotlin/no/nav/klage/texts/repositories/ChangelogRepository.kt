package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.ChangelogEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChangelogRepository : JpaRepository<ChangelogEntry, UUID>