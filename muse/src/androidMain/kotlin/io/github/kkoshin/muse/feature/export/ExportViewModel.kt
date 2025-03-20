@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.feature.export

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaFile
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaStoreType
import com.github.foodiestudio.sugar.storage.filesystem.toOkioPath
import io.github.kkoshin.elevenlabs.model.SubscriptionStatus
import io.github.kkoshin.muse.audio.Mp3Decoder
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.repo.queryPhrases
import io.github.kkoshin.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat
import okio.Path
import okio.buffer
import okio.sink
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ExportViewModel(
    private val speechProcessorManager: SpeechProcessorManager,
    accountManager: AccountManager,
    private val repo: MuseRepo,
) : ViewModel() {
    private val appContext: Context by inject(Context::class.java)
    private val tag = this.javaClass.simpleName

    private val _progress: MutableStateFlow<ProgressStatus> = MutableStateFlow(ProgressStatus.Idle)
    val progress: StateFlow<ProgressStatus> = _progress.asStateFlow()

    private val maxNumberOfConcurrentRequests = accountManager.subscriptionStatus.map {
        when (it) {
            SubscriptionStatus.Active, SubscriptionStatus.Trialing -> Int.MAX_VALUE
            else -> 4
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 4)

    suspend fun queryPhrases(scriptId: String): List<String>? =
        repo.queryPhrases(Uuid.parse(scriptId))

    /**
     * 相同的 phrase 仅需要生成一次，最终返回的时候要按照
     * @param onSuccess 回调的时候会按照 phrases 的顺序返回，前面 tts 这一步是会去重的。对应 phrase 的 pcm 文件
     */
    fun startTTS(
        voiceId: String,
        phrases: List<String>,
        onSuccess: (pcmList: List<Path>) -> Unit,
    ) {
        _progress.value = TTSProcessing()
        viewModelScope.launch {
            val uniquePhrases = phrases.toSet()
            val results = mutableListOf<Result<Unit>>()
            logcat(tag) { "maxNumberOfConcurrentRequests: ${maxNumberOfConcurrentRequests.value}" }
            for (batch in uniquePhrases.chunked(maxNumberOfConcurrentRequests.value)) {
                val requests = batch.map { phrase ->
                    async {
                        // mp3 原始文件暂时没有记录
                        withContext(Dispatchers.IO) {
                            speechProcessorManager
                                .getOrGenerate(voiceId, phrase)
                                .mapCatching {
                                    saveAsPcm(
                                        phrase,
                                        it.toUri(),
                                        repo.getPcmCache(voiceId, phrase).toFile(),
                                    )
                                }
                        }.onFailure {
                            logcat(tag) {
                                it.asLog()
                            }
                            _progress.value =
                                TTSFailed(throwable = it)
                        }
                    }
                }
                results.addAll(requests.awaitAll())
            }

            if (results.all { it.isSuccess }) {
                onSuccess(
                    phrases.map {
                        // 按照 phrases 的顺序返回，前面 tts 这一步是会去重的
                        repo.getPcmCache(voiceId = voiceId, it)
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

    /**
     * 将音频混合为 MP3 文件
     */
    @OptIn(ExperimentalSugarApi::class)
    fun mixAudioAsMp3(
        silence: SilenceDuration,
        phrases: List<String>,
        pcmList: List<Path>,
    ) {
        val exportPipeline = AudioExportPipeline(
            appContext,
            pcmList,
            phrases,
            silence,
        )
        val targetUri = MediaFile
            .create(
                appContext,
                MediaStoreType.Downloads,
                "Audio_${Instant.now().epochSecond}.mp3",
                MuseRepo.getExportRelativePath(),
                enablePending = false,
            ).mediaUri

        viewModelScope.launch {
            _progress.value = MixProcessing()
            exportPipeline
                .start(targetUri.toOkioPath())
                .onSuccess {
                    _progress.value = ProgressStatus.Success(targetUri.toOkioPath())
                }.onFailure { e ->
                    _progress.value = MixFailed(pcmList, e)
                }
        }
    }
}

class TTSFailed(
    throwable: Throwable,
) : ProgressStatus.Failed("TTS Failed", throwable)

class MixFailed(
    val pcmList: List<Path>,
    throwable: Throwable,
) : ProgressStatus.Failed("Mixing audios failed", throwable)

class TTSProcessing : ProgressStatus.Processing("Processing text-to-speech")

class MixProcessing : ProgressStatus.Processing("Generating mp3 file")
