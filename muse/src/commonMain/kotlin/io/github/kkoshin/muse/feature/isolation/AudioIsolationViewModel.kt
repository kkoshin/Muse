package io.github.kkoshin.muse.feature.isolation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kkoshin.muse.core.manager.AudioIsolationProcessor
import io.github.kkoshin.muse.feature.export.ProgressStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.Path

class AudioIsolationViewModel(
    private val audioIsolationProcessor: AudioIsolationProcessor,
) : ViewModel() {

    private val _progress: MutableStateFlow<ProgressStatus> = MutableStateFlow(ProgressStatus.Idle)
    val progress: StateFlow<ProgressStatus> = _progress.asStateFlow()

    private var lastJob: Job? = null

    fun removeBackgroundNoise(audioUri: Path) {
        _progress.value = ProgressStatus.Processing("remove background noise")
        lastJob?.cancel()
        lastJob = viewModelScope.launch {
            audioIsolationProcessor.removeBackgroundNoiseAndSave(audioUri)
                .onSuccess {
                    _progress.value = ProgressStatus.Success(it)
                }
                .onFailure {
                    _progress.value = ProgressStatus.Failed(it.message ?: "unknown", it)
                }
        }
    }
}
