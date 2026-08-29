package com.arslan.ndna.data

import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.Triple3

object Matcher {

    fun chips(f: Filters, name: String, description: String, language: String?): List<String> {
        val haystack = "$name $description".lowercase()
        return (keywordChips(f, haystack) + filterChips(f, haystack, language)).distinct()
    }

    private fun keywordChips(f: Filters, haystack: String): List<String> =
        f.keywords.trim().lowercase().split(" ")
            .filter { it.isNotBlank() && haystack.contains(it) }

    private fun filterChips(f: Filters, haystack: String, language: String?): List<String> =
        langChips(f, language) + listOfNotNull(
            "shizuku".takeIf { f.shizuku == Triple3.YES || haystack.contains("shizuku") },
            f.recency.label.takeIf { f.recency.minutes != null }
        )

    /** Only the selected langs the repo actually uses, per GitHub's own `language` field. */
    private fun langChips(f: Filters, language: String?): List<String> {
        if (language.isNullOrBlank()) return emptyList()
        return f.langs
            .filter { it.label.equals(language, true) || it.query.equals(language, true) }
            .map { it.label }
    }
}
