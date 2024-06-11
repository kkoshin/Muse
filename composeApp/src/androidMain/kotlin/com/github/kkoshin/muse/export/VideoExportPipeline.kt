package com.github.kkoshin.muse.export

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.Transformer
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import com.github.foodiestudio.sugar.storage.filesystem.toOkioPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okio.Path.Companion.toPath
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun rememberVideoExportPipeline(
    context: Context,
    input: List<Uri>,
    effects: Effects,
): VideoExportPipeline =
    remember(effects) {
        val mainTrack =
            // TODO(Jiangc): replace cover
            EditedMediaItem.Builder(MediaItem.fromUri("asset:///ic_logo.png"))
                .setDurationUs(5.seconds.inWholeMicroseconds)
                .setFrameRate(24)
                .setEffects(Effects(emptyList(), effects.videoEffects))
                .build()

        val oneSilence =
            EditedMediaItem.Builder(MediaItem.fromUri("asset:///silence_mono_1s.wav"))
                .build()

        var bgm = mutableListOf<EditedMediaItem>()
        input.forEach {
            bgm.add(
                EditedMediaItem.Builder(MediaItem.fromUri(it))
                    .setEffects(Effects(effects.audioProcessors, emptyList()))
                    .build(),
            )
            bgm.add(oneSilence)
        }

        val videoItem = EditedMediaItemSequence(mainTrack)

        val composition = Composition.Builder(videoItem, EditedMediaItemSequence(bgm)).build()

        VideoExportPipeline(context.applicationContext, composition)
    }

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class VideoExportPipeline(
    applicationContext: Context,
    private val input: Composition,
) : ExportPipeline<ExportResult> {
    private val _progress: MutableStateFlow<Int> = MutableStateFlow(-1)
    override val progress: StateFlow<Int> = _progress

    private val builder = Transformer.Builder(applicationContext)

    @OptIn(ExperimentalSugarApi::class)
    private val appFileHelper = AppFileHelper(applicationContext)

    // 导出中的临时文件
    private val exportingCache: File = applicationContext.createExternalCacheFile("exporting.mp4")

    private val transformer = builder
        .setVideoMimeType(MimeTypes.VIDEO_H265)
        .build()

    private suspend fun updateProgress() {
        val progressHolder = ProgressHolder()
        var transformState: Int
        do {
            transformState = transformer.getProgress(progressHolder)
            _progress.value = if (transformState != Transformer.PROGRESS_STATE_NOT_STARTED) {
                progressHolder.progress
            } else {
                -1
            }
            delay(500)
        } while (transformState != Transformer.PROGRESS_STATE_NOT_STARTED)
    }

    @OptIn(ExperimentalSugarApi::class)
    override suspend fun start(target: Uri): Result<ExportResult> =
        coroutineScope {
            launch {
                updateProgress()
            }
            startExport().onSuccess {
                ensureActive()
                withContext(Dispatchers.IO) {
                    appFileHelper.fileSystem.copy(
                        exportingCache.absolutePath.toPath(),
                        target.toOkioPath(),
                    )
                }
            }
        }

    private suspend fun startExport(): Result<ExportResult> =
        suspendCancellableCoroutine {
            val listener = object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    it.resume(Result.success(exportResult))
                }

                override fun onError(
                    composition: Composition,
                    exportResult: ExportResult,
                    exportException: ExportException,
                ) {
                    it.resume(Result.failure(exportException))
                }
            }
            transformer.addListener(listener)
            transformer.start(input, exportingCache.absolutePath)
        }

    override fun cancel() {
        transformer.cancel()
        _progress.value = -1
    }
}

@Throws(IOException::class)
internal fun Context.createExternalCacheFile(fileName: String): File {
    val file = File(externalCacheDir, fileName)
    check(!(file.exists() && !file.delete())) { "Could not delete the previous export output file" }
    check(file.createNewFile()) { "Could not create the export output file" }
    return file
}