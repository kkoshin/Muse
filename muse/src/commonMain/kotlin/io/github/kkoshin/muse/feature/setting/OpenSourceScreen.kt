package io.github.kkoshin.muse.feature.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kkoshin.muse.Route
import kotlinx.serialization.Serializable

@Serializable
object OpenSourceArgs : Route

@Composable
expect fun OpenSourceScreen(modifier: Modifier = Modifier, onOpenURL: (String) -> Unit = {})