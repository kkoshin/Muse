package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable
import okio.Path


enum class MimeType {
    Audio,
    Text
}

expect class DocumentPicker {
    fun launch()
}

@Composable
expect fun rememberDocumentPicker(
    mimeType: MimeType,
    onResult: (path: Path?) -> Unit,
): DocumentPicker