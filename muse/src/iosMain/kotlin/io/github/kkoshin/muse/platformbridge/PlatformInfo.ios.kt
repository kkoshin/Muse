package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable
import androidx.compose.ui.uikit.LocalUIViewController
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController

class IosPlatformSpecificInfo(private val viewController: platform.UIKit.UIViewController) : PlatformSpecificInfo {
    override val versionName: String
        get() = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString")?.toString() ?: "Unknown"
    
    override val versionCode: Int
        get() = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion")?.toString()?.toInt() ?: 0

    override val exportFolderPath: String
        get() = "TODO"

    override fun onOpenURL(url: String) {
        viewController.presentViewController(
            SFSafariViewController(NSURL.URLWithString(url)!!),
            animated = true,
            null
        )
    }
}

@Composable
actual fun rememberPlatformSpecificInfo(): PlatformSpecificInfo {
    val viewController = LocalUIViewController.current
    return IosPlatformSpecificInfo(viewController)
}
