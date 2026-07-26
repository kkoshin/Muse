package io.github.kkoshin.muse.feature.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.Route
import io.github.kkoshin.muse.designsystem.component.MuseButton
import io.github.kkoshin.muse.designsystem.component.MuseFilterChip
import io.github.kkoshin.muse.designsystem.component.MuseSlider
import io.github.kkoshin.muse.designsystem.component.MuseSwitch
import kotlinx.serialization.Serializable

@Serializable
class ExportConfigSheetArgs(
    val scriptId: String,
    val voiceIds: List<String>,
    val voiceNames: List<String>,
    val exportMode: String
) : Route {
    init {
        check(voiceNames.size == voiceIds.size)
    }
}

@Composable
fun ExportConfigSheet(
    modifier: Modifier = Modifier,
    voiceIds: List<String>,
    voiceNames: List<String>,
    mode: ExportMode,
    onExport: (
        voiceId: String,
        fixedDurationEnabled: Boolean,
        fixedSilenceSeconds: Float,
        silencePerCharSeconds: Float,
        minDynamicDurationSeconds: Float,
    ) -> Unit,
) {
    var fixedSilence by remember {
        mutableFloatStateOf(1.0f)
    }
    var silencePerChar by remember {
        mutableFloatStateOf(0.2f)
    }

    var minDynamicDuration by remember {
        mutableFloatStateOf(1f)
    }

    var fixedDurationEnabled by remember {
        mutableStateOf(false)
    }

    var selectedVoiceId: String? by remember {
        mutableStateOf(null)
    }

    LazyColumn(
        modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 8.dp),
    ) {
        item {
            DragHandle()
        }
        item {
            Column {
                Text("Voice", style = MaterialTheme.typography.labelLarge)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    items(voiceIds.size) { index ->
                        val selected = selectedVoiceId == voiceIds[index]
                        val chipColors = if (selected) {
                            androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            )
                        } else {
                            androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                containerColor = Color.Transparent,
                            )
                        }
                        val chipBorder = if (!selected) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                        } else {
                            null
                        }
                        MuseFilterChip(
                            selected = selected,
                            onClick = {
                                selectedVoiceId = voiceIds[index]
                            },
                            colors = chipColors,
                            border = chipBorder,
                            leadingIcon = {
                                if (selected) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                }
                            },
                            label = { Text(voiceNames[index]) },
                        )
                    }
                }
            }
        }
        if (mode == ExportMode.Dictation) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Fixed silence duration",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )

                        MuseSwitch(
                            checked = fixedDurationEnabled,
                            onCheckedChange = {
                                fixedDurationEnabled = it
                            },
                        )
                    }
                    if (fixedDurationEnabled) {
                        SliderBar(value = fixedSilence, valueRange = 0f..5.0f, onValueChange = {
                            fixedSilence = it
                        }, format = {
                            it.toInt().toString()
                        })
                    } else {
                        Text("Duration per character:", style = MaterialTheme.typography.labelLarge)
                        SliderBar(value = silencePerChar, valueRange = 0.1f..1f, onValueChange = {
                            silencePerChar = it
                        })
                        Text("Min duration:", style = MaterialTheme.typography.labelLarge)
                        SliderBar(value = minDynamicDuration, valueRange = 0f..5f, onValueChange = {
                            minDynamicDuration = it
                        }, format = {
                            it.toInt().toString()
                        })
                    }
                }
            }
        }
        item {
            MuseButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedVoiceId != null,
                onClick = {
                    onExport(
                        selectedVoiceId!!,
                        fixedDurationEnabled,
                        fixedSilence,
                        silencePerChar,
                        minDynamicDuration,
                    )
                },
            ) {
                Text("Continue export")
            }
        }
    }
}

@Composable
fun SliderBar(
    modifier: Modifier = Modifier,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    format: (Float) -> String = { formatDecimal(it, 1) },
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MuseSlider(
            value = value,
            valueRange = valueRange,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
        )
        Text(format(value) + "s")
    }
}