package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable

// Android only
// make three button style navigation transparent
@Composable
expect fun NavigationBarContrastEnforcedOnAndroid(enabled: Boolean, default: Boolean)