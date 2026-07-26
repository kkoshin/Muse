package io.github.kkoshin.muse.feature.noise

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.designsystem.component.MuseIconButton
import io.github.kkoshin.muse.designsystem.component.MuseScaffold
import io.github.kkoshin.muse.designsystem.component.MuseTopAppBar
import io.github.kkoshin.muse.feature.export.AudioProcessingView
import io.github.kkoshin.muse.feature.export.ProgressStatus
import io.github.kkoshin.muse.platformbridge.BackHandler
import kotlinx.serialization.Serializable
import museroot.muse.generated.resources.Res
import museroot.muse.generated.resources.generate_done
import museroot.muse.generated.resources.sound_effect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

import io.github.kkoshin.muse.Route

@Serializable
class WhiteNoiseScreenArgs(
    val prompt: String,
    val durationInMills: Long? = null,
    val promptInfluence: Float = 0.3f,
) : Route

@OptIn(ExperimentalMaterial3Api::class)
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

    MuseScaffold(
        modifier = modifier,
        topBar = {
            MuseTopAppBar(
                navigationIcon = {
                    MuseIconButton(onClick = {
                        onExit(progress is ProgressStatus.Success)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(text = stringResource(Res.string.sound_effect))
                },
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