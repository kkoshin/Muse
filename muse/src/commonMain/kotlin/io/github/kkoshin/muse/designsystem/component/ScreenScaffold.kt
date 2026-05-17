package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kkoshin.muse.platformbridge.AppBackButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar

@Composable
fun ScreenScaffold(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: @Composable () -> Unit = {
        AppBackButton()
    },
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    scrollBehavior: ScrollBehavior? = MiuixScrollBehavior(),
    content: @Composable (PaddingValues, ScrollBehavior?) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton
    ) { paddingValues ->
        content(paddingValues, scrollBehavior)
    }
}