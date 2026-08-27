package com.arslan.ndna.data.fdroid

import android.content.Context
import android.util.JsonReader
import com.arslan.ndna.data.Http
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.IOException

class FdroidSync(context: Context) {

    private val prefs = context.getSharedPreferences("ndna_fdroid", Context.MODE_PRIVATE)
    val cacheFile = File(context.filesDir, "fdroid.jsonl")

    fun lastSync(): Long = prefs.getLong("last_sync", 0L)

    fun sync(): String {
        if (recentlySynced()) return "Catalog is up to date"
        Http.client.newCall(request()).execute().use { response ->
            if (response.code == 304) return finish(response.header("ETag"), "Catalog unchanged")
            if (!response.isSuccessful) throw IOException("F-Droid error ${response.code}")
            val count = writeCache(response.body?.charStream() ?: throw IOException("Empty body"))
            return finish(response.header("ETag"), "Synced $count apps")
        }
    }

    private fun recentlySynced(): Boolean =
        cacheFile.exists() && System.currentTimeMillis() - lastSync() < DAY_MS

    private fun request(): Request {
        val builder = Request.Builder().url(INDEX_URL).header("User-Agent", "NDNA/1.0")
        val etag = prefs.getString("etag", null)
        if (etag != null && cacheFile.exists()) builder.header("If-None-Match", etag)
        return builder.build()
    }

    private fun finish(etag: String?, message: String): String {
        prefs.edit().putLong("last_sync", System.currentTimeMillis()).putString("etag", etag).apply()
        return message
    }

    private fun writeCache(source: java.io.Reader): Int {
        val reader = JsonReader(source)
        cacheFile.bufferedWriter().use { writer ->
            reader.beginObject()
            return readRoot(reader, writer)
        }
    }

    private fun readRoot(reader: JsonReader, writer: java.io.Writer): Int {
        var count = 0
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "apps") count = readApps(reader, writer) else reader.skipValue()
        }
        return count
    }

    private fun readApps(reader: JsonReader, writer: java.io.Writer): Int {
        var count = 0
        reader.beginArray()
        while (reader.hasNext()) {
            writer.append(readApp(reader).toString()).append('\n')
            count++
        }
        reader.endArray()
        return count
    }

    private fun readApp(reader: JsonReader): JSONObject {
        val app = JSONObject()
        reader.beginObject()
        while (reader.hasNext()) readField(reader, app)
        reader.endObject()
        return app
    }

    private fun readField(reader: JsonReader, app: JSONObject) {
        val key = reader.nextName()
        if (key !in KEYS) return reader.skipValue()
        app.put(key, if (key == "lastUpdated") reader.nextLong() else scalar(reader))
    }

    private fun scalar(reader: JsonReader): String {
        if (reader.peek() == android.util.JsonToken.STRING) return reader.nextString()
        reader.skipValue()
        return ""
    }

    private companion object {
        const val INDEX_URL = "https://f-droid.org/repo/index-v1.json"
        const val DAY_MS = 24 * 60 * 60 * 1000L
        val KEYS = setOf("packageName", "name", "summary", "icon", "lastUpdated")
    }
}
