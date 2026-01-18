package io.github.kkoshin.muse.feature.editor

import java.util.Locale

actual fun formatDecimal(value: Float, decimalPlaces: Int): String {
    return String.format(Locale.getDefault(), "%.${decimalPlaces}f", value)
}