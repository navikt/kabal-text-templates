package no.nav.klage.texts.repositories

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import no.nav.klage.texts.domain.Text
import org.springframework.stereotype.Repository

@Repository
class SearchTextRepositoryCustomImpl : SearchTextRepositoryCustom {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    override fun searchTexts(
        textType: String?,
        requiredSection: String?,
        utfall: List<String>,
        ytelser: List<String>,
        hjemler: List<String>,
        enheter: List<String>,
        sections: List<String>,
        templates: List<String>,
        templateSectionList: List<String>,
        ytelseHjemmelList: List<String>,
    ): List<Text> {
        val conditions = mutableListOf<String>()
        val joins = mutableListOf<String>()

        if (textType != null) {
            conditions += "t.textType = :textType"
        }
        if (requiredSection != null) {
            conditions += "s in :sections"
            joins += "join t.sections s"
        }

        if (utfall.isNotEmpty()) {
            var c = "(u in :utfall"
            if ("NONE" in utfall) {
                c += " or u is null"
            }
            c += ")"
            conditions += c
            joins += "left join t.utfall u"
        }
        if (ytelser.isNotEmpty()) {
            var c = "(y in :ytelser"
            if ("NONE" in ytelser) {
                c += " or y is null"
            }
            c += ")"
            conditions += c
            joins += "left join t.ytelser y"
        }
        if (hjemler.isNotEmpty()) {
            var c = "(h in :hjemler"
            if ("NONE" in hjemler) {
                c += " or h is null"
            }
            c += ")"
            conditions += c
            joins += "left join t.hjemler h"
        }
        if (enheter.isNotEmpty()) {
            var c = "(e in :enheter"
            if ("NONE" in enheter) {
                c += " or e is null"
            }
            c += ")"
            conditions += c
            joins += "left join t.enheter e"
        }
        if (sections.isNotEmpty()) {
            var c = "(s in :sections"
            if ("NONE" in sections) {
                c += " or s is null"
            }
            c += ")"
            conditions += c
            joins += "left join t.sections s"
        }
        if (templates.isNotEmpty()) {
            var c = "(ts in :templates"
            if ("NONE" in templates) {
                c += " or ts is null"
            }
            c += ")"
            conditions += c
            joins += "left join t.templates ts"
        }
        if (templateSectionList.isNotEmpty()) {
            val c = "(tsl in :templateSectionList)"
            conditions += c
            joins += "left join t.templateSectionList tsl"
        }
        if (ytelseHjemmelList.isNotEmpty()) {
            val c = "(yhl in :ytelseHjemmelList)"
            conditions += c
            joins += "left join t.ytelseHjemmelList yhl"
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
        if (requiredSection != null) {
            query = query.setParameter("sections", listOf(requiredSection))
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
        if (templateSectionList.isNotEmpty()) {
            query = query.setParameter("templateSectionList", templateSectionList)
        }
        if (ytelseHjemmelList.isNotEmpty()) {
            query = query.setParameter("ytelseHjemmelList", ytelseHjemmelList)
        }

        return query.resultList
    }

}