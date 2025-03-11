package io.github.kkoshin.muse.audio

import android.content.Context
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * MediaMetadataRetriever 的包装类
 */
class MediaMetadataRetrieverHelper(private val retriever: MediaMetadataRetriever) : AutoCloseable {

    /**
     * 没有取到的话返回 Duration.ZERO
     */
    val duration: Duration
        get() = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLong()?.milliseconds ?: Duration.ZERO

    val mimeType: String?
        get() = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)

    val title: String?
        get() = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)

    /**
     * 值为 90/-90/180/270 等
     */
    val rotation: Int?
        get() = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            ?.toInt()

    val width: Int?
        get() = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()

    val height: Int?
        get() = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()

    val bitrate: Int?
        get() = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toInt()

    val videoFrameCount: Int?
        @RequiresApi(Build.VERSION_CODES.P)
        get() = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
            ?.toInt()

    val captureFrameRate: Float?
        get() {
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
                ?.toFloatOrNull()
        }

    val sampleRate: Int?
        @RequiresApi(Build.VERSION_CODES.S)
        get() {
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
                ?.toIntOrNull()
        }

    val colorStandard: Int?
        @RequiresApi(Build.VERSION_CODES.R)
        get() {
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_STANDARD)
                ?.toIntOrNull()
        }

    val colorRange: Int?
        @RequiresApi(Build.VERSION_CODES.R)
        get() {
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_RANGE)
                ?.toIntOrNull()
        }

    val colorTransfer: Int?
        @RequiresApi(Build.VERSION_CODES.R)
        get() {
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_TRANSFER)
                ?.toIntOrNull()
        }

    override fun close() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            retriever.close()
        } else {
            retriever.release()
        }
    }
}

/**
 * 记得使用 use 方式关闭
 */
fun MediaMetadataRetrieverHelper(context: Context, uri: Uri): MediaMetadataRetrieverHelper =
    MediaMetadataRetrieverHelper(
        MediaMetadataRetriever().apply {
            check(uri.scheme.isNullOrEmpty().not()) {
                "uri scheme must not be empty"
            }
            setDataSource(context, uri)
        }
    )

/**
 * 记得使用 use 方式关闭
 */
fun MediaMetadataRetrieverHelper(filePath: String): MediaMetadataRetrieverHelper {
    check(File(filePath).exists()) {
        "File $filePath not exists"
    }
    return MediaMetadataRetrieverHelper(
        MediaMetadataRetriever().apply {
            setDataSource(filePath)
        }
    )
}

val MediaMetadataRetrieverHelper.colorStandardString: String?
    @RequiresApi(Build.VERSION_CODES.R)
    get() {
        return colorStandard?.let {
            when (it) {
                MediaFormat.COLOR_STANDARD_BT709 -> "BT709"
                MediaFormat.COLOR_STANDARD_BT2020 -> "BT2020"
                MediaFormat.COLOR_STANDARD_BT601_PAL -> "BT601PAL"
                MediaFormat.COLOR_STANDARD_BT601_NTSC -> "BT601NTSC"
                else -> null
            }
        }
    }

val MediaMetadataRetrieverHelper.colorRangeString: String?
    @RequiresApi(Build.VERSION_CODES.R)
    get() {
        return colorRange?.let {
            when (it) {
                MediaFormat.COLOR_RANGE_FULL -> "Full"
                MediaFormat.COLOR_RANGE_LIMITED -> "Limited"
                else -> null
            }
        }
    }

val MediaMetadataRetrieverHelper.colorTransferString: String?
    @RequiresApi(Build.VERSION_CODES.R)
    get() {
        return colorTransfer?.let {
            when (it) {
                MediaFormat.COLOR_TRANSFER_HLG -> "HLG/HDR"
                MediaFormat.COLOR_TRANSFER_SDR_VIDEO -> "SDR"
                MediaFormat.COLOR_TRANSFER_LINEAR -> "Linear/SDR"
                MediaFormat.COLOR_TRANSFER_ST2084 -> "ST2084/HDR"
                else -> null
            }
        }
    }

val MediaMetadataRetrieverHelper.fps: Int?
    get() {
        val frameCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            videoFrameCount
        } else {
            null
        }
        val result = if (duration == Duration.ZERO || frameCount == null) {
            frameCount
        } else {
            (frameCount / duration.inWholeSeconds.toDouble()).toInt()
        }
        return result?.takeIf { value -> value != 0 } ?: captureFrameRate?.toInt()
    }
