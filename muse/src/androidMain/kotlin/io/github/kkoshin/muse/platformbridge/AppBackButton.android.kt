package io.github.kkoshin.muse.platformbridge

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable

@Composable
actual fun AppBackButton() {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    IconButton(
        onClick = {
        backPressedDispatcher?.onBackPressed()
    }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
    }
}