package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable

/**
 * 应用导航栏的返回按钮
 * 仅在 iOS 上处理 onBack 回调
 */
@Composable
expect fun AppBackButton(onBack: () -> Unit = {})