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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.feature.export.AudioProcessingView
import io.github.kkoshin.muse.platformbridge.BackHandler
import kotlinx.serialization.Serializable
import muse.feature.generated.resources.Res
import muse.feature.generated.resources.denoise_done
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
class AudioIsolationArgs(
    val audioUri: String,
)

@Composable
fun AudioIsolationScreen(
    modifier: Modifier = Modifier,
    args: AudioIsolationArgs,
    viewModel: AudioIsolationViewModel = koinViewModel(),
    onExit: () -> Unit,
) {
    val progress by viewModel.progress.collectAsState()

    BackHandler {
        onExit()
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        onExit()
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
                LaunchedEffect(key1 = Unit) {
                    viewModel.removeBackgroundNoise(args.audioUri.toPath())
                }

                AudioProcessingView(
                    modifier,
                    progress = progress,
                    successLabel = stringResource(Res.string.denoise_done),
                    onRetry = { viewModel.removeBackgroundNoise(args.audioUri.toPath()) })
            }
        },
    )
}
