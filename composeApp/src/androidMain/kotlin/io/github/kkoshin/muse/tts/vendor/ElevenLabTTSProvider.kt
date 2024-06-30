package io.github.kkoshin.muse.tts.vendor

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.api.getSubscription
import io.github.kkoshin.elevenlabs.api.getVoices
import io.github.kkoshin.elevenlabs.api.textToSpeech
import io.github.kkoshin.elevenlabs.model.FreeTierOutputFormat
import io.github.kkoshin.elevenlabs.model.ModelId
import io.github.kkoshin.elevenlabs.model.TextToSpeechRequest
import io.github.kkoshin.muse.audio.MonoAudioSampleMetadata
import io.github.kkoshin.muse.tts.CharacterQuota
import io.github.kkoshin.muse.tts.SupportedAudioType
import io.github.kkoshin.muse.tts.TTSProvider
import io.github.kkoshin.muse.tts.TTSResult
import io.github.kkoshin.muse.tts.Voice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElevenLabTTSProvider(
    apiKey: String,
) : TTSProvider {
    // Old Male with British accent
    private val presetBrian = "nPczCjzI2devNBz1zQrb"

    private val preferredVoiceId = presetBrian

    private val client = ElevenLabsClient(apiKey)

    override suspend fun queryQuota(): Result<CharacterQuota> =
        withContext(Dispatchers.IO) {
            client.getSubscription().map {
                CharacterQuota(
                    consumed = it.characterCount,
                    total = it.characterLimit,
                )
            }
        }

    override suspend fun queryVoices(): Result<List<Voice>> =
        withContext(Dispatchers.IO) {
            client.getVoices().map { voices ->
                voices.map { item ->
                    Voice(
                        voiceId = item.voiceId,
                        name = item.name,
                        description = item.description,
                        previewUrl = item.previewUrl,
                        accent = Voice.Accent.entries.find { it.raw == item.labels?.get("accent") }
                            ?: Voice.Accent.Other,
                        age = Voice.Age.entries.find { it.raw == item.labels?.get("age") }
                            ?: Voice.Age.Other,
                        useCase = item.labels?.get("use case"),
                        gender = Voice.Gender.entries.find {
                            it.raw == item.labels?.get("gender")
                        },
                    )
                }
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