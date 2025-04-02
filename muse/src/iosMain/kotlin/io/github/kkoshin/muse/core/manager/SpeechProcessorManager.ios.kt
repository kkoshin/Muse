package io.github.kkoshin.muse.core.manager

import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.core.provider.Voice
import okio.Path

// TODO: Implement SpeechProcessorManager
actual class SpeechProcessorManager(
    private val provider: TTSProvider,
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
        return null
    }

    actual suspend fun updateAvailableVoice(voiceIds: Set<String>) {
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