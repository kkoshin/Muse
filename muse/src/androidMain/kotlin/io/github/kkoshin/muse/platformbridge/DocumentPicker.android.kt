package io.github.kkoshin.muse.platformbridge

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.foodiestudio.sugar.storage.filesystem.toOkioPath
import okio.Path

actual class DocumentPicker(private val launcher: ManagedActivityResultLauncher<String, Uri?>) {

    actual fun launch() {
        launcher.launch("text/*")
    }
}

@Composable
actual fun rememberDocumentPicker(onResult: (path: Path?) -> Unit): DocumentPicker {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onResult(uri?.toOkioPath())
    }
    return remember {
        DocumentPicker(launcher)
    }
}