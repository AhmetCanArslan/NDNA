package com.arslan.ndna.data

import okhttp3.Response

object RateLimiter {

    private var blockedUntilMs = 0L

    fun waitSeconds(): Long {
        val left = blockedUntilMs - System.currentTimeMillis()
        return if (left > 0) left / 1000 + 1 else 0
    }

    fun record(response: Response) {
        val remaining = response.header("X-RateLimit-Remaining")?.toLongOrNull()
        if (remaining != null && remaining > 0L) return
        blockedUntilMs = resetMs(response)
    }

    fun blockFor(seconds: Long) {
        blockedUntilMs = System.currentTimeMillis() + seconds * 1000
    }

    private fun resetMs(response: Response): Long {
        val retryAfter = response.header("Retry-After")?.toLongOrNull()
        if (retryAfter != null) return System.currentTimeMillis() + retryAfter * 1000
        val reset = response.header("X-RateLimit-Reset")?.toLongOrNull() ?: return 0L
        return reset * 1000
    }
}
