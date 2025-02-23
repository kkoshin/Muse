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
import io.github.kkoshin.muse.isolation.AudioIsolationProvider
import io.github.kkoshin.muse.tts.CharacterQuota
import io.github.kkoshin.muse.tts.SupportedAudioType
import io.github.kkoshin.muse.tts.TTSProvider
import io.github.kkoshin.muse.tts.TTSResult
import io.github.kkoshin.muse.tts.Voice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat
import okio.Source
import removeBackgroundAudio

class ElevenLabProvider(
    private val accountManager: AccountManager,
    private val scope: CoroutineScope,
) : TTSProvider, AudioIsolationProvider {
    private lateinit var client: ElevenLabsClient

    @Volatile
    private var isInitialized = false

    private var initJob: Job? = null

    private fun initialize() {
        if (!isInitialized) {
            initJob?.cancel()
            initJob = scope.launch {
                accountManager.apiKey
                    .onStart {
                        isInitialized = true
                    }
                    .distinctUntilChanged()
                    .collectLatest { apiKey ->
                        if (apiKey != null) {
                            client = ElevenLabsClient(apiKey)
                        }
                    }
            }
        }
    }

    private suspend fun requireClient(retryCount: Int = 2): Result<ElevenLabsClient> {
        // check client is initialized
        return if (!::client.isInitialized) {
            if (!isInitialized && retryCount > 0) {
                initialize()
                delay(200)
                requireClient(retryCount - 1)
            } else {
                Result.failure(IllegalStateException("apiKey is not set"))
            }
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
                            },
                            descriptive = item.labels?.get("descriptive")
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

    override suspend fun removeBackgroundNoise(
        audio: Source,
        audioName: String
    ): Result<ByteArray> {
        return withContext(Dispatchers.IO) {
            requireClient().mapCatching { client ->
                client.removeBackgroundAudio(audio, audioName).getOrThrow()
            }
        }
    }
}
