package io.github.kkoshin.muse.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MuseColor.primary,
    onPrimary = MuseColor.onPrimary,
    primaryContainer = MuseColor.primaryContainer,
    onPrimaryContainer = MuseColor.onPrimaryContainer,
    secondary = MuseColor.secondary,
    onSecondary = MuseColor.onSecondary,
    secondaryContainer = MuseColor.secondaryContainer,
    onSecondaryContainer = MuseColor.onSecondaryContainer,
    background = MuseColor.background,
    onBackground = MuseColor.onBackground,
    surface = MuseColor.surface,
    onSurface = MuseColor.onSurface,
    surfaceVariant = MuseColor.surfaceVariant,
    onSurfaceVariant = MuseColor.onSurfaceVariant,
    error = MuseColor.error,
    onError = MuseColor.onError,
    outline = MuseColor.outline,
    outlineVariant = MuseColor.outlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = MuseColor.darkPrimary,
    onPrimary = MuseColor.darkOnPrimary,
    primaryContainer = MuseColor.darkPrimaryContainer,
    onPrimaryContainer = MuseColor.darkOnPrimaryContainer,
    secondary = MuseColor.darkSecondary,
    onSecondary = MuseColor.darkOnSecondary,
    secondaryContainer = MuseColor.darkSecondaryContainer,
    onSecondaryContainer = MuseColor.darkOnSecondaryContainer,
    background = MuseColor.darkBackground,
    onBackground = MuseColor.darkOnBackground,
    surface = MuseColor.darkSurface,
    onSurface = MuseColor.darkOnSurface,
    surfaceVariant = MuseColor.darkSurfaceVariant,
    onSurfaceVariant = MuseColor.darkOnSurfaceVariant,
    error = MuseColor.darkError,
    onError = MuseColor.darkOnError,
    outline = MuseColor.darkOutline,
    outlineVariant = MuseColor.darkOutlineVariant,
)

/**
 * Muse Design System theme.
 *
 * Wraps Material 3 [MaterialTheme] with Muse color scheme, typography, and shapes.
 * Business code should use this as the root composable instead of [MaterialTheme] directly.
 */
@Composable
fun MuseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MuseTypography,
        shapes = MuseShapes,
        content = content,
    )
}
