package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.platformbridge.AppBackButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar

@Composable
fun ScreenScaffold(
    title: String,
    navigationIcon: @Composable () -> Unit = {
        AppBackButton(Modifier.padding(start = 16.dp))
    },
    content: @Composable (PaddingValues) -> Unit,
    scrollBehavior: ScrollBehavior? = null,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = title, navigationIcon = navigationIcon, scrollBehavior = scrollBehavior)
        },
    ) { paddingValues ->
        content(paddingValues)
    }
}