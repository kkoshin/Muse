package io.github.kkoshin.muse.noise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kkoshin.muse.tts.TTSManager
import kotlinx.coroutines.launch
import logcat.logcat

class WhiteNoiseViewModel(
    private val ttsManager: TTSManager,
) : ViewModel() {
    // TODO: show progress
    fun generate(prompt: String) {
        viewModelScope.launch {
            ttsManager.makeSoundEffects(
                prompt,
                config = SoundEffectConfig(promptInfluence = 0.5)
            ).onSuccess {
                logcat {
                    "generate success."
                }
            }.onFailure {
                it.printStackTrace()
                logcat {
                    "generate fail. "
                }
            }
        }
    }
}