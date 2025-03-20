@file:Suppress("ktlint:standard:no-wildcard-imports")

package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.serialization.Serializable
import okio.Path
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object DashboardArgs

@Composable
expect fun DashboardScreen(
    modifier: Modifier = Modifier,
    contentUri: Path?,
    initScriptId: Uuid?,
    viewModel: DashboardViewModel = koinViewModel(),
    onLaunchEditor: (Script) -> Unit,
    onCreateScriptRequest: () -> Unit,
    onLaunchSettingsPage: () -> Unit,
    onDeepLinkHandled: () -> Unit,
    onLaunchAudioIsolation: (uri: String) -> Unit,
    onLaunchWhiteNoise: () -> Unit,
)

