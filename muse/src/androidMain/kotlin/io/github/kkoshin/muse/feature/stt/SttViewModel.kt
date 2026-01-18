package io.github.kkoshin.muse.feature.stt

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.foodiestudio.sugar.storage.filesystem.toOkioPath
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import logcat.asLog
import logcat.logcat
import org.koin.java.KoinJavaComponent.inject

class SttViewModel(
    private val speechProcessorManager: SpeechProcessorManager,
) : ViewModel() {

    private val appContext: Context by inject(Context::class.java)

    fun startAsr(audioUri: Uri) {
        viewModelScope.launch {
            speechProcessorManager.transcribeAudio(audioUri.toOkioPath())
                .onSuccess {
                    logcat { "transcribe success: \n${Json.encodeToString(it)}" }
                }
                .onFailure {
                    logcat { "transcribe success: ${it.asLog()}" }
                }
        }
    }
}