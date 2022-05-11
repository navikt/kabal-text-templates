package no.nav.klage.texts.repositories

import no.nav.klage.texts.domain.Text
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
class SearchTextRepositoryCustomImpl : SearchTextRepositoryCustom {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    override fun searchTexts(
        type: String?,
        utfall: List<String>,
        ytelser: List<String>,
        hjemler: List<String>,
        enheter: List<String>,
        sections: List<String>,
    ): List<Text> {
        val conditions = mutableListOf<String>()

        if (type != null) {
            conditions += "t.type = :type"
        }
        if (utfall.isNotEmpty()) {
            conditions += "u in :utfall"
        }
        if (ytelser.isNotEmpty()) {
            conditions += "y in :ytelser"
        }
        if (hjemler.isNotEmpty()) {
            conditions += "h in :hjemler"
        }
        if (enheter.isNotEmpty()) {
            conditions += "e in :enheter"
        }
        if (sections.isNotEmpty()) {
            conditions += "s in :sections"
        }

        var innerQuery = conditions.joinToString(separator = " AND ")

        if (innerQuery.isNotEmpty()) {
            innerQuery = "WHERE $innerQuery"
        }

        val selectQuery = """
            SELECT DISTINCT t FROM Text t join t.hjemler h join t.utfall u join t.enheter e join t.ytelser y join t.sections s            
                $innerQuery 
                ORDER BY t.created
        """

        var query = entityManager.createQuery(selectQuery, Text::class.java)

        if (type != null) {
            query = query.setParameter("type", type)
        }
        if (hjemler.isNotEmpty()) {
            query = query.setParameter("hjemler", hjemler)
        }
        if (ytelser.isNotEmpty()) {
            query = query.setParameter("ytelser", ytelser)
        }
        if (utfall.isNotEmpty()) {
            query = query.setParameter("utfall", utfall)
        }
        if (enheter.isNotEmpty()) {
            query = query.setParameter("enheter", enheter)
        }
        if (sections.isNotEmpty()) {
            query = query.setParameter("sections", sections)
        }

        return query.resultList
    }

}