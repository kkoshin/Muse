package io.github.kkoshin.muse.tts.vendor

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.api.getSubscription
import io.github.kkoshin.elevenlabs.api.textToSpeech
import io.github.kkoshin.elevenlabs.model.FreeTierOutputFormat
import io.github.kkoshin.elevenlabs.model.ModelId
import io.github.kkoshin.elevenlabs.model.TextToSpeechRequest
import io.github.kkoshin.muse.audio.MonoAudioSampleMetadata
import io.github.kkoshin.muse.tts.CharacterQuota
import io.github.kkoshin.muse.tts.SupportedAudioType
import io.github.kkoshin.muse.tts.TTSProvider
import io.github.kkoshin.muse.tts.TTSResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElevenLabTTSProvider : TTSProvider {
    // Old Male with British accent
    private val presetBrian = "nPczCjzI2devNBz1zQrb"

    private val preferredVoiceId = presetBrian

    // 备用：7904879831bf1d4fd56f4f6baee9167b
    private val client = ElevenLabsClient("d41ee34b857479772db5ce143549bcd9")

    override suspend fun queryQuota(): Result<CharacterQuota> =
        withContext(Dispatchers.IO) {
            client.getSubscription().map {
                CharacterQuota(
                    consumed = it.characterCount,
                    total = it.characterLimit,
                )
            }
        }

    override suspend fun generate(text: String): Result<TTSResult> {
        check(text.isNotBlank()) {
            "text must not be blank."
        }
        return withContext(Dispatchers.IO) {
            val request = TextToSpeechRequest(
                text = text,
                modelId = ModelId.EnglishTurbo,
            )
            client
                .textToSpeech(
                    voiceId = preferredVoiceId,
                    textToSpeechRequest = request,
                    outputFormat = FreeTierOutputFormat.Mp3_44100_128,
                    optimizeStreamingLatency = null,
                ).map {
                    TTSResult(it, SupportedAudioType.MP3, MonoAudioSampleMetadata())
                }
        }
    }
}