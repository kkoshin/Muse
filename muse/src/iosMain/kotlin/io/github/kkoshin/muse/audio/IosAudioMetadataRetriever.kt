package io.github.kkoshin.muse.audio

import io.github.kkoshin.muse.platformbridge.toNsUrl
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import me.saket.bytesize.decimalBytes
import okio.FileSystem
import okio.Path
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.duration
import platform.CoreMedia.CMTimeGetSeconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalForeignApi::class)
class IosAudioMetadataRetriever : AudioMetadataRetriever {
    override suspend fun getAudioMetadata(path: Path): AudioMetadata? = withContext(Dispatchers.IO) {
        runCatching {
            val nsUrl = path.toNsUrl() ?: return@runCatching null
            val asset = AVURLAsset(uRL = nsUrl, options = null)
            val durationSeconds = CMTimeGetSeconds(asset.duration)
            
            val metadata = FileSystem.SYSTEM.metadata(path)
            
            AudioMetadata(
                duration = durationSeconds.seconds,
                displayName = path.name,
                size = metadata.size!!.decimalBytes
            )
        }.getOrNull()
    }
}
