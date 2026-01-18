package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enable: Boolean, onBack: () -> Unit) =
    androidx.activity.compose.BackHandler(enabled = enable) { onBack() }