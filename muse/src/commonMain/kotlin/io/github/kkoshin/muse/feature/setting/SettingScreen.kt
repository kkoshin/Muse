package io.github.kkoshin.muse.feature.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
object SettingArgs

@Composable
expect fun SettingScreen(
    versionName: String,
    versionCode: Int,
    modifier: Modifier = Modifier,
    onLaunchVoiceScreen: (Set<String>) -> Unit,
    onLaunchOpenSourceScreen: () -> Unit,
)
