package io.github.kkoshin.muse.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repo: MuseRepo,
) : ViewModel() {
    private val _scripts: MutableStateFlow<List<Script>> = MutableStateFlow(emptyList())
    val scripts: StateFlow<List<Script>> = _scripts.asStateFlow()

    fun loadScripts() {
        viewModelScope.launch {
            _scripts.update {
                repo.queryAllScripts()
            }
        }
    }

    private fun addScript(script: Script) {
        val newList = _scripts.value.toMutableList().apply {
            add(script)
        }
        _scripts.update {
            newList
        }
        viewModelScope.launch {
            repo.insertScript(script)
        }
    }

    fun importScript(content: String) {
        addScript(Script(text = content))
    }

    fun deleteScript(scriptId: Uuid) {
        val newList = _scripts.value.toMutableList().apply {
            removeAll { it.id == scriptId }
        }
        _scripts.update {
            newList
        }
        viewModelScope.launch {
            repo.deleteScript(scriptId)
        }
    }
}