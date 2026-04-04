package io.github.kkoshin.muse.feature.editor

import java.text.DecimalFormat

actual fun formatDecimal(value: Float, decimalPlaces: Int): String {
    val pattern = if (decimalPlaces > 0) {
        "0." + "0".repeat(decimalPlaces)
    } else {
        "0"
    }
    return DecimalFormat(pattern).format(value)
}
