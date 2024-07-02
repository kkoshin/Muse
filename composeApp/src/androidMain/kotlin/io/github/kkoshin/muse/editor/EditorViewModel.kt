package io.github.kkoshin.muse.editor

import androidx.lifecycle.ViewModel
import io.github.kkoshin.muse.tts.TTSManager
import io.github.kkoshin.muse.tts.Voice

class EditorViewModel(
    private val ttsManager: TTSManager,
) : ViewModel() {
    suspend fun fetchAvailableVoices(): Result<List<Voice>> {
        val availableVoiceIds =
            ttsManager.queryAvailableVoiceIds() ?: return Result.success(emptyList())
        return ttsManager
            .queryVoiceList()
            .map { list ->
                list.filter { it.voiceId in availableVoiceIds }
            }
    }
}