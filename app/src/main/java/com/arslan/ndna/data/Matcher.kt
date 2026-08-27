package com.arslan.ndna.data

import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.Triple3

object Matcher {

    fun chips(f: Filters, name: String, description: String, stars: Int?): List<String> {
        val haystack = "$name $description".lowercase()
        return (keywordChips(f, haystack) + filterChips(f, haystack, stars)).distinct()
    }

    private fun keywordChips(f: Filters, haystack: String): List<String> =
        f.keywords.trim().lowercase().split(" ")
            .filter { it.isNotBlank() && haystack.contains(it) }

    private fun filterChips(f: Filters, haystack: String, stars: Int?): List<String> = listOfNotNull(
        f.lang.query,
        stars?.let { "$it stars" },
        "shizuku".takeIf { f.shizuku == Triple3.YES || haystack.contains("shizuku") },
        f.recency.label.takeIf { f.recency.minutes != null }
    )
}
