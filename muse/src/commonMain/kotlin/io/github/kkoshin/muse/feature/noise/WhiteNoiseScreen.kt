package io.github.kkoshin.muse.feature.noise

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.feature.export.AudioProcessingView
import io.github.kkoshin.muse.feature.export.ProgressStatus
import io.github.kkoshin.muse.platformbridge.BackHandler
import kotlinx.serialization.Serializable
import muse.feature.generated.resources.Res
import muse.feature.generated.resources.generate_done
import muse.feature.generated.resources.sound_effect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Serializable
class WhiteNoiseScreenArgs(
    val prompt: String,
    val durationInMills: Long? = null,
    val promptInfluence: Float = 0.3f,
)

@Composable
fun WhiteNoiseScreen(
    modifier: Modifier = Modifier,
    viewModel: WhiteNoiseViewModel = koinViewModel(),
    args: WhiteNoiseScreenArgs,
    onExit: (isSuccess: Boolean) -> Unit,
) {
    val progress by viewModel.progress.collectAsState()

    BackHandler {
        onExit(progress is ProgressStatus.Success)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                backgroundColor = MaterialTheme.colors.surface,
                navigationIcon = {
                    IconButton(onClick = {
                        onExit(progress is ProgressStatus.Success)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(text = stringResource(Res.string.sound_effect))
                },
                elevation = 0.dp,
            )
        },
        content = { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                LaunchedEffect(key1 = Unit) {
                    viewModel.generate(
                        args.prompt,
                        SoundEffectConfig(
                            duration = args.durationInMills?.milliseconds,
                            promptInfluence = args.promptInfluence
                        )
                    )
                }

                AudioProcessingView(
                    modifier,
                    progress = progress,
                    successLabel = stringResource(Res.string.generate_done),
                    onRetry = {
                        viewModel.generate(
                            args.prompt, SoundEffectConfig(
                                duration = args.durationInMills?.milliseconds,
                                promptInfluence = args.promptInfluence
                            )
                        )
                    })
            }
        }
    )
}