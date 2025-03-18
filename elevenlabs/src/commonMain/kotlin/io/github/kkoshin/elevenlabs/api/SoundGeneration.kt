package io.github.kkoshin.elevenlabs.api

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.model.SoundGenerationRequest
import io.ktor.resources.Resource
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.readBytes
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
    okioBufferedSink.use {
        while (!isClosedForRead) {
            val packet = readRemaining(bufferSize.toLong()) // 分块读取数据
            val bytes = packet.readBytes()                  // 转换为 ByteArray
            if (bytes.isNotEmpty()) {
                okioBufferedSink.write(bytes)                // 写入 Okio Sink
            }
            packet.release()                                 // 释放 Ktor 内存
        }
        okioBufferedSink.flush()                             // 确保所有数据写入
    }
}