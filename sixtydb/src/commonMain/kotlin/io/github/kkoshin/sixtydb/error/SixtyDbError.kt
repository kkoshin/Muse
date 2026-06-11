package io.github.kkoshin.sixtydb.error

/**
 * Thrown when the 60db API returns a non-2xx response. Mirrors the role of
 * `ElevenLabsError` so the application's error handling code path is uniform
 * across providers.
 */
class SixtyDbError(
    val httpStatus: Int,
    override val message: String,
) : RuntimeException(message)
