package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import okio.Path
import platform.Foundation.NSURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypePlainText
import platform.UniformTypeIdentifiers.UTTypeText
import platform.darwin.NSObject

actual class DocumentPicker(
    private val uiViewController: UIViewController,
    private val viewController: UIDocumentPickerViewController
) {
    actual fun launch() {
        uiViewController.presentViewController(viewController, true, null)
    }
}

// TODO: 待验证
@Composable
actual fun rememberDocumentPicker(onResult: (path: Path?) -> Unit): DocumentPicker {
    val uiViewController = LocalUIViewController.current
    return remember {
        val viewController = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTTypeText, UTTypePlainText),
            false
        ).apply {
            delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                override fun documentPicker(
                    controller: UIDocumentPickerViewController,
                    didPickDocumentsAtURLs: List<*>
                ) {
                    onResult((didPickDocumentsAtURLs as List<NSURL>).firstOrNull()?.toOkioPath())
                }

                override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                    onResult(null)
                }
            }
        }
        DocumentPicker(uiViewController, viewController)
    }
}