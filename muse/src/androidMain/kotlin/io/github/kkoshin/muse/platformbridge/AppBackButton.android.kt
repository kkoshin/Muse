package io.github.kkoshin.muse.platformbridge

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back

@Composable
actual fun AppBackButton(modifier: Modifier, onBack: () -> Unit) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    IconButton(
        modifier = modifier,
        onClick = {
            backPressedDispatcher?.onBackPressed()
        }) {
        Icon(MiuixIcons.Back, contentDescription = null)
    }
}