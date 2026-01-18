package io.github.kkoshin.muse.feature.editor

import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.currentLocale

actual fun formatDecimal(value: Float, decimalPlaces: Int): String {
    val number = NSNumber(value)
    val formatter = NSNumberFormatter()

    formatter.apply {
        numberStyle = 1u // NSNumberFormatterDecimalStyle
        locale = NSLocale.currentLocale
        usesGroupingSeparator = false
        minimumFractionDigits = decimalPlaces.toULong()
        maximumFractionDigits = decimalPlaces.toULong()

        // 当小数位数为0时隐藏小数点
        alwaysShowsDecimalSeparator = decimalPlaces > 0
    }

    return formatter.stringFromNumber(number) ?: value.toString()
}