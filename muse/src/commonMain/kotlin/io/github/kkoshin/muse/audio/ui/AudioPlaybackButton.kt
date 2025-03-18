package io.github.kkoshin.muse.audio.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import okio.Path

@Composable
expect fun AudioPlaybackButton(
    modifier: Modifier = Modifier,
    audioSource: Path?,
    onProgress: (Float) -> Unit = {}
)