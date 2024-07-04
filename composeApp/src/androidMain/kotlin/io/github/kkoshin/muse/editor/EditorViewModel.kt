package io.github.kkoshin.muse.editor

import androidx.lifecycle.ViewModel
import io.github.kkoshin.muse.dashboard.Script
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.repo.queryPhrases
import io.github.kkoshin.muse.tts.TTSManager
import io.github.kkoshin.muse.tts.Voice
import java.util.UUID

class EditorViewModel(
    private val ttsManager: TTSManager,
    private val repo: MuseRepo,
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

    suspend fun queryPhrases(scriptId: String): List<String>? = repo.queryPhrases(UUID.fromString(scriptId))
}