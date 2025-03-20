@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
object ScriptCreatorArgs {
    const val RESULT_KEY = "script_creator_result_script_id"
}

@Composable
expect fun ScriptCreatorScreen(
    modifier: Modifier = Modifier,
    script: Script? = null,
    onResult: (scriptId: Uuid?) -> Unit,
)
