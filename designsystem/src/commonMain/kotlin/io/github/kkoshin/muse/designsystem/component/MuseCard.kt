package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import io.github.kkoshin.muse.designsystem.theme.MuseElevation

/**
 * Muse Design System — Card.
 *
 * Thin wrapper over M3 [Card] with design-system token defaults:
 * - `elevation` = [MuseElevation.low] (2 dp, matching the M2 Card default).
 *
 * Colors resolve through [CardDefaults.cardColors] from [MuseTheme]'s color scheme.
 */
@Composable
fun MuseCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = MuseElevation.low),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content,
    )
}
