package io.github.kkoshin.muse.feature.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kkoshin.elevenlabs.model.SubscriptionStatus
import io.github.kkoshin.muse.audio.Mp3Decoder
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.platformbridge.MediaStoreHelper
import io.github.kkoshin.muse.platformbridge.SystemFileSystem
import io.github.kkoshin.muse.platformbridge.logcat
import io.github.kkoshin.muse.platformbridge.toSink
import io.github.kkoshin.muse.repo.MusePathManager
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.repo.queryPhrases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
import kotlinx.datetime.Clock
import okio.Path
import okio.buffer
import okio.use
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ExportViewModel(
    private val speechProcessorManager: SpeechProcessorManager,
    accountManager: AccountManager,
    private val mediaStoreHelper: MediaStoreHelper,
    private val repo: MuseRepo,
) : ViewModel() {
    private val tag = this::class.simpleName!!

    private val _progress: MutableStateFlow<ProgressStatus> = MutableStateFlow(ProgressStatus.Idle)
    val progress: StateFlow<ProgressStatus> = _progress.asStateFlow()

    private val maxNumberOfConcurrentRequests = accountManager.subscriptionStatus.map {
        when (it) {
            SubscriptionStatus.Active, SubscriptionStatus.Trialing -> Int.MAX_VALUE
            else -> 4
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 4)

    @OptIn(ExperimentalUuidApi::class)
    suspend fun queryPhrases(scriptId: String): List<String>? =
        repo.queryPhrases(Uuid.parse(scriptId))

    fun startTTSForReadingMode(
        voiceId: String,
        text: String
    ) {
        viewModelScope.launch {
            speechProcessorManager
                .getOrGenerateForLongText(voiceId, text)
                .onSuccess {
                    _progress.value = ProgressStatus.Success(it)
                }
                .onFailure {
                    logcat {
                        it.stackTraceToString()
                    }
                    _progress.value = TTSFailed(throwable = it)
                }
        }
    }

    /**
     * 相同的 phrase 仅需要生成一次，最终返回的时候要按照
     * @param onSuccess 回调的时候会按照 phrases 的顺序返回，前面 tts 这一步是会去重的。对应 phrase 的 pcm 文件
     */
    fun startTTSForDictationMode(
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
                                        it,
                                        repo.getPcmCache(voiceId, phrase),
                                    )
                                }
                        }.onFailure {
                            logcat {
                                it.stackTraceToString()
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
        mp3Path: Path,
        target: Path,
    ) {
        val mp3Decoder = Mp3Decoder()
        if (SystemFileSystem.exists(target)) {
            logcat(tag) {
                "$text.pcm already exists."
            }
        }
        val output = target.toSink().buffer()
        runCatching {
            output.use {
                // 11 labs 生成的音量偏小，这里需要增大音量
                mp3Decoder.decodeMp3ToPCM(output, mp3Path, volumeBoost = 3.0f)
            }
        }.onFailure {
            SystemFileSystem.delete(target)
        }.getOrThrow()
    }

    /**
     * 将音频混合为 MP3 文件
     */
    fun mixAudioAsMp3(
        silence: SilenceDuration,
        phrases: List<String>,
        pcmList: List<Path>,
    ) {
        val exportPipeline = AudioExportPipeline(
            pcmList,
            phrases,
            silence,
        )
        viewModelScope.launch {
            _progress.value = MixProcessing()
            val path = mediaStoreHelper.exportFileToDownload(
                fileName = "Audio_${Clock.System.now().epochSeconds}.mp3",
                relativePath = MusePathManager.getExportRelativePath(),
            )
            path.toSink().buffer().use { outputSink ->
                exportPipeline
                    .start(outputSink)
                    .onSuccess {
                        _progress.value = ProgressStatus.Success(path)
                    }.onFailure { e ->
                        logcat {
                            e.stackTraceToString()
                        }
                        _progress.value = MixFailed(pcmList, e)
                    }
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
