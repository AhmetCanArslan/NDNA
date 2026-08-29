package com.arslan.ndna.data

import android.content.Context
import com.arslan.ndna.model.BlockedApp

/** Blocked apps, kept as "id|name" entries so settings can show real names. */
class BlockStore(context: Context) {

    private val prefs = context.getSharedPreferences("ndna_blocked", Context.MODE_PRIVATE)

    fun load(): List<BlockedApp> = prefs.getStringSet(KEY, emptySet()).orEmpty()
        .map { entry -> BlockedApp(entry.substringBefore('|'), entry.substringAfter('|', "")) }
        .map { if (it.name.isEmpty()) it.copy(name = it.id) else it }
        .sortedBy { it.name.lowercase() }

    fun save(blocked: List<BlockedApp>) = prefs.edit()
        .putStringSet(KEY, blocked.map { "${it.id}|${it.name}" }.toSet())
        .apply()

    private companion object {
        const val KEY = "blocked"
    }
}
