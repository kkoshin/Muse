package io.github.kkoshin.muse.isolation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaFile
import com.github.foodiestudio.sugar.storage.filesystem.media.MediaStoreType
import io.github.kkoshin.muse.export.ProgressStatus
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.tts.TTSManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.time.Instant

class AudioIsolationViewModel(
    private val ttsManager: TTSManager,
) : ViewModel() {

    private val appContext: Context by inject(Context::class.java)

    private val _progress: MutableStateFlow<ProgressStatus> = MutableStateFlow(ProgressStatus.Idle)
    val progress: StateFlow<ProgressStatus> = _progress.asStateFlow()

    private var lastJob: Job? = null

    @OptIn(ExperimentalSugarApi::class)
    fun removeBackgroundNoise(audioUri: Uri) {
        _progress.value = ProgressStatus.Processing("remove background noise")
        lastJob?.cancel()
        lastJob = viewModelScope.launch {
            ttsManager.removeBackgroundNoise(audioUri)
                .onSuccess {
                    val targetUri = MediaFile
                        .create(
                            appContext,
                            MediaStoreType.Downloads,
                            "Denoise_${Instant.now().epochSecond}.mp3",
                            MuseRepo.getExportRelativePath(appContext),
                            enablePending = false,
                        ).mediaUri
                    appContext.contentResolver.openOutputStream(targetUri)?.use { outputStream ->
                        it.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    _progress.value = ProgressStatus.Success(targetUri)
                }
                .onFailure {
                    _progress.value = ProgressStatus.Failed(it.message ?: "unknown", it)
                }
        }
    }
}