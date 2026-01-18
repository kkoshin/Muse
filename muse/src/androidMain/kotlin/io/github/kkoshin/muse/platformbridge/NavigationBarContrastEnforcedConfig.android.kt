package io.github.kkoshin.muse.platformbridge

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun NavigationBarContrastEnforcedOnAndroid(enabled: Boolean, default: Boolean) {
    val context = LocalContext.current
    DisposableEffect(enabled) {
        // make three button style navigation transparent
        (context as Activity).window.isNavigationBarContrastEnforced = enabled
        onDispose {
            if (context.window.isNavigationBarContrastEnforced) {
                context.window.isNavigationBarContrastEnforced = false
            }
        }
    }
}