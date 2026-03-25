package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kkoshin.muse.LocalNavigationController
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back

@Composable
actual fun AppBackButton(modifier: Modifier, onBack: () -> Unit) {
    val localNavController = LocalNavigationController.current
    IconButton(
        modifier = modifier,
        onClick = {
            onBack()
            localNavController.navigateUp()
        }) {
        Icon(MiuixIcons.Back, contentDescription = null)
    }
}