package io.github.kkoshin.muse.feature.dashboard

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.kkoshin.muse.platformbridge.DocumentPicker
import io.github.kkoshin.muse.platformbridge.LocalToaster
import io.github.kkoshin.muse.platformbridge.MimeType
import io.github.kkoshin.muse.platformbridge.rememberDocumentPicker
import io.github.kkoshin.muse.platformbridge.toUri
import io.github.kkoshin.muse.repo.MAX_TEXT_LENGTH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import okio.use

@Composable
actual fun rememberPicker(onResult: (text: String) -> Unit): DocumentPicker {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val toaster = LocalToaster.current
    return rememberDocumentPicker(MimeType.Text) { path ->
        val uri = path?.toUri()
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val text = readTextContent(context, uri, false)
                if (text.length > MAX_TEXT_LENGTH) {
                    withContext(Dispatchers.Main) {
                        toaster.show("Text is too long, import failed.")
                    }
                } else {
                    onResult(text)
                }
            }
        }
    }
}

fun readTextContent(
    context: Context,
    contentUri: Uri,
    formatEnabled: Boolean,
): String =
    context.contentResolver
        .openInputStream(contentUri)
        ?.source()
        ?.buffer()
        ?.use {
            it.readString(Charsets.UTF_8)
        }?.let {
            if (formatEnabled) {
                it.replace("\n", " ")
            } else {
                it
            }
        }
        ?: ""