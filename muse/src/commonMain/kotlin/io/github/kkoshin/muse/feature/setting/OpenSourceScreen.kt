package io.github.kkoshin.muse.feature.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

@Serializable
object OpenSourceArgs

@Composable
expect fun OpenSourceScreen(modifier: Modifier = Modifier, onOpenURL: (String) -> Unit = {})