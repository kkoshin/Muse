package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import okio.Path
import platform.Foundation.NSURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypeAudio
import platform.UniformTypeIdentifiers.UTTypePlainText
import platform.UniformTypeIdentifiers.UTTypeText
import platform.darwin.NSObject

actual class DocumentPicker(
    private val uiViewController: UIViewController,
    private val viewController: UIDocumentPickerViewController,
    private val keepDelegate: Any? = null // 强引用持有 delegate，防止被系统回收
) {
    actual fun launch() {
        uiViewController.presentViewController(viewController, true, null)
    }
}

@Composable
actual fun rememberDocumentPicker(
    mimeType: MimeType,
    onResult: (path: Path?) -> Unit
): DocumentPicker {
    val uiViewController = LocalUIViewController.current
    // 增加 key1 = mimeType, key2 = onResult，确保参数变化时重新创建
    return remember(mimeType, onResult) {
        val forOpeningContentTypes = when (mimeType) {
            MimeType.Audio -> listOf(UTTypeAudio)
            MimeType.Text -> listOf(UTTypeText, UTTypePlainText)
        }
        
        val pickerDelegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                // 安全转型，并使用 toOkioPath 转换
                val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                onResult(url?.toOkioPath())
            }

            override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                onResult(null)
            }
        }

        // 使用 asCopy = true，系统会将文件复制到应用沙盒的 tmp 目录，避免 Security Scope 权限问题
        val viewController = UIDocumentPickerViewController(
            forOpeningContentTypes = forOpeningContentTypes,
            asCopy = true
        ).apply {
            delegate = pickerDelegate
        }
        DocumentPicker(uiViewController, viewController, pickerDelegate)
    }
}