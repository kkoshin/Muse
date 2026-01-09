package io.github.kkoshin.muse.platformbridge

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.kkoshin.muse.feature.generated.resources.Res
import io.github.kkoshin.muse.repo.MusePathManager
import okio.Path.Companion.toOkioPath

class AndroidPlatformSpecificInfo(private val context: Context) : PlatformSpecificInfo {
    override val versionName: String
        get() = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown"
    
    override val versionCode: Int
        get() = context.packageManager.getPackageInfo(context.packageName, 0).versionCode

    override val exportFolderPath: String
        get() = Environment
            .getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS,
            ).toOkioPath()
            .resolve("../${MusePathManager.getExportRelativePath()}", true)
            .toString()

    override fun onOpenURL(url: String) {
        val intent = CustomTabsIntent
            .Builder()
            .build()
        intent.launchUrl(context, Uri.parse(url))
    }
}

@Composable
actual fun rememberPlatformSpecificInfo(): PlatformSpecificInfo {
    val context = LocalContext.current
    return AndroidPlatformSpecificInfo(context)
}
