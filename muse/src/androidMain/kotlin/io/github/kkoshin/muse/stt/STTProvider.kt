package io.github.kkoshin.muse.stt

import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import okio.Source

interface STTProvider {
    suspend fun transcribeAudio(audio: Source, audioName: String): Result<SpeechToTextChunkResponseModel>
}