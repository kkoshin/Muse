@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.kkoshin.muse.platformbridge.DocumentPicker
import io.github.kkoshin.muse.platformbridge.LocalToaster
import io.github.kkoshin.muse.platformbridge.rememberDocumentPicker
import io.github.kkoshin.muse.repo.MAX_TEXT_LENGTH
import io.github.kkoshin.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi

@Composable
actual fun rememberPicker(onResult: (text: String) -> Unit): DocumentPicker {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val toaster = LocalToaster.current
    return rememberDocumentPicker { path ->
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