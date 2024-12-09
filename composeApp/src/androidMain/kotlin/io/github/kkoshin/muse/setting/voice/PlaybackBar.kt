package io.github.kkoshin.muse.setting.voice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.KeyboardArrowDown
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
import io.github.kkoshin.muse.tts.Voice
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

enum class PlaybackState {
    Idle,
    Ready,
    Buffering,
    Finished,
}

@Composable
fun PlaybackBar(modifier: Modifier = Modifier, voice: Voice, onClose: () -> Unit) {
    val audioUrl by remember(voice) { mutableStateOf<String?>(voice.previewUrl) }
    val context = LocalContext.current
    val playbackState = remember { mutableStateOf<PlaybackState>(PlaybackState.Idle) }
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

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        player.pause()
    }

    LaunchedEffect(audioUrl) {
        audioUrl?.let {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
            player.play()
        }
    }
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))
        Text(getAccentFlag(voice.accent), style = MaterialTheme.typography.h5)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(voice.name, style = MaterialTheme.typography.body1)
            listOfNotNull(voice.gender?.raw, voice.age?.raw, voice.descriptive)
                .joinToString("・")
                .let { Text(it, style = MaterialTheme.typography.caption) }
        }
        PlaybackButton(
            playbackState = playbackState.value,
            progress = progress,
            isPlaying = playing,
            onResetProgress = {
                player.seekTo(0)
                progress = 0f
            },
            onUpdatePlayWhenReady = { playWhenReady -> player.playWhenReady = playWhenReady }
        )
        IconButton(onClick = { onClose() }) { Icon(Icons.Outlined.KeyboardArrowDown, "close") }
    }
}

@Composable
fun PlaybackButton(
    playbackState: PlaybackState,
    progress: Float,
    isPlaying: Boolean,
    onResetProgress: () -> Unit,
    onUpdatePlayWhenReady: (Boolean) -> Unit,
) {
    IconButton(
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
fun PlaybackButtonContent(progress: Float?, isPlaying: Boolean) {
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
