package io.github.kkoshin.muse.editor

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kkoshin.muse.MuseRepo
import io.github.kkoshin.muse.audio.Mp3Decoder
import io.github.kkoshin.muse.tts.CharacterQuota
import io.github.kkoshin.muse.tts.TTSManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import okio.buffer
import okio.sink
import org.koin.java.KoinJavaComponent.inject
import java.io.File

class EditorViewModel(
    private val ttsManager: TTSManager,
    private val repo: MuseRepo,
) : ViewModel() {
    private val appContext: Context by inject(Context::class.java)
    private val tag = this.javaClass.simpleName

    private val _progress: MutableStateFlow<ProgressStatus> =
        MutableStateFlow(ProgressStatus.Idle(CharacterQuota.unknown))
    val progress: StateFlow<ProgressStatus> = _progress.asStateFlow()

    fun refreshQuota() {
        viewModelScope.launch {
            ttsManager
                .queryQuota()
                .onSuccess {
                    _progress.value = ProgressStatus.Idle(it)
                }.onFailure {
                    logcat(tag) {
                        it.asLog()
                    }
                }
        }
    }

    /**
     * 相同的 phrase 仅需要生成一次，最终返回的时候要按照
     */
    fun startTTS(phrases: List<String>) {
        _progress.value = ProgressStatus.Processing(0, "${phrases.size} phrases")
        viewModelScope.launch {
            coroutineScope {
                val requests = phrases.toSet().map { phrase ->
                    async {
                        // mp3 原始文件暂时没有记录
                        ttsManager
                            .getOrGenerate(phrase)
                            .mapCatching {
                                saveAsPcm(
                                    phrase,
                                    it,
                                    repo.getPcmCache(phrase),
                                )
                            }.onFailure {
                                logcat(tag) {
                                    it.asLog()
                                }
                                _progress.value =
                                    ProgressStatus.Failed(errorMsg = it.message ?: "unknown error")
                            }
                    }
                }
                val result = requests.awaitAll()
                if (result.all { it.isSuccess }) {
                    _progress.value = ProgressStatus.Success(
                        // 按照 phrases 的顺序返回，前面 tts 这一步是会去重的
                        pcmList = phrases.map {
                            repo.getPcmCache(it).toUri()
                        },
                    )
                }
            }
        }
    }

    private suspend fun saveAsPcm(
        text: String,
        mp3Uri: Uri,
        target: File,
    ) {
        val mp3Decoder = Mp3Decoder()
        if (target.exists() && target.length() > 0) {
            logcat(tag) {
                "$text.pcm already exists."
            }
        }
        val output = target.sink().buffer()
        runCatching {
            output.use {
                // 11 labs 生成的音量偏小，这里需要增大音量
                mp3Decoder.decodeMp3ToPCM(appContext, output, mp3Uri, volumeBoost = 3.0f)
            }
        }.onFailure {
            target.delete()
        }.getOrThrow()
    }
}

sealed interface ProgressStatus {
    class Idle(
        val characterQuota: CharacterQuota,
    ) : ProgressStatus

    class Failed(
        val errorMsg: String,
    ) : ProgressStatus

    class Processing(
        val value: Int,
        val phrase: String,
    ) : ProgressStatus

    class Success(
        val pcmList: List<Uri>,
    ) : ProgressStatus
}