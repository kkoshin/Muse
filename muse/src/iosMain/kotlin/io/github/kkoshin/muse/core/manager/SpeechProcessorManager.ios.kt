package io.github.kkoshin.muse.core.manager

import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.Voice
import okio.Path

actual class SpeechProcessorManager {
    actual suspend fun queryQuota(): Result<CharacterQuota> {
        TODO("Not yet implemented")
    }

    actual suspend fun queryVoiceList(skipCache: Boolean): Result<List<Voice>> {
        TODO("Not yet implemented")
    }

    actual suspend fun queryAvailableVoiceIds(): Set<String>? {
        TODO("Not yet implemented")
    }

    actual suspend fun updateAvailableVoice(voiceIds: Set<String>) {
    }

    actual suspend fun getOrGenerate(
        voiceId: String,
        text: String
    ): Result<Path> {
        TODO("Not yet implemented")
    }

    actual suspend fun removeBackgroundNoise(audioUri: Path): Result<ByteArray> {
        TODO("Not yet implemented")
    }

    actual suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        fileNameWithoutExtension: String
    ): Result<Path> {
        TODO("Not yet implemented")
    }

    actual suspend fun transcribeAudio(audioUri: Path): Result<SpeechToTextChunkResponseModel> {
        TODO("Not yet implemented")
    }

}