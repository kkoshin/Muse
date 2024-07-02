package io.github.kkoshin.muse.export

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaFile
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaStoreType
import io.github.kkoshin.muse.MuseRepo
import io.github.kkoshin.muse.R
import io.github.kkoshin.muse.audio.Mp3Decoder
import io.github.kkoshin.muse.tts.TTSManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
import java.time.Instant
import kotlin.time.Duration

class ExportViewModel(
    private val ttsManager: TTSManager,
    private val repo: MuseRepo,
) : ViewModel() {
    private val appContext: Context by inject(Context::class.java)
    private val tag = this.javaClass.simpleName

    private val _progress: MutableStateFlow<ProgressStatus> = MutableStateFlow(ProgressStatus.Idle)
    val progress: StateFlow<ProgressStatus> = _progress.asStateFlow()

    /**
     * 相同的 phrase 仅需要生成一次，最终返回的时候要按照
     */
    fun startTTS(
        voiceId: String,
        phrases: List<String>,
        onSuccess: (pcmList: List<Uri>) -> Unit,
    ) {
        _progress.value = TTSProcessing()
        viewModelScope.launch {
            val requests = phrases.toSet().map { phrase ->
                async {
                    // mp3 原始文件暂时没有记录
                    ttsManager
                        .getOrGenerate(voiceId, phrase)
                        .mapCatching {
                            saveAsPcm(
                                phrase,
                                it,
                                repo.getPcmCache(voiceId, phrase),
                            )
                        }.onFailure {
                            logcat(tag) {
                                it.asLog()
                            }
                            _progress.value =
                                TTSFailed(throwable = it)
                        }
                }
            }
            val result = requests.awaitAll()
            if (result.all { it.isSuccess }) {
                onSuccess(
                    phrases.map {
                        // 按照 phrases 的顺序返回，前面 tts 这一步是会去重的
                        repo.getPcmCache(voiceId = voiceId, it).toUri()
                    },
                )
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

    @OptIn(ExperimentalSugarApi::class)
    fun mixAudioAsMp3(
        silence: Duration,
        pcmList: List<Uri>,
    ) {
        val exportPipeline = AudioExportPipeline(
            appContext,
            pcmList,
            silence,
        )
        val targetUri = MediaFile
            .create(
                appContext,
                MediaStoreType.Downloads,
                "Audio_${Instant.now().epochSecond}.mp3",
                "Download/${appContext.getString(R.string.app_name)}",
                enablePending = false,
            ).mediaUri

        viewModelScope.launch {
            _progress.value = MixProcessing()
            exportPipeline
                .start(targetUri)
                .onSuccess {
                    _progress.value = ProgressStatus.Success(targetUri)
                }.onFailure { e ->
                    _progress.value = MixFailed(pcmList, e)
                }
        }
    }
}

sealed interface ProgressStatus {
    data object Idle : ProgressStatus

    open class Failed(
        val errorMsg: String,
        val throwable: Throwable?,
    ) : ProgressStatus

    open class Processing(
        val description: String,
    ) : ProgressStatus

    class Success(
        val uri: Uri,
    ) : ProgressStatus
}

class TTSFailed(
    throwable: Throwable,
) : ProgressStatus.Failed("TTS Failed", throwable)

class MixFailed(
    val pcmList: List<Uri>,
    throwable: Throwable,
) : ProgressStatus.Failed("Mixing audios failed", throwable)

class TTSProcessing : ProgressStatus.Processing("Processing text-to-speech")

class MixProcessing : ProgressStatus.Processing("Generating mp3 file")