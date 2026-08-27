package com.arslan.ndna.data

import com.arslan.ndna.model.AppItem
import com.arslan.ndna.model.Filters
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class GithubRepo(private val tokenStore: TokenStore) {

    private val cache = mutableMapOf<String, List<AppItem>>()

    fun search(filters: Filters, page: Int): List<AppItem> {
        val query = QueryBuilder.build(filters)
        val key = "$query#$page"
        cache[key]?.let { return it }
        guardRateLimit()
        val items = fetch(query, page, filters)
        cache[key] = items
        return items
    }

    private fun guardRateLimit() {
        val wait = RateLimiter.waitSeconds()
        if (wait > 0) throw IOException("Rate limited. Retry in ${wait}s")
    }

    private fun fetch(query: String, page: Int, filters: Filters): List<AppItem> {
        Http.client.newCall(request(query, page)).execute().use { response ->
            RateLimiter.record(response)
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw IOException(errorOf(response.code, body))
            return parse(body, filters)
        }
    }

    private fun errorOf(code: Int, body: String): String {
        if (code == 403 || code == 429) return "Rate limited. Retry in ${RateLimiter.waitSeconds()}s"
        return "GitHub error $code: ${body.take(120)}"
    }

    private fun request(query: String, page: Int): Request {
        val url = "https://api.github.com/search/repositories" +
            "?q=${java.net.URLEncoder.encode(query, "UTF-8")}&sort=updated&per_page=30&page=$page"
        val builder = Request.Builder().url(url)
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("User-Agent", "NDNA/1.0")
        val token = tokenStore.get()
        if (token.isNotBlank()) builder.header("Authorization", "Bearer $token")
        return builder.build()
    }

    private fun parse(body: String, filters: Filters): List<AppItem> {
        val array = JSONObject(body).optJSONArray("items") ?: JSONArray()
        return (0 until array.length()).map { toItem(array.getJSONObject(it), filters) }
    }

    private fun toItem(json: JSONObject, filters: Filters): AppItem {
        val name = json.optString("full_name")
        val description = json.optString("description").ifBlank { "No description" }
        val stars = json.optInt("stargazers_count")
        return AppItem(
            id = "gh-${json.optLong("id")}",
            name = name,
            description = description,
            iconUrl = json.optJSONObject("owner")?.optString("avatar_url"),
            url = json.optString("html_url"),
            stars = stars,
            source = "GitHub",
            matches = Matcher.chips(filters, name, description, stars)
        )
    }
}
