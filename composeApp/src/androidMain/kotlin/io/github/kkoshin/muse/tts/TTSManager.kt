package io.github.kkoshin.muse.tts

import android.content.Context
import android.net.Uri
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
import io.github.kkoshin.muse.R
import io.github.kkoshin.muse.isolation.AudioIsolationProvider
import io.github.kkoshin.muse.stt.STTProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okio.source

// TODO: rename to MuseManager
@OptIn(ExperimentalSugarApi::class)
class TTSManager(
    private val appContext: Context,
    private val provider: TTSProvider,
    private val isolationProvider: AudioIsolationProvider,
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

    suspend fun queryQuota(): Result<CharacterQuota> = provider.queryQuota()

    suspend fun queryVoiceList(skipCache: Boolean): Result<List<Voice>> {
        if (!skipCache) {
            voicesCache?.let {
                return Result.success(it)
            }
        }
        return provider.queryVoices().onSuccess {
            voicesCache = it
        }
    }

    suspend fun queryAvailableVoiceIds(): Set<String>? =
        appContext.dataStore.data.first()[availableVoiceIdsKey]

    suspend fun updateAvailableVoice(voiceIds: Set<String>) {
        appContext.dataStore.edit {
            it[availableVoiceIdsKey] = voiceIds
        }
    }

    /**
     * 如果已经生成过了，本地有音频文件就直接返回
     * TODO 目前的音频格式是固定为 [MonoAudioSampleMetadata]
     * @return 本地文件的 URI，目前仅为 MP3 文件
     */
    suspend fun getOrGenerate(
        voiceId: String,
        text: String,
    ): Result<Uri> {
        val key = stringPreferencesKey("${voiceId}_${text.lowercase()}")
        return runCatching {
            check(text.isNotBlank())
            appContext.dataStore.data
                .first()[key]
                ?.toUri() ?: run {
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
                            "Music/${appContext.getString(R.string.app_name)}/$voiceId",
                            enablePending = true,
                        ).let {
                            it.write {
                                writeAll(audio.content.source())
                            }
                            it.releasePendingStatus()
                            appContext.dataStore.edit { voices ->
                                voices[key] = it.mediaUri.toString()
                            }
                            it.mediaUri
                        }
                }
            }
        }
    }

    /**
     * 对 audioUri 进行背景噪音移除
     * @return 移除后的音频文件的 Base64 字符串
     */
    suspend fun removeBackgroundNoise(audioUri: Uri): Result<ByteArray> {
        return withContext(Dispatchers.IO) {
            val fileSystem = AppFileHelper(appContext).fileSystem
            val name = fileSystem.metadata(audioUri.toOkioPath()).displayName
            isolationProvider.removeBackgroundNoise(
                fileSystem.source(audioUri.toOkioPath()),
                name
            )
        }
    }

    /**
     * 对音频进行转录
     */
    suspend fun transcribeAudio(audioUri: Uri): Result<SpeechToTextChunkResponseModel> {
        return withContext(Dispatchers.IO) {
            val fileSystem = AppFileHelper(appContext).fileSystem
            val name = fileSystem.metadata(audioUri.toOkioPath()).displayName
            sttProvider.transcribeAudio(
                fileSystem.source(audioUri.toOkioPath()),
                name
            )
        }
    }
}