package com.github.kkoshin.muse.script

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

@Serializable
object ScriptArgs

/**
 * 1. input/paste sentences here.
 * 2. convert to phrases.
 * 3. request to tts.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScriptScreen(
    modifier: Modifier = Modifier,
    onRequest: (phrases: List<String>) -> Unit,
) {
    var script: String by rememberSaveable { mutableStateOf("") }

    val phrases: List<String> by remember {
        derivedStateOf {
            script.split(" ").filterNot { it.isBlank() }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                title = { Text(text = "Script") },
                actions = {
                    IconButton(
                        enabled = phrases.isNotEmpty(),
                        onClick = {
                            onRequest(phrases)
                        },
                    ) {
                        Icon(Icons.Filled.Done, contentDescription = null)
                    }
                },
            )
        },
        content = {
            Column(Modifier.padding(it)) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    value = script,
                    minLines = 3,
                    maxLines = 5,
                    onValueChange = {
                        script = it
                    },
                    placeholder = {
                        Text("Input your script here")
                    },
                )
                Text(text = "Phrases:", Modifier.padding(horizontal = 16.dp))
                FlowRow(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    phrases.forEach {
                        OutlinedButton(onClick = { /*TODO*/ }) {
                            Text(text = it)
                        }
                    }
                }
            }
        },
    )
}