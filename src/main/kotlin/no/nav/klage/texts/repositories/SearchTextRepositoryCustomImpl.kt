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
        textType: String?,
        utfall: List<String>,
        ytelser: List<String>,
        hjemler: List<String>,
        enheter: List<String>,
        sections: List<String>,
        templates: List<String>,
    ): List<Text> {
        val conditions = mutableListOf<String>()
        val joins = mutableListOf<String>()

        if (textType != null) {
            conditions += "t.textType = :textType"
        }
        if (utfall.isNotEmpty()) {
            conditions += "(u in :utfall or u is null)"
            joins += "left join t.utfall u"
        }
        if (ytelser.isNotEmpty()) {
            conditions += "(y in :ytelser or y is null)"
            joins += "left join t.ytelser y"
        }
        if (hjemler.isNotEmpty()) {
            conditions += "(h in :hjemler or h is null)"
            joins += "left join t.hjemler h"
        }
        if (enheter.isNotEmpty()) {
            conditions += "(e in :enheter or e is null)"
            joins += "left join t.enheter e"
        }
        if (sections.isNotEmpty()) {
            conditions += "(s in :sections or s is null)"
            joins += "left join t.sections s"
        }
        if (templates.isNotEmpty()) {
            conditions += "(ts in :templates or ts is null)"
            joins += "left join t.templates ts"
        }

        val innerJoins = joins.joinToString(separator = " ")

        var innerQuery = conditions.joinToString(separator = " AND ")

        if (innerQuery.isNotEmpty()) {
            innerQuery = "WHERE $innerQuery"
        }

        val selectQuery = """
            SELECT DISTINCT t FROM Text t $innerJoins        
                $innerQuery 
                ORDER BY t.created
        """

        var query = entityManager.createQuery(selectQuery, Text::class.java)

        if (textType != null) {
            query = query.setParameter("textType", textType)
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
        if (templates.isNotEmpty()) {
            query = query.setParameter("templates", templates)
        }

        return query.resultList
    }

}