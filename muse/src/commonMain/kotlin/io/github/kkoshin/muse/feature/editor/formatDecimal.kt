package io.github.kkoshin.muse.feature.editor

/**
 * Format a double value with the given number of decimal places.
 * E.g. formatDecimal(1.23456, 2) -> "1.23"
 */
expect fun formatDecimal(value: Float, decimalPlaces: Int = 1): String