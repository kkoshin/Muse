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
import io.github.kkoshin.muse.platformbridge.logcat
import io.github.kkoshin.muse.platformbridge.toNsUrl
import io.github.kkoshin.muse.platformbridge.toOkioPath
import io.github.kkoshin.muse.repo.MusePathManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import platform.Foundation.NSURL

actual class SpeechProcessorManager(
    private val provider: TTSProvider,
    private val isolationProvider: AudioIsolationProvider,
    private val soundEffectProvider: SoundEffectProvider,
    private val sttProvider: STTProvider,
    private val mediaStoreHelper: MediaStoreHelper,
    private val voicePreference: DataStore<Preferences>
) : AudioIsolationProcessor {
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
                logcat { "getOrGenerate from cache: $it" }
                val path = if (it.startsWith("/")) {
                    it.toPath()
                } else {
                    NSURL.URLWithString(it)?.toOkioPath()
                }
                if (path != null && FileSystem.SYSTEM.exists(path)) {
                    path
                } else {
                    logcat { "Cached file does not exist or invalid: $it" }
                    null
                }
            } ?: run {
                logcat { "start generate audio for $text" }
                val audio = provider.generate(voiceId, text).getOrThrow()
                val fileExtName = when (audio.mimeType) {
                    SupportedAudioType.MP3 -> ".mp3"
                    SupportedAudioType.PCM -> ".pcm"
                    SupportedAudioType.WAV -> ".wav"
                }
                withContext(Dispatchers.IO) {
                    logcat { "start saveAudio $text" }
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

    actual suspend fun getOrGenerateForLongText(voiceId: String, longText: String): Result<Path> {
        val textHash = longText.hashCode()
        val key = stringPreferencesKey("${voiceId}_$textHash")
        return runCatching {
            check(longText.isNotBlank())
            voicePreference.data.first()[key]?.let {
                val path = if (it.startsWith("/")) {
                    it.toPath()
                } else {
                    NSURL.URLWithString(it)?.toOkioPath()
                }
                if (path != null && FileSystem.SYSTEM.exists(path)) {
                    path
                } else {
                    logcat { "Cached file does not exist or invalid: $it" }
                    null
                }
            } ?: run {
                val audio = provider.generate(voiceId, longText).getOrThrow()
                check(audio.mimeType == SupportedAudioType.MP3) {
                    "only support Mp3 yet."
                }
                // 文本可能比较长，文件名只能取它的 hash 值
                val fileName = "Audio_$textHash.mp3"
                withContext(Dispatchers.IO) {
                    mediaStoreHelper.saveAudio(
                        relativePath = "${MusePathManager.getMusicRelativePath()}/$voiceId",
                        fileName = fileName,
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
            isolationProvider.removeBackgroundNoise(
                FileSystem.SYSTEM.source(audioUri).buffer(),
                name
            )
        }
    }

    override actual suspend fun removeBackgroundNoiseAndSave(audioUri: Path): Result<Path> {
        return removeBackgroundNoise(audioUri).map { content ->
            withContext(Dispatchers.IO) {
                val path = mediaStoreHelper.exportFileToDownload(
                    fileName = "Denoise_${Clock.System.now().epochSeconds}.mp3",
                    relativePath = MusePathManager.getExportRelativePath(),
                )
                FileSystem.SYSTEM.sink(path).buffer().use { sink ->
                    sink.write(content)
                }
                path
            }
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
                soundEffectProvider.makeSoundEffects(
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
            sttProvider.transcribeAudio(
                FileSystem.SYSTEM.source(audioUri).buffer(),
                name
            )
        }
    }
}
