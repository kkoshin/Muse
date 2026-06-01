package io.github.kkoshin.muse.feature.isolation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.feature.export.AudioProcessingView
import io.github.kkoshin.muse.feature.setting.ApiKeyRequiredSheet
import io.github.kkoshin.muse.platformbridge.BackHandler
import kotlinx.serialization.Serializable
import museroot.muse.generated.resources.Res
import museroot.muse.generated.resources.denoise_done
import io.github.kkoshin.muse.Route
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Serializable
class AudioIsolationArgs(
    val audioUri: String,
) : Route

@Composable
fun AudioIsolationScreen(
    modifier: Modifier = Modifier,
    args: AudioIsolationArgs,
    viewModel: AudioIsolationViewModel = koinViewModel(),
    onDone: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToApiKeyHelp: () -> Unit,
) {
    val accountManager = koinInject<AccountManager>()
    val apiKeyConfigured by accountManager.apiKeyConfigured.collectAsState(false)
    val progress by viewModel.progress.collectAsState()
    var showApiKeyDialog by remember { mutableStateOf(false) }

    BackHandler {
        onDone()
    }

    LaunchedEffect(apiKeyConfigured) {
        if (apiKeyConfigured) {
            viewModel.removeBackgroundNoise(args.audioUri.toPath())
        } else {
            showApiKeyDialog = true
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        onDone()
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                },
                windowInsets = WindowInsets.statusBars,
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            )
        },
        content = { contentPadding ->
            Box(Modifier.padding(contentPadding)) {
                AudioProcessingView(
                    modifier,
                    progress = progress,
                    successLabel = stringResource(Res.string.denoise_done),
                    onRetry = { viewModel.removeBackgroundNoise(args.audioUri.toPath()) })
            }
        },
    )

    if (showApiKeyDialog) {
        ApiKeyRequiredSheet(
            onGoToSettings = {
                showApiKeyDialog = false
                onNavigateToSettings()
            },
            onWhatIsApiKey = {
                showApiKeyDialog = false
                onNavigateToApiKeyHelp()
            },
            onDismiss = {
                showApiKeyDialog = false
                onDone()
            },
        )
    }
}
