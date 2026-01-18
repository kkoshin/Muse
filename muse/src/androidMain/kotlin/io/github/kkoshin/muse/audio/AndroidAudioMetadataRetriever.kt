package io.github.kkoshin.muse.audio

import android.content.Context
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import com.github.foodiestudio.sugar.storage.filesystem.displayName
import io.github.kkoshin.muse.platformbridge.toUri
import me.saket.bytesize.decimalBytes
import okio.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalSugarApi::class)
class AndroidAudioMetadataRetriever(
    private val context: Context,
    private val appFileHelper: AppFileHelper
) : AudioMetadataRetriever {
    override suspend fun getAudioMetadata(path: Path): AudioMetadata? = withContext(Dispatchers.IO) {
        runCatching {
            MediaMetadataRetrieverHelper(context, path.toUri()).use { helper ->
                val metadata = appFileHelper.fileSystem.metadata(path)
                AudioMetadata(
                    duration = helper.duration,
                    displayName = metadata.displayName,
                    size = metadata.size!!.decimalBytes
                )
            }
        }.getOrNull()
    }
}
