package io.github.kkoshin.muse.feature.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun SettingScreen(
    versionName: String,
    versionCode: Int,
    modifier: Modifier,
    onLaunchVoiceScreen: (Set<String>) -> Unit,
    onLaunchOpenSourceScreen: () -> Unit
) {
    // TODO: Implement SettingScreen
}