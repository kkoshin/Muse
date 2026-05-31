package io.github.kkoshin.muse.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import io.github.kkoshin.muse.platformbridge.BackHandler
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Metadata key used to mark a [NavEntry] as a bottom sheet destination.
 */
object BottomSheetKey

/**
 * Creates metadata map indicating this entry should be displayed as a bottom sheet.
 */
fun bottomSheetMetadata(): Map<String, Any> = mapOf(BottomSheetKey.toString() to Unit)

/**
 * A [SceneStrategy] that displays entries with [BottomSheetKey] metadata
 * within a custom bottom sheet overlay.
 *
 * Usage:
 * ```
 * val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }
 * NavDisplay(
 *     backStack = backStack,
 *     onBack = { backStack.removeLastOrNull() },
 *     sceneStrategy = bottomSheetStrategy.then(SinglePaneSceneStrategy()),
 *     entryProvider = { key ->
 *         when (key) {
 *             is DashboardArgs -> NavEntry(key) { DashboardScreen(...) }
 *             is ExportConfigSheetArgs -> NavEntry(key, metadata = bottomSheetMetadata()) {
 *                 ExportConfigSheet(...)
 *             }
 *             // ...
 *         }
 *     }
 * )
 * ```
 */
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(
        entries: List<NavEntry<T>>,
    ): Scene<T>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val isBottomSheet = lastEntry.metadata[BottomSheetKey.toString()] != null
        if (!isBottomSheet) return null

        @Suppress("UNCHECKED_CAST")
        return BottomSheetScene(
            key = lastEntry.contentKey as T,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            entry = lastEntry,
            onBack = onBack,
        )
    }
}

/**
 * An [OverlayScene] that renders an [entry] within a bottom sheet
 * that slides up from the bottom of the screen.
 */
data class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        BottomSheetContent(
            onDismiss = onBack,
        ) {
            entry.Content()
        }
    }
}

@Composable
private fun BottomSheetContent(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val offsetY = remember { Animatable(1f) } // 1f = hidden (below screen), 0f = fully shown
    val sheetHeightPx = with(density) { 400.dp.toPx() }
    var isDismissing by remember { mutableStateOf(false) }

    // Animate in on first composition
    LaunchedEffect(Unit) {
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300),
        )
    }

    fun dismiss() {
        if (isDismissing) return
        isDismissing = true
        scope.launch {
            offsetY.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200),
            )
            onDismiss()
        }
    }

    // Intercept system back press to animate the sheet out before dismissing
    BackHandler {
        dismiss()
    }

    Box(Modifier.fillMaxSize()) {
        // Scrim background — fades in/out with sheet position
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f * (1f - offsetY.value)))
                .clickable { dismiss() },
        )

        // Bottom sheet panel
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .offset { IntOffset(0, (offsetY.value * sheetHeightPx).roundToInt()) }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            // Snap to closest state: shown (0) or hidden (1)
                            scope.launch {
                                val target = if (offsetY.value > 0.5f) 1f else 0f
                                if (target == 1f) {
                                    offsetY.animateTo(1f, tween(200))
                                    onDismiss()
                                } else {
                                    offsetY.animateTo(0f, tween(200))
                                }
                            }
                        },
                    ) { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            val newValue = (offsetY.value + dragAmount / sheetHeightPx)
                                .coerceIn(0f, 1f)
                            offsetY.snapTo(newValue)
                        }
                    }
                },
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            elevation = 8.dp,
        ) {
            Box(modifier = Modifier.navigationBarsPadding()) {
                content()
            }
        }
    }
}
