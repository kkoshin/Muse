package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable

interface PlatformSpecificInfo {
    val versionName: String
    val versionCode: Int
    val exportFolderPath: String
    fun onOpenURL(url: String)
}

@Composable
expect fun rememberPlatformSpecificInfo(): PlatformSpecificInfo
