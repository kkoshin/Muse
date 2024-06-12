package com.github.kkoshin.muse.tts

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaFile
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaStoreType
import com.github.kkoshin.muse.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okio.source

@OptIn(ExperimentalSugarApi::class)
class TTSManager(private val appContext: Context, private val provider: TTSProvider) {
    /**
     * 持久化 text:Uri
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "voices")

    /**
     * 如果已经生成过了，本地有音频文件就直接返回
     * TODO 目前的音频格式是固定为 [MonoAudioSampleMetadata]
     * @return 本地文件的 URI
     */
    suspend fun getOrGenerate(text: String): Result<Uri> {
        val key = stringPreferencesKey(text.lowercase())
        return runCatching {
            check(text.isNotBlank())
            appContext.dataStore.data.first()[key]?.toUri() ?: run {
                val audio = provider.generate(text).getOrThrow()
                val fileExtName = when (audio.mimeType) {
                    SupportedAudioType.MP3 -> ".mp3"
                    SupportedAudioType.PCM -> ".pcm"
                    SupportedAudioType.WAV -> ".wav"
                }
                withContext(Dispatchers.IO) {
                    MediaFile.create(
                        appContext,
                        MediaStoreType.Audio,
                        "${text.lowercase()}${fileExtName}",
                        "Music/${appContext.getString(R.string.app_name)}",
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
}