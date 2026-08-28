package com.arslan.ndna.data

import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.Lang
import com.arslan.ndna.model.Triple3
import java.time.Instant
import java.time.temporal.ChronoUnit

object QueryBuilder {

    /** Restricts results to Android app repos. */
    private const val ANDROID_CLAUSE = "android in:name,description,topics"

    fun buildAll(f: Filters): List<String> =
        if (f.langs.isEmpty()) listOf(build(f, null))
        else f.langs.map { build(f, it) }

    fun build(f: Filters, lang: Lang?): String = listOfNotNull(
        f.keywords.trim().ifBlank { null },
        shizukuClause(f.shizuku),
        ANDROID_CLAUSE,
        lang?.let { "language:${it.query}" },
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
