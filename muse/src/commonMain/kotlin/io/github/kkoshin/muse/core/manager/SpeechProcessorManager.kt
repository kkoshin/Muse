package io.github.kkoshin.muse.core.manager

import android.net.Uri
import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.Voice
import java.time.Instant

expect class SpeechProcessorManager {

    suspend fun queryQuota(): Result<CharacterQuota>

    suspend fun queryVoiceList(skipCache: Boolean): Result<List<Voice>>

    suspend fun queryAvailableVoiceIds(): Set<String>?

    suspend fun updateAvailableVoice(voiceIds: Set<String>)

    suspend fun getOrGenerate(
        voiceId: String,
        text: String,
    ): Result<Uri>

    suspend fun removeBackgroundNoise(audioUri: Uri): Result<ByteArray>

    suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        fileNameWithoutExtension: String = "Sound_${Instant.now().epochSecond}",
    ): Result<Uri>

    suspend fun transcribeAudio(audioUri: Uri): Result<SpeechToTextChunkResponseModel>
}