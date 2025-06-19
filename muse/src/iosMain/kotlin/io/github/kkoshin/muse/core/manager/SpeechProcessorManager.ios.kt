package io.github.kkoshin.muse.core.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.core.provider.Voice
import io.github.kkoshin.muse.platformbridge.MediaStoreHelper
import kotlinx.coroutines.flow.first
import okio.Path

// TODO: Implement SpeechProcessorManager
actual class SpeechProcessorManager(
    private val provider: TTSProvider,
    private val mediaStoreHelper: MediaStoreHelper,
    private val voicePreference: DataStore<Preferences>
) {
    /**
     * 内存中缓存
     */
    private var voicesCache: List<Voice>? = null

    actual suspend fun queryQuota(): Result<CharacterQuota> = provider.queryQuota()

    actual suspend fun queryVoiceList(skipCache: Boolean): Result<List<Voice>> {
        if (!skipCache) {
            voicesCache?.let {
                return Result.success(it)
            }
        }
        return provider.queryVoices().onSuccess {
            voicesCache = it
        }
    }

    actual suspend fun queryAvailableVoiceIds(): Set<String>? {
        return voicePreference.data.first()[availableVoiceIdsKey]
    }

    actual suspend fun updateAvailableVoice(voiceIds: Set<String>) {
        voicePreference.edit {
            it[availableVoiceIdsKey] = voiceIds
        }
    }

    actual suspend fun getOrGenerate(
        voiceId: String,
        text: String
    ): Result<Path> {
        return Result.failure(Exception("Not yet implemented"))
    }

    actual suspend fun removeBackgroundNoise(audioUri: Path): Result<ByteArray> {
        return Result.failure(Exception("Not yet implemented"))
    }

    actual suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        fileNameWithoutExtension: String
    ): Result<Path> {
        return Result.failure(Exception("Not yet implemented"))
    }

    actual suspend fun transcribeAudio(audioUri: Path): Result<SpeechToTextChunkResponseModel> {
        return Result.failure(Exception("Not yet implemented"))
    }

}