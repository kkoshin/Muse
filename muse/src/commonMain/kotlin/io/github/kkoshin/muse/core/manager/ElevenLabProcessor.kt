package io.github.kkoshin.muse.core.manager

import io.github.kkoshin.elevenlabs.ElevenLabsClient
import io.github.kkoshin.elevenlabs.api.getSubscription
import io.github.kkoshin.elevenlabs.api.getVoices
import io.github.kkoshin.elevenlabs.api.makeSoundEffects
import io.github.kkoshin.elevenlabs.api.textToSpeech
import io.github.kkoshin.elevenlabs.api.transcribeWithAudio
import io.github.kkoshin.elevenlabs.model.FreeTierOutputFormat
import io.github.kkoshin.elevenlabs.model.ModelId
import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.elevenlabs.model.TextToSpeechRequest
import io.github.kkoshin.muse.audio.MonoAudioSampleMetadata
import io.github.kkoshin.muse.core.provider.AudioIsolationProvider
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.STTProvider
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.core.provider.SupportedAudioType
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.core.provider.TTSResult
import io.github.kkoshin.muse.core.provider.Voice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.Sink
import okio.Source
import removeBackgroundAudio
import kotlin.concurrent.Volatile

class ElevenLabProcessor(
    private val accountManager: AccountManager,
    private val scope: CoroutineScope,
) : TTSProvider, AudioIsolationProvider, SoundEffectProvider, STTProvider {
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
                        val accent = Voice.Accent.entries.find {
                            it.raw.equals(item.labels?.get("accent"), true)
//                        }?.also {
//                            logcat { "accent not found: ${item.labels?.get("accent")}" }
                        }

                        Voice(
                            voiceId = item.voiceId,
                            name = item.name,
                            description = item.description,
                            previewUrl = item.previewUrl,
                            accent = accent ?: Voice.Accent.Other,
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

    override suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        target: Sink,
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            requireClient().mapCatching { client ->
                client.makeSoundEffects(
                    prompt,
                    durationSeconds = config.duration?.inWholeMilliseconds?.let { it / 1000.0 },
                    promptInfluence = config.promptInfluence,
                    sink = target,
                ).getOrThrow()
            }
        }
    }

    override suspend fun transcribeAudio(
        audio: Source,
        audioName: String
    ): Result<SpeechToTextChunkResponseModel> {
        return withContext(Dispatchers.IO) {
            requireClient().mapCatching { client ->
                client.transcribeWithAudio(audio, audioName).getOrThrow()
            }
        }
    }
}
