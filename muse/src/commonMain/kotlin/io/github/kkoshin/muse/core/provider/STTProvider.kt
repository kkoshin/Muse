package io.github.kkoshin.muse.core.provider

import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import okio.Source

interface STTProvider {
    suspend fun transcribeAudio(audio: Source, audioName: String): Result<SpeechToTextChunkResponseModel>
}