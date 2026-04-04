package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import okio.Path

actual class DocumentPicker {
    actual fun launch() {
        // No-op for Desktop
    }
}

@Composable
actual fun rememberDocumentPicker(
    mimeType: MimeType,
    onResult: (path: Path?) -> Unit,
): DocumentPicker = remember { DocumentPicker() }
