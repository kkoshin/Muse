package io.github.kkoshin.muse.core.manager

import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.Voice
import kotlin.time.Clock
import okio.Path

/**
 * Voice ids the user has selected for ElevenLabs (legacy key — name preserved
 * for backwards compatibility with existing installs).
 */
internal val availableVoiceIdsKey = stringSetPreferencesKey("available_voice_ids")

/** Voice ids the user has selected for 60db. */
internal val availableSixtydbVoiceIdsKey = stringSetPreferencesKey("available_voice_ids_sixtydb")

expect class SpeechProcessorManager : AudioIsolationProcessor {

    suspend fun queryQuota(): Result<CharacterQuota>

    suspend fun queryVoiceList(skipCache: Boolean): Result<List<Voice>>

    suspend fun queryAvailableVoiceIds(): Set<String>?

    suspend fun updateAvailableVoice(voiceIds: Set<String>)

    suspend fun getOrGenerate(
        voiceId: String,
        text: String,
    ): Result<Path>

    suspend fun getOrGenerateForLongText(voiceId: String, longText: String): Result<Path>

    suspend fun removeBackgroundNoise(audioUri: Path): Result<ByteArray>

    override suspend fun removeBackgroundNoiseAndSave(audioUri: Path): Result<Path>

    suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        fileNameWithoutExtension: String = "Sound_${Clock.System.now().epochSeconds}",
    ): Result<Path>

    suspend fun transcribeAudio(audioUri: Path): Result<SpeechToTextChunkResponseModel>
}