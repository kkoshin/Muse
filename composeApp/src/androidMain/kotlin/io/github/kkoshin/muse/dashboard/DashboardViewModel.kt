package io.github.kkoshin.muse.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kkoshin.muse.MuseRepo
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
                listOf(
                    Script(
                        title = "Script 06-27",
                        text = "A B C",
                    ),
                )
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
    }

    fun importScript(content: String) {
        addScript(Script(text = content))
    }
}