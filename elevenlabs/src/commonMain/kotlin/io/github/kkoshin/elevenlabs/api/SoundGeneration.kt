package io.github.kkoshin.elevenlabs.api

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.model.SoundGenerationRequest
import io.ktor.resources.Resource
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import java.io.OutputStream

@Resource("/sound-generation")
class SoundGeneration

suspend fun ElevenLabsClient.makeSoundEffects(
    prompt: String,
    durationSeconds: Double?,
    promptInfluence: Float,
    outputStream: OutputStream
): Result<Unit> =
    post<SoundGenerationRequest, SoundGeneration, ByteReadChannel>(
        SoundGeneration(), data = SoundGenerationRequest(
            text = prompt,
            durationSeconds = durationSeconds,
            promptInfluence = promptInfluence,
        )
    ).mapCatching {
        it.copyTo(outputStream)
    }
