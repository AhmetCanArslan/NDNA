package com.arslan.ndna.model

enum class Lang(val label: String, val query: String?) {
    ANY("Any", null),
    KOTLIN("Kotlin", "Kotlin"),
    JAVA("Java", "Java")
}

enum class Triple3(val label: String) {
    ANY("Any"),
    YES("Yes"),
    NO("No")
}

enum class Recency(val label: String, val minutes: Long?) {
    ANY("Any", null),
    M10("10 min", 10),
    H1("1 hour", 60),
    D1("24 hours", 1440),
    D7("7 days", 10080)
}

data class Filters(
    val minStars: Int = 5,
    val maxStars: Int = 1000,
    val lang: Lang = Lang.KOTLIN,
    val recency: Recency = Recency.D1,
    val shizuku: Triple3 = Triple3.ANY,
    val keywords: String = ""
)

data class AppItem(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String?,
    val url: String,
    val stars: Int?,
    val source: String,
    val matches: List<String>
)

data class SearchState(
    val loading: Boolean = false,
    val items: List<AppItem> = emptyList(),
    val error: String? = null,
    val page: Int = 1,
    val canLoadMore: Boolean = false
)
