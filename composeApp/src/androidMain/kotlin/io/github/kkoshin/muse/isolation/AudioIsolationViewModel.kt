package io.github.kkoshin.muse.isolation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kkoshin.muse.tts.TTSManager
import kotlinx.coroutines.launch
import logcat.logcat

class AudioIsolationViewModel(
    private val ttsManager: TTSManager,
) : ViewModel() {
    fun removeBackgroundNoise(audioUri: Uri) {
        viewModelScope.launch {
            ttsManager.removeBackgroundNoise(audioUri)
                .onSuccess {
                    logcat {
                        "remove background noise success: ${it.size}"
                    }
                }
                .onFailure {
                    logcat {
                        "remove background noise failed: $it"
                    }
                }
        }
    }
}