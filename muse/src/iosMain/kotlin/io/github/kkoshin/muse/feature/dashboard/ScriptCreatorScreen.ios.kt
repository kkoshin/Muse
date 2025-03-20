@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kkoshin.muse.repo.model.Script
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
actual fun ScriptCreatorScreen(
    modifier: Modifier,
    script: Script?,
    onResult: (scriptId: Uuid?) -> Unit
) {
    // TODO implement ScriptCreatorScreen
    Box {
        Text("ScriptCreatorScreen")
    }
}