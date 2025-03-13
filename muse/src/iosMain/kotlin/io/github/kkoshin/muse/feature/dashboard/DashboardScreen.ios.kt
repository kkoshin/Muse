package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import io.github.kkoshin.muse.repo.model.Script
import okio.Path

@Composable
actual fun DashboardScreen(
    modifier: Modifier,
    contentUri: Path?,
    initScriptId: Uuid?,
    viewModel: DashboardViewModel,
    onLaunchEditor: (Script) -> Unit,
    onCreateScriptRequest: () -> Unit,
    onLaunchSettingsPage: () -> Unit,
    onDeepLinkHandled: () -> Unit,
    onLaunchAudioIsolation: (uri: String) -> Unit,
    onLaunchWhiteNoise: () -> Unit
) {
    // TODO implement DashboardScreen
}