package io.github.kkoshin.muse.core.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.AudioIsolationProvider
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.STTProvider
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.core.provider.SupportedAudioType
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.core.provider.Voice
import io.github.kkoshin.muse.platformbridge.MediaStoreHelper
import io.github.kkoshin.muse.platformbridge.toNsUrl
import io.github.kkoshin.muse.platformbridge.toOkioPath
import io.github.kkoshin.muse.repo.MusePathManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use
import platform.Foundation.NSURL

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
        val key = stringPreferencesKey("${voiceId}_${text.lowercase()}")
        return runCatching {
            check(text.isNotBlank())
            voicePreference.data.first()[key]?.let {
                NSURL.URLWithString(it)?.toOkioPath()
            } ?: run {
                val audio = provider.generate(voiceId, text).getOrThrow()
                val fileExtName = when (audio.mimeType) {
                    SupportedAudioType.MP3 -> ".mp3"
                    SupportedAudioType.PCM -> ".pcm"
                    SupportedAudioType.WAV -> ".wav"
                }
                withContext(Dispatchers.IO) {
                    mediaStoreHelper.saveAudio(
                        relativePath = "${MusePathManager.getMusicRelativePath()}/$voiceId",
                        fileName = "${text.lowercase()}$fileExtName",
                        action = {
                            writeAll(audio.content)
                        }
                    ).also {
                        voicePreference.edit { voices ->
                            voices[key] = it.toNsUrl()?.absoluteString ?: it.toString()
                        }
                    }
                }
            }
        }
    }

    actual suspend fun removeBackgroundNoise(audioUri: Path): Result<ByteArray> {
        return withContext(Dispatchers.IO) {
            val name = audioUri.name
            (provider as AudioIsolationProvider).removeBackgroundNoise(
                FileSystem.SYSTEM.source(audioUri).buffer(),
                name
            )
        }
    }

    actual suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        fileNameWithoutExtension: String
    ): Result<Path> {
        return withContext(Dispatchers.IO) {
            val path = mediaStoreHelper.exportFileToDownload(
                fileName = "$fileNameWithoutExtension.mp3",
                relativePath = MusePathManager.getExportRelativePath(),
            )
            FileSystem.SYSTEM.sink(path).buffer().use { sink ->
                (provider as SoundEffectProvider).makeSoundEffects(
                    prompt,
                    config,
                    sink,
                )
            }
            Result.success(path)
        }
    }

    actual suspend fun transcribeAudio(audioUri: Path): Result<SpeechToTextChunkResponseModel> {
        return withContext(Dispatchers.IO) {
            val name = audioUri.name
            (provider as STTProvider).transcribeAudio(
                FileSystem.SYSTEM.source(audioUri).buffer(),
                name
            )
        }
    }

}
