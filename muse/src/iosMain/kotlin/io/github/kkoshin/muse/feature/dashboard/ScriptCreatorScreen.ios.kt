package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import io.github.kkoshin.muse.repo.model.Script

@Composable
actual fun ScriptCreatorScreen(
    modifier: Modifier,
    script: Script?,
    onResult: (scriptId: Uuid?) -> Unit
) {
    // TODO implement ScriptCreatorScreen
}