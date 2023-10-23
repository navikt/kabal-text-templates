package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TextRepository : JpaRepository<Text, UUID>