package io.github.kkoshin.muse.isolation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.foodiestudio.sugar.notification.toast
import io.github.kkoshin.muse.export.ProgressStatus
import kotlinx.serialization.Serializable
import muse.composeapp.generated.resources.Res
import muse.composeapp.generated.resources.denoise_done
import muse.composeapp.generated.resources.open_with_other_app
import muse.composeapp.generated.resources.retry
import muse.composeapp.generated.resources.share_to_other_app
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

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
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
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
                        backPressedDispatcher?.onBackPressed()
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
                Content(
                    modifier = Modifier.fillMaxSize(),
                    audioUri = args.audioUri.toUri(),
                    progress,
                    viewModel = viewModel,
                )
            }
        },
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    audioUri: Uri,
    progress: ProgressStatus,
    viewModel: AudioIsolationViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.removeBackgroundNoise(audioUri)
    }

    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        when (progress) {
            is ProgressStatus.Idle -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 64.dp),
                    verticalArrangement = Arrangement.spacedBy(64.dp),
                ) {
                    CircularProgressIndicator(
                        Modifier.size(112.dp),
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round,
                    )
                }
            }

            is ProgressStatus.Processing -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 64.dp),
                    verticalArrangement = Arrangement.spacedBy(64.dp),
                ) {
                    CircularProgressIndicator(
                        Modifier.size(112.dp),
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round,
                    )
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AnimatedContent(
                            targetState = progress.description,
                            transitionSpec = {
                                fadeIn()
                                    .togetherWith(fadeOut())
                                    .using(
                                        SizeTransform(clip = false),
                                    )
                            },
                            label = "",
                        ) {
                            Text(text = it, style = MaterialTheme.typography.h6)
                        }
                    }
                }
            }

            is ProgressStatus.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 64.dp),
                    verticalArrangement = Arrangement.spacedBy(64.dp),
                ) {
                    Icon(
                        Icons.Default.AudioFile,
                        null,
                        modifier = Modifier.size(112.dp),
                        tint = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                    )
                    Text(stringResource(Res.string.denoise_done), style = MaterialTheme.typography.h6)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 60.dp),
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                Intent(Intent.ACTION_VIEW).let {
                                    it.data = progress.uri
                                    context.startActivity(it)
                                }
                            },
                        ) {
                            Text(text = stringResource(Res.string.open_with_other_app))
                        }

                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                runCatching {
                                    Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "audio/mpeg"
                                        putExtra(Intent.EXTRA_STREAM, progress.uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        context.startActivity(this)
                                    }
                                }.onFailure { err ->
                                    context.toast(err.message)
                                    err.printStackTrace()
                                }
                            },
                        ) {
                            Text(text = stringResource(Res.string.share_to_other_app))
                        }
                    }
                }
            }

            is ProgressStatus.Failed -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 64.dp),
                    verticalArrangement = Arrangement.spacedBy(64.dp),
                ) {
                    Text("_(:з」∠)_", style = MaterialTheme.typography.h4)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = progress.errorMsg, style = MaterialTheme.typography.h6)
                        Text(text = progress.throwable?.message ?: "", maxLines = 6, overflow = TextOverflow.Ellipsis)
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 60.dp),
                        onClick = {
                            viewModel.removeBackgroundNoise(audioUri)
                        },
                    ) {
                        Text(text = stringResource(Res.string.retry))
                    }
                }
            }
        }
    }
}