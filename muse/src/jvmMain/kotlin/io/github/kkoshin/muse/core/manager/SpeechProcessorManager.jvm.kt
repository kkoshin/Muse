package io.github.kkoshin.muse.core.manager

import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.Voice
import okio.Path

actual class SpeechProcessorManager : AudioIsolationProcessor {

    actual suspend fun queryQuota(): Result<CharacterQuota> = Result.failure(Exception("Not implemented on Desktop"))

    actual suspend fun queryVoiceList(skipCache: Boolean): Result<List<Voice>> = Result.success(emptyList())

    actual suspend fun queryAvailableVoiceIds(): Set<String>? = emptySet()

    actual suspend fun updateAvailableVoice(voiceIds: Set<String>) {}

    actual suspend fun getOrGenerate(
        voiceId: String,
        text: String,
    ): Result<Path> = Result.failure(Exception("Not implemented on Desktop"))

    actual suspend fun getOrGenerateForLongText(voiceId: String, longText: String): Result<Path> = Result.failure(Exception("Not implemented on Desktop"))

    actual suspend fun removeBackgroundNoise(audioUri: Path): Result<ByteArray> = Result.failure(Exception("Not implemented on Desktop"))

    actual override suspend fun removeBackgroundNoiseAndSave(audioUri: Path): Result<Path> = Result.failure(Exception("Not implemented on Desktop"))

    actual suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        fileNameWithoutExtension: String,
    ): Result<Path> = Result.failure(Exception("Not implemented on Desktop"))

    actual suspend fun transcribeAudio(audioUri: Path): Result<SpeechToTextChunkResponseModel> = Result.failure(Exception("Not implemented on Desktop"))
}
