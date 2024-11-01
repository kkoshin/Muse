package io.github.kkoshin.muse.tts.vendor

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.api.getSubscription
import io.github.kkoshin.elevenlabs.api.getVoices
import io.github.kkoshin.elevenlabs.api.textToSpeech
import io.github.kkoshin.elevenlabs.model.FreeTierOutputFormat
import io.github.kkoshin.elevenlabs.model.ModelId
import io.github.kkoshin.elevenlabs.model.TextToSpeechRequest
import io.github.kkoshin.muse.AccountManager
import io.github.kkoshin.muse.audio.MonoAudioSampleMetadata
import io.github.kkoshin.muse.tts.CharacterQuota
import io.github.kkoshin.muse.tts.SupportedAudioType
import io.github.kkoshin.muse.tts.TTSProvider
import io.github.kkoshin.muse.tts.TTSResult
import io.github.kkoshin.muse.tts.Voice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat

class ElevenLabTTSProvider(
    accountManager: AccountManager,
    scope: CoroutineScope,
) : TTSProvider {
    private lateinit var client: ElevenLabsClient

    init {
        scope.launch {
            accountManager.apiKey
                .distinctUntilChanged()
                .collectLatest { apiKey ->
                    if (apiKey != null) {
                        client = ElevenLabsClient(apiKey)
                    }
                }
        }
    }

    private fun requireClient(): Result<ElevenLabsClient> {
        // check client is initialized
        return if (!::client.isInitialized) {
            Result.failure(IllegalStateException("apiKey is not set"))
        } else {
            Result.success(client)
        }
    }

    override suspend fun queryQuota(): Result<CharacterQuota> =
        withContext(Dispatchers.IO) {
            requireClient()
                .mapCatching { client ->
                    client.getSubscription().getOrThrow().let {
                        CharacterQuota(
                            consumed = it.characterCount,
                            total = it.characterLimit,
                            status = it.status,
                        )
                    }
                }
        }

    override suspend fun queryVoices(): Result<List<Voice>> =
        withContext(Dispatchers.IO) {
            requireClient()
                .mapCatching { client ->
                    client.getVoices().getOrThrow().map { item ->
                        Voice(
                            voiceId = item.voiceId,
                            name = item.name,
                            description = item.description,
                            previewUrl = item.previewUrl,
                            accent = Voice.Accent.entries.find {
                                it.raw.equals(item.labels?.get("accent"), true)
                            } ?: Voice.Accent.Other.also {
                                logcat { "accent not found: ${item.labels?.get("accent")}" }
                            },
                            age = Voice.Age.entries.find {
                                it.raw.equals(item.labels?.get("age"), true)
                            } ?: Voice.Age.Other,
                            useCase = item.labels?.get("use case"),
                            gender = Voice.Gender.entries.find {
                                it.raw.equals(item.labels?.get("gender"), true)
                            }
                        )
                    }
                }
        }


    override suspend fun generate(
        voiceId: String,
        text: String,
    ): Result<TTSResult> {
        check(text.isNotBlank()) {
            "text must not be blank."
        }
        return withContext(Dispatchers.IO) {
            val request = TextToSpeechRequest(
                text = text,
                modelId = ModelId.EnglishTurbo,
            )
            requireClient().mapCatching { client ->
                client.textToSpeech(
                    voiceId = voiceId,
                    textToSpeechRequest = request,
                    outputFormat = FreeTierOutputFormat.Mp3_44100_128,
                    optimizeStreamingLatency = null,
                ).getOrThrow().let {
                    TTSResult(it, SupportedAudioType.MP3, MonoAudioSampleMetadata())
                }
            }
        }
    }
}
