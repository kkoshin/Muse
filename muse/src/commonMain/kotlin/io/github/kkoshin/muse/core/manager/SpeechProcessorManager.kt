package io.github.kkoshin.muse.core.manager

import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.Voice
import kotlinx.datetime.Clock
import okio.Path

/**
 * 存储当前可用的 voice
 */
internal val availableVoiceIdsKey = stringSetPreferencesKey("available_voice_ids")

expect class SpeechProcessorManager {

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

    suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        fileNameWithoutExtension: String = "Sound_${Clock.System.now().epochSeconds}",
    ): Result<Path>

    suspend fun transcribeAudio(audioUri: Path): Result<SpeechToTextChunkResponseModel>
}