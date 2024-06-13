package io.github.kkoshin.muse.editor

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kkoshin.muse.MuseRepo
import io.github.kkoshin.muse.audio.Mp3Decoder
import io.github.kkoshin.muse.tts.TTSManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import okio.buffer
import okio.sink
import org.koin.java.KoinJavaComponent.inject

class EditorViewModel(
    private val ttsManager: TTSManager,
    private val repo: MuseRepo,
) : ViewModel() {
    private val appContext: Context by inject(Context::class.java)
    private val tag = this.javaClass.simpleName

    private val _progress: MutableStateFlow<ProgressStatus> = MutableStateFlow(ProgressStatus.Idle)
    val progress: StateFlow<ProgressStatus> = _progress

    fun startTTS(phrases: List<String>) {
        _progress.value = ProgressStatus.Processing(0, "${phrases.size} phrases")
        viewModelScope.launch {
            coroutineScope {
                val requests = phrases.map { phrase ->
                    async {
                        ttsManager.getOrGenerate(phrase).map {
                            // mp3 uri to pcm uri
                            it to saveAsPcm(phrase, it)
                        }.onFailure {
                            it.printStackTrace()
                            _progress.value =
                                ProgressStatus.Failed(errorMsg = it.message ?: "unknown error")
                        }
                    }
                }
                val result = requests.awaitAll()
                if (result.all { it.isSuccess }) {
                    _progress.value = ProgressStatus.Success(
                        pcm = result.map { it.getOrNull()!!.second },
                        audios = result.map { it.getOrNull()!!.first },
                    )
                }
            }
        }
    }

    private suspend fun saveAsPcm(
        text: String,
        mp3Uri: Uri,
    ): Uri {
        val mp3Decoder = Mp3Decoder()
        val target = repo.getVoiceFolder().resolve("${text}.pcm")
        if (target.exists() && target.length() > 0) {
            logcat(tag) {
                "${text}.pcm already exists."
            }
            return target.toUri()
        }
        val output = target.sink().buffer()
        return runCatching {
            output.use {
                // 11 labs 生成的音量偏小，这里需要增大音量
                mp3Decoder.decodeMp3ToPCM(appContext, output, mp3Uri, volumeBoost = 3.0f)
            }
            target.toUri()
        }.onFailure {
            logcat(tag) {
                it.asLog()
            }
            target.delete()
        }.getOrThrow()
    }
}

sealed interface ProgressStatus {
    object Idle : ProgressStatus
    class Failed(val errorMsg: String) : ProgressStatus
    class Processing(val value: Int, val phrase: String) : ProgressStatus
    class Success(val audios: List<Uri>, val pcm: List<Uri>) : ProgressStatus
}