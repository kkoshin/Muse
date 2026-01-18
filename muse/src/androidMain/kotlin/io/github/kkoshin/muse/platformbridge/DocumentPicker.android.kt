package io.github.kkoshin.muse.platformbridge

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.foodiestudio.sugar.storage.filesystem.toOkioPath
import okio.Path

actual class DocumentPicker(
    private val mimeType: MimeType,
    private val launcher: ManagedActivityResultLauncher<String, Uri?>
) {

    actual fun launch() {
        val type = when (mimeType) {
            MimeType.Audio -> "audio/*"
            MimeType.Text -> "text/*"
        }
        launcher.launch(type)
    }
}

@Composable
actual fun rememberDocumentPicker(
    mimeType: MimeType,
    onResult: (path: Path?) -> Unit
): DocumentPicker {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onResult(uri?.toOkioPath())
    }
    return remember {
        DocumentPicker(mimeType, launcher)
    }
}