package com.arslan.ndna.data

import android.content.Context
import com.arslan.ndna.model.Filters
import com.arslan.ndna.model.Lang
import com.arslan.ndna.model.Recency
import com.arslan.ndna.model.Triple3

class FiltersStore(context: Context) {

    private val prefs = context.getSharedPreferences("ndna_filters", Context.MODE_PRIVATE)

    fun load(): Filters = runCatching {
        val d = Filters()
        Filters(
            minStars = prefs.getInt("minStars", d.minStars),
            maxStars = prefs.getInt("maxStars", d.maxStars),
            langs = prefs.getStringSet("langs", null)?.map { Lang.valueOf(it) }?.toSet() ?: d.langs,
            recency = Recency.valueOf(prefs.getString("recency", d.recency.name)!!),
            shizuku = Triple3.valueOf(prefs.getString("shizuku", d.shizuku.name)!!),
            keywords = prefs.getString("keywords", d.keywords).orEmpty()
        )
    }.getOrDefault(Filters())

    fun save(f: Filters) = prefs.edit()
        .putInt("minStars", f.minStars)
        .putInt("maxStars", f.maxStars)
        .putStringSet("langs", f.langs.map { it.name }.toSet())
        .putString("recency", f.recency.name)
        .putString("shizuku", f.shizuku.name)
        .putString("keywords", f.keywords)
        .apply()
}
