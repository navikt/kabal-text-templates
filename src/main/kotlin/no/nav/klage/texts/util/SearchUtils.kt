package no.nav.klage.texts.util

fun testSets(queryValues: List<String>, dbValues: Set<String>): Boolean {
    if (queryValues.contains("NONE") && queryValues.size == 1 && dbValues.isEmpty()) {
        return true
    }

    if (queryValues.isEmpty() || dbValues.isEmpty()) {
        return true
    }

    val queryValueSets = queryValues.map { valueString ->
        valueString.split(":").toSet()
    }

    val dbValueSets = dbValues.map { dbString ->
        dbString.split(":").toSet()
    }

    return dbValueSets.any { dbValueSet ->
        queryValueSets.any { queryValueSet ->
            queryValueSet.containsAll(dbValueSet)
        }
    }
}

fun testCompositeValues(queryValues: List<String>, dbValues: Set<String>): Boolean {
    if (queryValues.contains("NONE") && queryValues.size == 1 && dbValues.isEmpty()) {
        return true
    }

    if (queryValues.isEmpty() || dbValues.isEmpty()) {
        return true
    }

    for (query in queryValues) {
        for (dbValue in dbValues) {
            val regex = Regex(query.replace("*", ".*"))
            if (dbValue.matches(regex)) {
                return true
            }
        }
    }

    return false
}