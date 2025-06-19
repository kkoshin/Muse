package io.github.kkoshin.muse.audio.ui

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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.platformbridge.logcat
import io.github.kkoshin.muse.platformbridge.toNsUrl
import kotlinx.coroutines.delay
import okio.Path
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemFailedToPlayToEndTimeErrorKey
import platform.AVFoundation.AVPlayerItemFailedToPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import kotlin.time.Duration.Companion.milliseconds

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
@Composable
actual fun AudioPlaybackButton(
    modifier: Modifier,
    audioSource: Path?,
    onProgress: (Float) -> Unit
) {
    val audioPath by remember(audioSource) { mutableStateOf(audioSource) }
    var playbackState by remember { mutableStateOf(PlaybackState.Idle) }
    var progress by remember { mutableFloatStateOf(0f) }
    var player by remember { mutableStateOf<AVPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(audioPath) {
        if (audioPath != null) {
            val playerItem = AVPlayerItem(uRL = audioPath.toNsUrl()!!)
            player = AVPlayer(playerItem)

            // 监听播放结束通知
            val observer = NSNotificationCenter.defaultCenter.addObserverForName(
                name = AVPlayerItemDidPlayToEndTimeNotification,
                `object` = playerItem,
                queue = null
            ) { _ ->
                playbackState = PlaybackState.Finished
                isPlaying = false
            }

            val errorObserver = NSNotificationCenter.defaultCenter.addObserverForName(
                AVPlayerItemFailedToPlayToEndTimeNotification,
                playerItem,
                null
            ) { notification ->
                val error = notification?.userInfo?.get(AVPlayerItemFailedToPlayToEndTimeErrorKey)
                logcat { "Playback error: $error" }
                playbackState = PlaybackState.Idle
                isPlaying = false
            }
            player?.play()
            playbackState = PlaybackState.Ready
            isPlaying = true

            onDispose {
                NSNotificationCenter.defaultCenter.removeObserver(observer)
                NSNotificationCenter.defaultCenter.removeObserver(errorObserver)
                player?.pause()
                player = null
            }
        } else {
            onDispose {}
        }
    }

    // 更新播放状态和进度
    LaunchedEffect(player, isPlaying) {
        if (player != null && isPlaying) {
            while (isPlaying && playbackState == PlaybackState.Ready) {
                player?.currentItem?.let { item ->
                    // 获取当前时间和总时长
                    val currentSeconds = CMTimeGetSeconds(player?.currentTime()!!)
                    val durationSeconds = CMTimeGetSeconds(item.duration())

                    if (durationSeconds > 0) {
                        progress = (currentSeconds / durationSeconds).toFloat()
                        onProgress(progress)
                    }
                }
                delay(100.milliseconds)
            }
        }
    }

    // 监听播放状态变化
    LaunchedEffect(player) {
        while (player != null) {
            isPlaying = player?.timeControlStatus == AVPlayerTimeControlStatusPlaying
            delay(200.milliseconds)
        }
    }

    PlaybackButton(
        modifier = modifier,
        playbackState = playbackState,
        progress = progress,
        isPlaying = isPlaying,
        onResetProgress = {
            // 使用 CMTimeMakeWithSeconds 而不是 CMTimeMake
            player?.seekToTime(CMTimeMakeWithSeconds(0.0, 600))
            progress = 0f
            playbackState = PlaybackState.Ready
        },
        onUpdatePlayWhenReady = { playWhenReady ->
            if (playWhenReady) {
                player?.play()
                isPlaying = true
            } else {
                player?.pause()
            }
        }
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

    SideEffect {
        logcat { "PlaybackButton: $playbackState, $progress, $isPlaying" }
    }
    IconButton(
        modifier = modifier,
        onClick = {
            when (playbackState) {
                PlaybackState.Ready -> {
                    onUpdatePlayWhenReady(!isPlaying)
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
