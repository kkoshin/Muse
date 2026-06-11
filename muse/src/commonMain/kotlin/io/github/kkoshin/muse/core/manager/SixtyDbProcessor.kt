package io.github.kkoshin.muse.core.manager

import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.sixtydb.SixtyDbClient
import io.github.kkoshin.sixtydb.api.getDefaultVoices
import io.github.kkoshin.sixtydb.api.getMyVoices
import io.github.kkoshin.sixtydb.api.textToSpeechStream
import io.github.kkoshin.sixtydb.api.transcribeAudio
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
import kotlin.concurrent.Volatile

/**
 * 60db implementation of Muse's processor interfaces. Mirrors the structure
 * of [ElevenLabProcessor] so the [ProcessorRouter] can swap them at runtime
 * based on the user's preference in Settings.
 *
 * Coverage:
 * - TTS via `POST /tts-stream` (NDJSON of base64 mp3 chunks) — streamed
 *   straight into Muse's existing `okio.Source` pipeline.
 * - Voice list via `GET /default-voices` + `GET /my-voices` (deduplicated).
 * - STT via multipart `POST /stt`, packed into the same
 *   `SpeechToTextChunkResponseModel` shape ElevenLabs uses.
 * - Audio isolation + sound effects: 60db has no equivalents, so these throw
 *   [UnsupportedOperationException]. The router intercepts and falls back to
 *   ElevenLabs when its key is configured.
 * - Quota: 60db's billing is per-request, not character-based. We surface
 *   `CharacterQuota.unknown` so the UI gracefully shows "—".
 */
class SixtyDbProcessor(
    private val accountManager: AccountManager,
    private val scope: CoroutineScope,
) : TTSProvider, AudioIsolationProvider, SoundEffectProvider, STTProvider {

    private lateinit var client: SixtyDbClient

    @Volatile
    private var isInitialized = false

    private var initJob: Job? = null

    private fun initialize() {
        if (!isInitialized) {
            initJob?.cancel()
            initJob = scope.launch {
                accountManager.sixtydbApiKey
                    .onStart { isInitialized = true }
                    .distinctUntilChanged()
                    .collectLatest { apiKey ->
                        if (apiKey != null) {
                            client = SixtyDbClient(apiKey)
                        }
                    }
            }
        }
    }

    private suspend fun requireClient(retryCount: Int = 2): Result<SixtyDbClient> =
        if (!::client.isInitialized) {
            if (!isInitialized && retryCount > 0) {
                initialize()
                delay(200)
                requireClient(retryCount - 1)
            } else {
                Result.failure(IllegalStateException("60db apiKey is not set"))
            }
        } else {
            Result.success(client)
        }

    override suspend fun queryQuota(): Result<CharacterQuota> =
        Result.success(CharacterQuota.unknown)

    override suspend fun queryVoices(): Result<List<Voice>> = withContext(Dispatchers.IO) {
        requireClient().mapCatching { client ->
            val defaults = client.getDefaultVoices().getOrThrow()
            val mine = runCatching { client.getMyVoices().getOrThrow() }.getOrDefault(emptyList())
            val all = (defaults + mine).distinctBy { it.voiceId }
            all.map { v ->
                Voice(
                    voiceId = v.voiceId,
                    name = v.name ?: v.voiceId,
                    description = v.description,
                    previewUrl = v.previewUrl ?: "",
                    accent = Voice.Accent.Other,
                    age = Voice.Age.Other,
                    useCase = null,
                    gender = Voice.Gender.entries.find { it.raw.equals(v.gender, true) },
                    descriptive = v.language,
                )
            }
        }
    }

    override suspend fun generate(voiceId: String, text: String): Result<TTSResult> {
        check(text.isNotBlank()) { "text must not be blank." }
        return withContext(Dispatchers.IO) {
            requireClient().mapCatching { client ->
                client.textToSpeechStream(
                    voiceId = voiceId,
                    text = text,
                    outputFormat = "mp3",
                ).getOrThrow().let {
                    TTSResult(it, SupportedAudioType.MP3, MonoAudioSampleMetadata())
                }
            }
        }
    }

    override suspend fun removeBackgroundNoise(audio: Source, audioName: String): Result<ByteArray> =
        Result.failure(UnsupportedOperationException("60db does not provide audio isolation."))

    override suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        target: Sink,
    ): Result<Unit> =
        Result.failure(UnsupportedOperationException("60db does not provide sound effect generation."))

    override suspend fun transcribeAudio(
        audio: Source,
        audioName: String,
    ): Result<SpeechToTextChunkResponseModel> = withContext(Dispatchers.IO) {
        requireClient().mapCatching { client ->
            val resp = client.transcribeAudio(audio, audioName).getOrThrow()
            // Translate 60db's flatter STT response into the ElevenLabs shape so
            // downstream callers do not need to branch by provider.
            // 60db reports the language as a name; ElevenLabs uses a code. We
            // pass the raw value through and use 1.0 as a confidence placeholder.
            SpeechToTextChunkResponseModel(
                text = resp.text,
                languageCode = resp.language ?: "unk",
                languageProbability = 1.0,
                words = emptyList(),
            )
        }
    }
}
