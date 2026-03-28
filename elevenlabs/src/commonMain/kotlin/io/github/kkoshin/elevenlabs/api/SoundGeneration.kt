package io.github.kkoshin.elevenlabs.api

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.model.SoundGenerationRequest
import io.ktor.resources.Resource
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import okio.Sink
import okio.buffer
import okio.use

@Resource("/sound-generation")
class SoundGeneration

suspend fun ElevenLabsClient.makeSoundEffects(
    prompt: String,
    durationSeconds: Double?,
    promptInfluence: Float,
    sink: Sink
): Result<Unit> =
    post<SoundGenerationRequest, SoundGeneration, ByteReadChannel>(
        SoundGeneration(), data = SoundGenerationRequest(
            text = prompt,
            durationSeconds = durationSeconds,
            promptInfluence = promptInfluence,
        )
    ).mapCatching {
        it.writeToSink(sink)
    }


suspend fun ByteReadChannel.writeToSink(sink: Sink, bufferSize: Int = 8192) {
    val okioBufferedSink = sink.buffer() // 使用 Okio 缓冲提升性能
    val buffer = ByteArray(bufferSize)
    okioBufferedSink.use {
        while (!isClosedForRead) {
            val bytesRead = readAvailable(buffer, 0, bufferSize)
            if (bytesRead > 0) {
                okioBufferedSink.write(buffer, 0, bytesRead)
            } else if (bytesRead == -1) {
                break
            }
        }
        okioBufferedSink.flush()                             // 确保所有数据写入
    }
}