package io.github.kkoshin.muse.audio.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import okio.Path

@Composable
actual fun AudioPlaybackButton(
    modifier: Modifier,
    audioSource: Path?,
    onProgress: (Float) -> Unit
) {
}