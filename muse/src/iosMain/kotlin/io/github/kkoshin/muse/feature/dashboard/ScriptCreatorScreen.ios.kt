@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.kkoshin.muse.platformbridge.DocumentPicker
import io.github.kkoshin.muse.platformbridge.LocalToaster
import io.github.kkoshin.muse.platformbridge.logcat
import io.github.kkoshin.muse.platformbridge.rememberDocumentPicker
import io.github.kkoshin.muse.platformbridge.toNsUrl
import io.github.kkoshin.muse.repo.MAX_TEXT_LENGTH
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfURL
import kotlin.uuid.ExperimentalUuidApi

// TODO: not working
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberPicker(onResult: (text: String) -> Unit): DocumentPicker {
    val scope = rememberCoroutineScope()
    val toaster = LocalToaster.current
    return rememberDocumentPicker { path ->
        logcat { "rememberPicker path: $path" }
        val didPickDocumentAtURL = path.toNsUrl()
        didPickDocumentAtURL?.let {
            try {
                it.startAccessingSecurityScopedResource()
                scope.launch(Dispatchers.IO) {
                    val text = NSString.stringWithContentsOfURL(
                        it,
                        encoding = NSUTF8StringEncoding,
                        error = null
                    ) ?: ""
                    if (text.length > MAX_TEXT_LENGTH) {
                        withContext(Dispatchers.Main) {
                            toaster.show("Text is too long, import failed.")
                        }
                    } else {
                        onResult(text)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                it.stopAccessingSecurityScopedResource()
            }
        }
    }
}