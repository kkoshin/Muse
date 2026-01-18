package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.staticCompositionLocalOf

interface ToastManager {
    fun show(message: String?)
}

val LocalToaster = staticCompositionLocalOf<ToastManager> {
    TODO("Not yet implemented")
}