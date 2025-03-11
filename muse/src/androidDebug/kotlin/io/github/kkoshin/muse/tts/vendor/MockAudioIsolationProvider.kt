package io.github.kkoshin.muse.tts.vendor

import android.content.Context
import io.github.kkoshin.muse.debugLog
import io.github.kkoshin.muse.isolation.AudioIsolationProvider
import kotlinx.coroutines.delay
import okio.Source
import org.koin.java.KoinJavaComponent.inject
import java.io.ByteArrayOutputStream

class MockAudioIsolationProvider : AudioIsolationProvider {
    private val appContext: Context by inject(Context::class.java)

    override suspend fun removeBackgroundNoise(
        audio: Source,
        audioName: String
    ): Result<ByteArray> {
        debugLog { "start remove background noise: $audioName" }
        delay(1000)
        appContext.assets.open("english.mp3").use { inputStream ->
            val outputStream = ByteArrayOutputStream()
            inputStream.copyTo(outputStream)
            return Result.success(outputStream.toByteArray())
        }
    }
}
