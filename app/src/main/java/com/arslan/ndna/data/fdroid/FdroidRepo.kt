package com.arslan.ndna.data.fdroid

import com.arslan.ndna.data.Matcher
import com.arslan.ndna.model.AppItem
import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.Triple3
import org.json.JSONObject
import java.io.IOException

class FdroidRepo(private val sync: FdroidSync) {

    fun search(filters: Filters, limit: Int = 60): List<AppItem> {
        if (!sync.cacheFile.exists()) throw IOException("Sync the F-Droid catalog in Settings first")
        return sync.cacheFile.useLines { lines ->
            lines.map { JSONObject(it) }
                .filter { matches(it, filters) }
                .take(limit)
                .map { toItem(it, filters) }
                .toList()
        }
    }

    private fun matches(app: JSONObject, filters: Filters): Boolean =
        matchesKeywords(app, filters) && matchesShizuku(app, filters) && matchesRecency(app, filters)

    private fun haystack(app: JSONObject): String =
        "${app.optString("name")} ${app.optString("summary")} ${app.optString("packageName")}".lowercase()

    private fun matchesKeywords(app: JSONObject, filters: Filters): Boolean {
        val words = filters.keywords.trim().lowercase().split(" ").filter { it.isNotBlank() }
        if (words.isEmpty()) return true
        return words.all { haystack(app).contains(it) }
    }

    private fun matchesShizuku(app: JSONObject, filters: Filters): Boolean {
        val has = haystack(app).contains("shizuku")
        if (filters.shizuku == Triple3.YES) return has
        if (filters.shizuku == Triple3.NO) return !has
        return true
    }

    private fun matchesRecency(app: JSONObject, filters: Filters): Boolean {
        val minutes = filters.recency.minutes ?: return true
        val updated = app.optLong("lastUpdated", 0L)
        return updated >= System.currentTimeMillis() - minutes * 60_000
    }

    private fun toItem(app: JSONObject, filters: Filters): AppItem {
        val name = app.optString("name")
        val summary = app.optString("summary").ifBlank { "No description" }
        val icon = app.optString("icon")
        return AppItem(
            id = "fd-${app.optString("packageName")}",
            name = name,
            description = summary,
            iconUrl = if (icon.isBlank()) null else "https://f-droid.org/repo/icons-640/$icon",
            url = "https://f-droid.org/packages/${app.optString("packageName")}",
            stars = null,
            source = "F-Droid",
            matches = Matcher.chips(filters, name, summary, null)
        )
    }
}
