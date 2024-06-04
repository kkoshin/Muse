package com.github.kkoshin.speaker.editor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kkoshin.speaker.tts.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditorViewModel(private val ttsManager: TTSManager) : ViewModel() {
    private val tag = this.javaClass.simpleName

    private val _progress: MutableStateFlow<ProgressStatus> = MutableStateFlow(ProgressStatus.Idle)
    val progress: StateFlow<ProgressStatus> = _progress

    fun startTTS(phrase: List<String>) {
        _progress.value = ProgressStatus.Processing(0, phrase.first())
        viewModelScope.launch {
            ttsManager.getOrGenerate(phrase.first()).onFailure {
                it.printStackTrace()
                _progress.value = ProgressStatus.Failed(errorMsg = it.message ?: "unknown error")
            }.onSuccess {
                _progress.value = ProgressStatus.Success(it)
            }
        }
    }
}

sealed interface ProgressStatus {
    object Idle : ProgressStatus
    class Failed(val errorMsg: String) : ProgressStatus
    class Processing(val value: Int, val phrase: String) : ProgressStatus
    class Success(val audio: Uri) : ProgressStatus
}