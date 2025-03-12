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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
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
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
class ExportConfigSheetArgs(
    val scriptId: String,
//    val phrases: List<String>,
    val voiceIds: List<String>,
    val voiceNames: List<String>,
) {
    init {
        check(voiceNames.size == voiceIds.size)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExportConfigSheet(
    modifier: Modifier = Modifier,
    voiceIds: List<String>,
    voiceNames: List<String>,
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
        mutableStateOf(true)
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
                Text("Voice", style = MaterialTheme.typography.button)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    items(voiceIds.size) {
                        val selected = selectedVoiceId == voiceIds[it]
                        Chip(
                            onClick = {
                                selectedVoiceId = voiceIds[it]
                            },
                            border = if (!selected) {
                                BorderStroke(
                                    1.dp,
                                    MaterialTheme.colors.onSurface.copy(0.12f),
                                )
                            } else {
                                null
                            },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = if (!selected) {
                                    Color.Transparent
                                } else {
                                    MaterialTheme.colors.primary.copy(
                                        0.12f,
                                    )
                                },
                            ),
                            leadingIcon = {
                                if (selected) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                                }
                            },
                        ) {
                            Text(voiceNames[it])
                        }
                    }
                }
            }
        }
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Fixed silence duration",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.weight(1f),
                    )

                    Switch(
                        checked = fixedDurationEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colors.primary,
                        ),
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
                    Text("Duration per character:", style = MaterialTheme.typography.button)
                    SliderBar(value = silencePerChar, valueRange = 0.1f..1f, onValueChange = {
                        silencePerChar = it
                    })
                    Text("Min duration:", style = MaterialTheme.typography.button)
                    SliderBar(value = minDynamicDuration, valueRange = 0f..5f, onValueChange = {
                        minDynamicDuration = it
                    }, format = {
                        it.toInt().toString()
                    })
                }
            }
        }
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
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
    format: (Float) -> String = { String.format(Locale.getDefault(), "%.1f", it) },
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Slider(
            value = value,
            valueRange = valueRange,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
        )
        Text(format(value) + "s")
    }
}