package com.arslan.ndna.model

enum class Lang(val label: String, val query: String) {
    KOTLIN("Kotlin", "Kotlin"),
    JAVA("Java", "Java"),
    DART("Dart", "Dart"),
    CPP("C++", "C++")
}

enum class Triple3(val label: String) {
    ANY("Any"),
    YES("Yes"),
    NO("No")
}

enum class Recency(val label: String, val minutes: Long?) {
    H1("1 hour", 60),
    H2("2 hours", 120),
    H3("3 hours", 180),
    H6("6 hours", 360),
    H12("12 hours", 720),
    H24("24 hours", 1440),
    W1("1 week", 10080),
    MO1("1 month", 43200),
    ANY("Any", null)
}

data class Filters(
    val minStars: Int = 5,
    val maxStars: Int = 1000,
    val langs: Set<Lang> = setOf(Lang.KOTLIN),
    val recency: Recency = Recency.H24,
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
    val matches: List<String>,
    val updatedAt: String = ""
)

data class BlockedApp(
    val id: String,
    val name: String
)

/** README preview shown over the results list. */
data class Preview(
    val item: AppItem,
    val loading: Boolean = true,
    val readme: String = "",
    val error: String? = null
)

data class SearchState(
    val loading: Boolean = false,
    val items: List<AppItem> = emptyList(),
    val error: String? = null,
    val page: Int = 1,
    val canLoadMore: Boolean = false
)
