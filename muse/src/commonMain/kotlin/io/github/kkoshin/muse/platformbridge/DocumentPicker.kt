package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable
import okio.Path

expect class DocumentPicker {
    fun launch()
}

@Composable
expect fun rememberDocumentPicker(
    onResult: (path: Path?) -> Unit,
): DocumentPicker