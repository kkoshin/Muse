package io.github.kkoshin.muse.audio.ui

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

enum class PlaybackState {
    Idle,
    Ready,
    Buffering,
    Finished,
}

@Composable
actual fun AudioPlaybackButton(
    modifier: Modifier,
    audioSource: Uri?,
    onProgress: (Float) -> Unit
) {
    val audioUri by remember(audioSource) { mutableStateOf(audioSource) }
    val context = LocalContext.current
    val playbackState = remember { mutableStateOf(PlaybackState.Idle) }
    var progress by remember { mutableFloatStateOf(0f) }
    val player by remember { mutableStateOf(ExoPlayer.Builder(context).build()) }

    var playing by remember { mutableStateOf(false) }

    DisposableEffect(player) {
        val playbackStateListener =
            object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_IDLE -> playbackState.value = PlaybackState.Idle
                        Player.STATE_BUFFERING -> playbackState.value = PlaybackState.Buffering
                        Player.STATE_READY -> playbackState.value = PlaybackState.Ready
                        Player.STATE_ENDED -> playbackState.value = PlaybackState.Finished
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    playing = isPlaying
                }
            }

        player.addListener(playbackStateListener)
        onDispose {
            player.removeListener(playbackStateListener)
            player.release()
        }
    }

    LaunchedEffect(playbackState.value, playing) {
        when (playbackState.value) {
            PlaybackState.Ready -> {
                while (playing) {
                    progress = player.contentPosition / player.duration.toFloat()
                    delay(100.milliseconds)
                }
            }

            PlaybackState.Finished -> {
                progress = 100f
            }

            PlaybackState.Idle -> {
                progress = 0f
            }

            else -> {
                // do nothing
            }
        }
    }

    LaunchedEffect(progress) {
        onProgress(progress)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        player.pause()
    }

    LaunchedEffect(audioUri) {
        audioUri?.let {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
            player.play()
        }
    }

    PlaybackButton(
        modifier = modifier,
        playbackState = playbackState.value,
        progress = progress,
        isPlaying = playing,
        onResetProgress = {
            player.seekTo(0)
            progress = 0f
        },
        onUpdatePlayWhenReady = { playWhenReady -> player.playWhenReady = playWhenReady }
    )
}

@Composable
private fun PlaybackButton(
    modifier: Modifier,
    playbackState: PlaybackState,
    progress: Float,
    isPlaying: Boolean,
    onResetProgress: () -> Unit,
    onUpdatePlayWhenReady: (Boolean) -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = {
            when (playbackState) {
                PlaybackState.Ready -> {
                    if (isPlaying) {
                        onUpdatePlayWhenReady(false)
                    } else {
                        onUpdatePlayWhenReady(true)
                    }
                }

                PlaybackState.Finished -> {
                    onResetProgress()
                    onUpdatePlayWhenReady(true)
                }

                else -> {
                    // do nothing
                }
            }
        }
    ) {
        when (playbackState) {
            PlaybackState.Idle, PlaybackState.Buffering -> {
                PlaybackButtonContent(progress = null, isPlaying = false)
            }

            PlaybackState.Ready -> {
                PlaybackButtonContent(progress = progress, isPlaying = isPlaying)
            }

            PlaybackState.Finished -> {
                PlaybackButtonContent(progress = progress, isPlaying = false)
            }
        }
    }
}

@Composable
private fun PlaybackButtonContent(progress: Float?, isPlaying: Boolean) {
    Box(contentAlignment = Alignment.Center) {
        if (progress == null) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                strokeWidth = 2.dp
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                progress = progress,
                color = MaterialTheme.colors.onSurface,
                strokeWidth = 2.dp
            )
        }
        if (isPlaying) {
            Icon(Icons.Filled.Pause, "pause", Modifier.size(16.dp))
        } else {
            Icon(Icons.Filled.PlayArrow, "play", Modifier.size(16.dp))
        }
    }
}
