package io.github.kkoshin.muse.feature.isolation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.designsystem.component.MuseIconButton
import io.github.kkoshin.muse.designsystem.component.MuseScaffold
import io.github.kkoshin.muse.designsystem.component.MuseTopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
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

    MuseScaffold(
        modifier = modifier,
        topBar = {
            MuseTopAppBar(
                title = {},
                navigationIcon = {
                    MuseIconButton(onClick = {
                        onDone()
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                },
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
