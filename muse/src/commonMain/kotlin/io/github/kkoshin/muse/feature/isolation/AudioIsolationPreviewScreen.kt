package io.github.kkoshin.muse.feature.isolation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.audio.AudioMetadata
import io.github.kkoshin.muse.audio.AudioMetadataRetriever
import io.github.kkoshin.muse.audio.ui.AudioPlaybackButton
import kotlinx.serialization.Serializable
import me.saket.bytesize.megabytes
import museroot.muse.generated.resources.Res
import museroot.muse.generated.resources.audio_isolation
import museroot.muse.generated.resources.audio_isolation_error_audio_file_too_large
import museroot.muse.generated.resources.audio_isolation_error_audio_file_too_short
import museroot.muse.generated.resources.remove_noise
import okio.Path
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@Serializable
class AudioIsolationPreviewArgs(
    val audioUri: String,
)

@Composable
fun AudioIsolationPreviewScreen(
    modifier: Modifier = Modifier,
    args: AudioIsolationPreviewArgs,
    metadataRetriever: AudioMetadataRetriever = koinInject(),
    onRequest: () -> Unit,
) {
    var audioMetadata: AudioMetadata? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(args.audioUri) {
        audioMetadata = metadataRetriever.getAudioMetadata(args.audioUri.toPath())
    }

    audioMetadata?.let {
        if (it.size > maxFileSize || it.duration > maxDuration || it.duration < minDuration) {
            AudioNotSupported(
                modifier = modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                isTooShort = it.duration < minDuration,
            )
        } else {
            Content(
                modifier = modifier,
                audioMetadata = it,
                audioPath = args.audioUri.toPath(),
                onRequest = onRequest
            )
        }
    } ?: Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(all = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    audioMetadata: AudioMetadata,
    audioPath: Path,
    onRequest: () -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            stringResource(Res.string.audio_isolation),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )
        audioMetadata.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = MaterialTheme.colors.surface,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colors.onBackground.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(all = 4.dp)
                        .size(24.dp),
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        it.displayName,
                        style = MaterialTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "${it.size} | ${it.duration}",
                        style = MaterialTheme.typography.caption.copy(
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )
                    )
                }
                AudioPlaybackButton(audioSource = audioPath)
            }
        }

        Row(
            modifier = Modifier.align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Points: ${audioMetadata.points}", style = MaterialTheme.typography.caption.copy(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                )
            )
            Button(
                shape = RoundedCornerShape(50),
                onClick = {
                    onRequest()
                },
            ) {
                Icon(
                    Icons.Outlined.AutoFixHigh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.size(8.dp))
                Text(stringResource(Res.string.remove_noise))
            }
        }
    }
}

private val maxFileSize = 500.megabytes
private val maxDuration = 1.hours
private val minDuration = 4.6.seconds

@Composable
private fun AudioNotSupported(modifier: Modifier = Modifier, isTooShort: Boolean) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colors.error,
            modifier = Modifier
                .padding(top = 12.dp)
                .size(40.dp),
        )
        Text(
            text = stringResource(
                if (isTooShort) Res.string.audio_isolation_error_audio_file_too_short else Res.string.audio_isolation_error_audio_file_too_large
            ),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.error.copy(alpha = 0.7f),
        )
    }
}
