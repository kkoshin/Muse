package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 应用导航栏的返回按钮
 * 仅在 iOS 上处理 onBack 回调
 */
@Composable
expect fun AppBackButton(modifier: Modifier = Modifier, onBack: () -> Unit = {})