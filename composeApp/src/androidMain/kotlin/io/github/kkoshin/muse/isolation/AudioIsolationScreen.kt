package io.github.kkoshin.muse.isolation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import com.github.foodiestudio.sugar.storage.filesystem.displayName
import com.github.foodiestudio.sugar.storage.filesystem.toOkioPath
import io.github.kkoshin.muse.audio.MediaMetadataRetrieverHelper
import io.github.kkoshin.muse.audio.ui.AudioPlaybackButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import me.saket.bytesize.DecimalByteSize
import me.saket.bytesize.decimalBytes
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration

@Serializable
class AudioIsolationArgs(
    val audioUri: String,
)

private data class AudioMetadata(
    val duration: Duration,
    val displayName: String,
    val size: DecimalByteSize
)

// TODO: 展示时可以显示当前正在播放的音频，并且支持播放，暂停。
// TODO: 预计消耗点数：1000 / min
// TODO：最大支持上传大小为 500 MB或者1个小时
@OptIn(ExperimentalSugarApi::class)
@Composable
fun AudioIsolationScreen(
    modifier: Modifier = Modifier,
    args: AudioIsolationArgs,
    viewModel: AudioIsolationViewModel = koinViewModel(),
) {
    var audioMetadata: AudioMetadata? by remember {
        mutableStateOf(null)
    }

    val context = LocalContext.current

    LaunchedEffect(args.audioUri) {
        withContext(Dispatchers.IO) {
            audioMetadata =
                MediaMetadataRetrieverHelper(context, args.audioUri.toUri()).use { helper ->
                    AppFileHelper(context.applicationContext).fileSystem.metadata(
                        args.audioUri.toUri().toOkioPath()
                    ).let {
                        AudioMetadata(
                            duration = helper.duration,
                            displayName = it.displayName,
                            size = it.size!!.decimalBytes,
                        )
                    }
                }
        }
    }

    Column(
        modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        audioMetadata?.let {
            Row {
                Icon(
                    Icons.Default.Audiotrack,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                )
                Column {
                    Text(it.displayName)
                    Text("${it.size}|${it.duration}")
                }
                AudioPlaybackButton(audioSource = args.audioUri.toUri())
            }
        }

        Button(
            enabled = audioMetadata != null,
            onClick = {
                viewModel.removeBackgroundNoise(args.audioUri.toUri())
            }) {
            Text("Remove background noise")
        }
    }
}