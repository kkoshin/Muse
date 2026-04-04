package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Desktop
import java.net.URI

class JvmPlatformSpecificInfo : PlatformSpecificInfo {
    override val versionName: String = "1.0.0-desktop"
    override val versionCode: Int = 1
    override val exportFolderPath: String = System.getProperty("java.io.tmpdir")

    override fun onOpenURL(url: String) {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(url))
        }
    }
}

@Composable
actual fun rememberPlatformSpecificInfo(): PlatformSpecificInfo = remember {
    JvmPlatformSpecificInfo()
}
