package com.arslan.ndna.data

import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.Triple3
import java.time.Instant
import java.time.temporal.ChronoUnit

object QueryBuilder {

    fun build(f: Filters): String = listOfNotNull(
        f.keywords.trim().ifBlank { null },
        shizukuClause(f.shizuku),
        f.lang.query?.let { "language:$it" },
        "stars:${f.minStars}..${f.maxStars}",
        pushedClause(f),
        "fork:false"
    ).joinToString(" ")

    private fun shizukuClause(v: Triple3): String? = when (v) {
        Triple3.YES -> "shizuku in:name,description,topics"
        Triple3.NO -> "NOT shizuku in:name,description,topics"
        Triple3.ANY -> null
    }

    private fun pushedClause(f: Filters): String? {
        val minutes = f.recency.minutes ?: return null
        val since = Instant.now().minus(minutes, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.SECONDS)
        return "pushed:>$since"
    }
}
