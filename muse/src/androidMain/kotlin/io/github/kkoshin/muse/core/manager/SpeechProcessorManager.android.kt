package io.github.kkoshin.muse.core.manager

import android.content.Context
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import com.github.foodiestudio.sugar.storage.filesystem.displayName
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaFile
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaStoreType
import com.github.foodiestudio.sugar.storage.filesystem.toOkioPath
import io.github.kkoshin.elevenlabs.model.SpeechToTextChunkResponseModel
import io.github.kkoshin.muse.core.provider.AudioIsolationProvider
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.core.provider.STTProvider
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.core.provider.SupportedAudioType
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.core.provider.Voice
import io.github.kkoshin.muse.repo.MusePathManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okio.Path
import okio.sink

@OptIn(ExperimentalSugarApi::class)
actual class SpeechProcessorManager(
    private val appContext: Context,
    private val provider: TTSProvider,
    private val isolationProvider: AudioIsolationProvider,
    private val soundEffectProvider: SoundEffectProvider,
    private val sttProvider: STTProvider,
) {
    /**
     * 持久化 text:Uri
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "voices")

    /**
     * 存储当前可用的 voice
     */
    private val availableVoiceIdsKey = stringSetPreferencesKey("available_voice_ids")

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

    actual suspend fun queryAvailableVoiceIds(): Set<String>? =
        appContext.dataStore.data.first()[availableVoiceIdsKey]

    actual suspend fun updateAvailableVoice(voiceIds: Set<String>) {
        appContext.dataStore.edit {
            it[availableVoiceIdsKey] = voiceIds
        }
    }

    /**
     * 如果已经生成过了，本地有音频文件就直接返回
     * TODO 目前的音频格式是固定为 [MonoAudioSampleMetadata]
     * @return 本地文件的 URI，目前仅为 MP3 文件
     */
   actual suspend fun getOrGenerate(
        voiceId: String,
        text: String,
    ): Result<Path> {
        val key = stringPreferencesKey("${voiceId}_${text.lowercase()}")
        return runCatching {
            check(text.isNotBlank())
            appContext.dataStore.data
                .first()[key]
                ?.toUri()?.toOkioPath() ?: run {
                val audio = provider.generate(voiceId, text).getOrThrow()
                val fileExtName = when (audio.mimeType) {
                    SupportedAudioType.MP3 -> ".mp3"
                    SupportedAudioType.PCM -> ".pcm"
                    SupportedAudioType.WAV -> ".wav"
                }
                withContext(Dispatchers.IO) {
                    MediaFile
                        .create(
                            appContext,
                            MediaStoreType.Audio,
                            "${text.lowercase()}$fileExtName",
                            "${MusePathManager.getMusicRelativePath()}/$voiceId",
                            enablePending = true,
                        ).let {
                            it.write {
                                writeAll(audio.content)
                            }
                            it.releasePendingStatus()
                            appContext.dataStore.edit { voices ->
                                voices[key] = it.mediaUri.toString()
                            }
                            it.mediaUri.toOkioPath()
                        }
                }
            }
        }
    }

    /**
     * 对 audioUri 进行背景噪音移除
     * @return 移除后的音频文件的 Base64 字符串
     */
   actual suspend fun removeBackgroundNoise(audioUri: Path): Result<ByteArray> {
        return withContext(Dispatchers.IO) {
            val fileSystem = AppFileHelper(appContext).fileSystem
            val name = fileSystem.metadata(audioUri).displayName
            isolationProvider.removeBackgroundNoise(
                fileSystem.source(audioUri),
                name
            )
        }
    }

    actual suspend fun makeSoundEffects(
        prompt: String,
        config: SoundEffectConfig,
        fileNameWithoutExtension: String,
    ): Result<Path> {
        val targetUri = MediaFile
            .create(
                appContext,
                MediaStoreType.Downloads,
                "$fileNameWithoutExtension.mp3",
                MusePathManager.getExportRelativePath(),
                enablePending = false,
            ).mediaUri
        return withContext(Dispatchers.IO) {
            appContext.contentResolver.openOutputStream(targetUri)!!.use { outputStream ->
                soundEffectProvider.makeSoundEffects(
                    prompt,
                    config,
                    outputStream.sink(),
                )
            }
            Result.success(targetUri.toOkioPath())
        }
    }

    /**
     * 对音频进行转录
     */
   actual suspend fun transcribeAudio(audioUri: Path): Result<SpeechToTextChunkResponseModel> {
        return withContext(Dispatchers.IO) {
            val fileSystem = AppFileHelper(appContext).fileSystem
            val name = fileSystem.metadata(audioUri).displayName
            sttProvider.transcribeAudio(
                fileSystem.source(audioUri),
                name
            )
        }
    }
}