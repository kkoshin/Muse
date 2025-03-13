package io.github.kkoshin.muse.feature.stt

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class SttArgs(
    val audioUri: String,
)

@Composable
fun SttScreen(
    modifier: Modifier = Modifier,
    args: SttArgs,
    viewModel: SttViewModel = koinViewModel(),
) {
    Button(onClick = {
        viewModel.startAsr(args.audioUri.toUri())
    }) {
        Text("Start ASR")
    }
}