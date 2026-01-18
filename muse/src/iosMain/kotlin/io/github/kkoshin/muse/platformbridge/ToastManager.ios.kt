package io.github.kkoshin.muse.platformbridge

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.*

class IosToastManager : ToastManager {
    @OptIn(ExperimentalForeignApi::class)
    override fun show(message: String?) {
        if (message == null) return

        val window = UIApplication.sharedApplication.keyWindow 
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
            ?: return

        val toastView = UIView().apply {
            backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.7)
            layer.cornerRadius = 10.0
            clipsToBounds = true
            alpha = 0.0
        }

        val label = UILabel().apply {
            text = message
            textColor = UIColor.whiteColor
            textAlignment = NSTextAlignmentCenter
            font = UIFont.systemFontOfSize(14.0)
            numberOfLines = 0
        }

        toastView.addSubview(label)
        window.addSubview(toastView)

        // Calculate size
        val screenBounds = UIScreen.mainScreen.bounds
        val screenWidth = screenBounds.useContents { size.width }
        val screenHeight = screenBounds.useContents { size.height }
        val horizontalPadding = 20.0
        val verticalPadding = 10.0
        val maxWidth = screenWidth - 60.0
        
        // Use intrinsic size or manual calculation
        val expectedSize = label.sizeThatFits(CGSizeMake(maxWidth, screenHeight))
        val width = (expectedSize.useContents { width } + horizontalPadding * 2).coerceAtMost(maxWidth)
        val height = expectedSize.useContents { height } + verticalPadding * 2

        toastView.setFrame(
            CGRectMake(
                (screenWidth - width) / 2.0,
                screenHeight - height - 100.0,
                width,
                height
            )
        )
        label.setFrame(CGRectMake(horizontalPadding, verticalPadding, width - horizontalPadding * 2, height - verticalPadding * 2))

        // Animation
        UIView.animateWithDuration(0.3, animations = {
            toastView.alpha = 1.0
        }) { _ ->
            UIView.animateWithDuration(0.3, delay = 2.0, options = 0UL, animations = {
                toastView.alpha = 0.0
            }) { _ ->
                toastView.removeFromSuperview()
            }
        }
    }
}
