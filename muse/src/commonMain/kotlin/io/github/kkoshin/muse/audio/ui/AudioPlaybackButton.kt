package io.github.kkoshin.muse.audio.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AudioPlaybackButton(
    modifier: Modifier = Modifier,
    audioSource: Uri?,
    onProgress: (Float) -> Unit = {}
)