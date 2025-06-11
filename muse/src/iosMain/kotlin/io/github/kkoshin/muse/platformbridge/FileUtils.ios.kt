package io.github.kkoshin.muse.platformbridge

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import okio.Path
import okio.Sink
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.popoverPresentationController

/**
 * 分享音频文件
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun shareAudioFile(path: Path): Result<Unit> = runCatching {
    autoreleasepool {
        val fileUrl = path.toNsUrl()

        val windows = UIApplication.sharedApplication.windows as List<platform.UIKit.UIWindow>

        // 获取当前活动的视图控制器
        val rootViewController = windows.firstOrNull {
            it.isKeyWindow()
        }?.rootViewController

        // 创建分享控制器
        val activityViewController = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null
        ).apply {
            // iPad 设备需要设置弹出位置（避免崩溃）
            if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
                popoverPresentationController?.apply {
                    // 使用屏幕中心作为弹出位置
                    sourceView = rootViewController?.view

                    // 计算屏幕中心点
                    val screenBounds = UIScreen.mainScreen.bounds
                    val center = CGRectMake(
                        screenBounds.useContents { origin.x } + screenBounds.useContents { size.width } / 2,
                        screenBounds.useContents { origin.y } + screenBounds.useContents { size.height } / 2,
                        1.0,  // 最小尺寸
                        1.0
                    )
                    sourceRect = center
                    permittedArrowDirections = 0UL // 不显示箭头
                }
            }
        }

        // 显示分享控制器
        rootViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}

actual fun openFile(path: Path): Result<Unit> {
    TODO("Not yet implemented")
}

actual fun createCacheFile(fileName: String, sensitive: Boolean): Path {
    TODO("Not yet implemented")
}

actual fun Path.toSink(): Sink = SystemFileSystem.sink(this)