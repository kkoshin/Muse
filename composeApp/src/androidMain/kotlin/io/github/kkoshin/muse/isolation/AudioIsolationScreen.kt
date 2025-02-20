package io.github.kkoshin.muse.isolation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.serialization.Serializable
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
) {
    Column(
        modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text("Selected audio: ${args.audioUri}")
        Button(onClick = {
            viewModel.removeBackgroundNoise(args.audioUri.toUri())
        }) {
            Text("Remove background noise")
        }
    }
}