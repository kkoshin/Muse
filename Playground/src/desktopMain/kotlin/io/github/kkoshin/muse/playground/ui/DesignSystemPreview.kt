@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.kkoshin.muse.playground.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.designsystem.component.*
import io.github.kkoshin.muse.designsystem.foundation.musePadding
import io.github.kkoshin.muse.designsystem.theme.MuseSpacing
import io.github.kkoshin.muse.designsystem.theme.MuseTheme
import kotlin.math.roundToInt

/**
 * Playground preview of all Muse Design System components.
 *
 * Renders every component with enabled / disabled / selected states
 * inside [MuseTheme] so visual regressions are caught early.
 */
@Composable
fun DesignSystemPreview() {
    MuseTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .musePadding(),
            verticalArrangement = Arrangement.spacedBy(MuseSpacing.lg),
        ) {
            // ── Header ───────────────────────────────────────────
            Text("Muse Design System", style = MaterialTheme.typography.headlineMedium)
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // TopAppBar + Scaffold
            // ═══════════════════════════════════════════════════════
            SectionHeader("TopAppBar & Scaffold")

            ScaffoldExample()
            Spacer(Modifier.height(MuseSpacing.sm))
            Text("Demo TopAppBar with navigation + action icons",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Buttons
            // ═══════════════════════════════════════════════════════
            SectionHeader("Buttons")

            Row(horizontalArrangement = Arrangement.spacedBy(MuseSpacing.sm)) {
                MuseButton(onClick = { }) { Text("Filled") }
                MuseOutlinedButton(onClick = { }) { Text("Outlined") }
                MuseTextButton(onClick = { }) { Text("Text") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(MuseSpacing.sm)) {
                MuseButton(onClick = { }, enabled = false) { Text("Disabled") }
                MuseOutlinedButton(onClick = { }, enabled = false) { Text("Disabled") }
                MuseTextButton(onClick = { }, enabled = false) { Text("Disabled") }
            }
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Icon Buttons
            // ═══════════════════════════════════════════════════════
            SectionHeader("Icon Buttons")

            Row(horizontalArrangement = Arrangement.spacedBy(MuseSpacing.sm)) {
                MuseIconButton(onClick = { }) { Icon(Icons.Default.Settings, "Settings") }
                MuseIconButton(onClick = { }, enabled = false) { Icon(Icons.Default.Settings, "Disabled") }
            }
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Switches
            // ═══════════════════════════════════════════════════════
            SectionHeader("Switches")

            var switchChecked by remember { mutableStateOf(false) }
            MuseSwitch(checked = switchChecked, onCheckedChange = { switchChecked = it })
            MuseSwitch(checked = true, onCheckedChange = null, enabled = false)
            MuseSwitch(checked = false, onCheckedChange = null, enabled = false)
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Text Fields
            // ═══════════════════════════════════════════════════════
            SectionHeader("Text Fields")

            MuseOutlinedTextField(
                value = "Filled text",
                onValueChange = { },
                label = { Text("Label") },
                singleLine = true,
            )
            MuseOutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Disabled") },
                enabled = false,
                singleLine = true,
            )
            MuseOutlinedTextField(
                value = "Error state",
                onValueChange = { },
                label = { Text("Error") },
                isError = true,
                singleLine = true,
            )
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Cards
            // ═══════════════════════════════════════════════════════
            SectionHeader("Cards")

            MuseCard(modifier = Modifier.fillMaxWidth()) {
                Text("Card content", modifier = Modifier.musePadding())
            }
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Dialog — shown inline for preview
            // ═══════════════════════════════════════════════════════
            SectionHeader("Alert Dialog")

            var showDialog by remember { mutableStateOf(false) }
            MuseButton(onClick = { showDialog = true }) { Text("Show Dialog") }
            if (showDialog) {
                MuseAlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Dialog Title") },
                    text = { Text("Dialog body text") },
                    confirmButton = {
                        MuseButton(onClick = { showDialog = false }) { Text("Confirm") }
                    },
                    dismissButton = {
                        MuseTextButton(onClick = { showDialog = false }) { Text("Cancel") }
                    },
                )
            }
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Chips
            // ═══════════════════════════════════════════════════════
            SectionHeader("Filter Chips")

            var chipSelected by remember { mutableStateOf(false) }
            MuseFilterChip(
                selected = chipSelected,
                onClick = { chipSelected = !chipSelected },
                label = { Text("Toggle Chip") },
            )
            MuseFilterChip(
                selected = true,
                onClick = { },
                enabled = false,
                label = { Text("Disabled") },
            )
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Sliders
            // ═══════════════════════════════════════════════════════
            SectionHeader("Sliders")

            var sliderValue by remember { mutableStateOf(0.5f) }
            MuseSlider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Value: ${(sliderValue * 100).roundToInt()}%",
                style = MaterialTheme.typography.bodySmall)
            MuseSlider(
                value = 0.3f,
                onValueChange = { },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Tab Rows
            // ═══════════════════════════════════════════════════════
            SectionHeader("Tab Row")

            var tabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("Tab A", "Tab B", "Tab C")
            MuseTabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    MuseTab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title) },
                    )
                }
            }
            HorizontalDivider()

            // ═══════════════════════════════════════════════════════
            // Progress Indicators
            // ═══════════════════════════════════════════════════════
            SectionHeader("Progress Indicators")

            Row(horizontalArrangement = Arrangement.spacedBy(MuseSpacing.md)) {
                MuseCircularProgressIndicator()
                MuseCircularProgressIndicator(
                    progress = 0.65f,
                    modifier = Modifier.size(48.dp),
                )
            }
            HorizontalDivider()

            Spacer(Modifier.height(MuseSpacing.xxl))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun ScaffoldExample() {
    var counter by remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Scaffold + TopAppBar preview:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        MuseScaffold(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            topBar = {
                MuseTopAppBar(
                    title = { Text("Demo TopAppBar") },
                    navigationIcon = {
                        MuseIconButton(onClick = { }) {
                            Icon(Icons.Default.Favorite, "Back")
                        }
                    },
                    actions = {
                        MuseIconButton(onClick = { counter++ }) {
                            Icon(Icons.Default.Send, "Action")
                        }
                        MuseIconButton(onClick = { }) {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    },
                )
            },
        ) { padding ->
            Text(
                text = "Content area. Counter: $counter",
                modifier = Modifier.musePadding().padding(padding),
            )
        }
    }
}

@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?) {
    Icon(imageVector = imageVector, contentDescription = contentDescription, modifier = Modifier)
}

@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, modifier: Modifier) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = androidx.compose.material3.LocalContentColor.current,
    )
}

/**
 * Simple icon composable that avoids ambiguity between material / material3 icon overloads.
 */
@Composable
private fun Icon(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = androidx.compose.material3.LocalContentColor.current,
) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}
