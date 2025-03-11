package io.github.kkoshin.muse.stt

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kkoshin.muse.debugLog
import io.github.kkoshin.muse.tts.TTSManager
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import logcat.asLog
import org.koin.java.KoinJavaComponent.inject

class SttViewModel(
    private val ttsManager: TTSManager,
) : ViewModel() {

    private val appContext: Context by inject(Context::class.java)

    fun startAsr(audioUri: Uri) {
        viewModelScope.launch {
            ttsManager.transcribeAudio(audioUri)
                .onSuccess {
                    debugLog { "transcribe success: \n${Json.encodeToString(it)}" }
                }
                .onFailure {
                    debugLog { "transcribe success: ${it.asLog()}" }
                }
        }
    }
}