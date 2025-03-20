package io.github.kkoshin.muse.feature.editor

import androidx.lifecycle.ViewModel
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.Voice
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.repo.queryPhrases
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class EditorViewModel(
    private val speechProcessorManager: SpeechProcessorManager,
    private val repo: MuseRepo,
) : ViewModel() {
    suspend fun fetchAvailableVoices(): Result<List<Voice>> {
        val availableVoiceIds =
            speechProcessorManager.queryAvailableVoiceIds() ?: return Result.success(emptyList())
        return speechProcessorManager
            .queryVoiceList(false)
            .map { list ->
                list.filter { it.voiceId in availableVoiceIds }
            }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun queryPhrases(scriptId: String): List<String>? = repo.queryPhrases(Uuid.parse(scriptId))
}