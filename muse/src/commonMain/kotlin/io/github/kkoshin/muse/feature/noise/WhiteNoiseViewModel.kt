package io.github.kkoshin.muse.feature.noise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.feature.export.ProgressStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WhiteNoiseViewModel(
    private val speechProcessorManager: SpeechProcessorManager,
) : ViewModel() {

    private val _progress: MutableStateFlow<ProgressStatus> = MutableStateFlow(ProgressStatus.Idle)
    val progress: StateFlow<ProgressStatus> = _progress.asStateFlow()

    private var lastJob: Job? = null

    fun generate(prompt: String, config: SoundEffectConfig) {
        _progress.value = ProgressStatus.Processing("remove background noise")
        lastJob?.cancel()
        lastJob = viewModelScope.launch {
            speechProcessorManager.makeSoundEffects(
                prompt,
                config = config
            ).fold(
                onSuccess = {
                    _progress.value = ProgressStatus.Success(it)
                },
                onFailure = {
                    _progress.value = ProgressStatus.Failed(it.message ?: "unknown", it)
                })
        }
    }
}