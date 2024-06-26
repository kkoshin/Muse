package io.github.kkoshin.muse.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

@Serializable
object ExportConfigSheetArgs

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExportConfigSheet(
    modifier: Modifier = Modifier,
    onExport: (voiceId: String) -> Unit,
) {
    var silence by remember {
        mutableFloatStateOf(1.0f)
    }

    var fixedDurationEnabled by remember {
        mutableStateOf(true)
    }

    LazyColumn(
        modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 56.dp),
    ) {
        item {
            DragHandle()

            Box(
                Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Configure before export",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }
        item {
            Column {
                Text("Voice")
                Row {
                    Chip(onClick = {
                    }) {
                        Text("Anna")
                    }
                }
            }
        }
        item {
            Column {
                Text("Silence")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Fixed duration", Modifier.weight(1f))
                    Switch(checked = fixedDurationEnabled, onCheckedChange = {
                        fixedDurationEnabled = it
                    })
                }
                if (fixedDurationEnabled) {
                    Slider(value = silence, valueRange = 0f..5.0f, onValueChange = {
                        silence = it
                    })
                } else {
                    Text("Duration per character:")
                }
            }
        }
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    // TODO
                    onExport("TODO")
                },
            ) {
                Text("Continue export")
            }
        }
    }
}