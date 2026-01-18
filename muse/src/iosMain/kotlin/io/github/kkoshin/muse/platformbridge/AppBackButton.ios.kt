package io.github.kkoshin.muse.platformbridge

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import io.github.kkoshin.muse.LocalNavigationController

@Composable
actual fun AppBackButton(onBack: () -> Unit) {
    val localNavController = LocalNavigationController.current
    IconButton(onClick = {
        onBack()
        localNavController.navigateUp()
    }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
    }
}