package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable

@Composable
actual fun AppBackButton(onBack: () -> Unit) {
    // No-op for Desktop (or add a simple Back icon if needed)
}
