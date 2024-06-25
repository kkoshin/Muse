package io.github.kkoshin.elevenlabs.error

open class ElevenLabsError(
    val statusCode: Int,
    message: String,
) : Throwable(message, null)