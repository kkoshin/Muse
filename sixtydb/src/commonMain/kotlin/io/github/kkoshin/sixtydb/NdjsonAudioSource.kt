package io.github.kkoshin.sixtydb

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.Buffer
import okio.Source
import okio.Timeout
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Adapts the NDJSON `POST /tts-stream` response into an `okio.Source` of
 * decoded audio bytes so it can feed straight into the existing audio
 * pipeline (`SpeechProcessorManager.getOrGenerate` -> file). Mirrors the role
 * of `StreamingByteReadChannelSource` from `:elevenlabs`.
 *
 * Wire format per docs.60db.ai:
 *   line 1   { "type": "audio", "audioContent": "<base64 mp3 chunk>" }
 *   ...
 *   line N   { "type": "complete" }
 *   on error { "type": "error", "message": "..." }
 */
@OptIn(ExperimentalEncodingApi::class)
class NdjsonAudioSource(
    private val channel: ByteReadChannel,
) : Source {
    private val closed = atomic(false)
    private val pending = Buffer()
    private val finished = atomic(false)

    override fun read(sink: Buffer, byteCount: Long): Long {
        check(!closed.value) { "NdjsonAudioSource is closed" }
        require(byteCount >= 0) { "byteCount < 0: $byteCount" }

        if (pending.size == 0L) {
            if (finished.value) return -1L
            fillPending()
            if (pending.size == 0L) return -1L
        }

        val toRead = minOf(byteCount, pending.size)
        sink.write(pending, toRead)
        return toRead
    }

    /** Block until the next NDJSON line yields decoded bytes or we hit EOF. */
    private fun fillPending() = runBlocking(Dispatchers.IO) {
        while (pending.size == 0L && !finished.value) {
            val line = channel.readUTF8Line() ?: run {
                finished.value = true
                return@runBlocking
            }
            if (line.isBlank()) continue

            val json = runCatching { Json.parseToJsonElement(line).jsonObject }
                .getOrNull() ?: continue
            when (json["type"]?.jsonPrimitive?.contentOrNull()) {
                "complete" -> {
                    finished.value = true
                    return@runBlocking
                }
                "error" -> {
                    finished.value = true
                    val msg = json["message"]?.jsonPrimitive?.contentOrNull() ?: "stream error"
                    throw IllegalStateException("60db /tts-stream error: $msg")
                }
                else -> {
                    val b64 = json["audioContent"]?.jsonPrimitive?.contentOrNull() ?: continue
                    pending.write(Base64.decode(b64))
                }
            }
        }
    }

    override fun timeout(): Timeout = Timeout.NONE

    override fun close() {
        if (closed.compareAndSet(false, true)) {
            channel.cancel(null)
        }
    }
}

private fun kotlinx.serialization.json.JsonPrimitive.contentOrNull(): String? =
    runCatching { content }.getOrNull()
