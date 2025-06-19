package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import muse.feature.generated.resources.Res
import muse.feature.generated.resources.format_replace_newlines_with_spaces
import muse.feature.generated.resources.import_file_content_with_file_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun ImportConfirmDialog(
    modifier: Modifier = Modifier,
    fileName: String,
    onConfirm: (Boolean) -> Unit,
    onCancel: () -> Unit,
) {
    var replaceEnabled by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onCancel()
        },
        title = { Text("Import Text", style = MaterialTheme.typography.h6) },
        text = {
            Column {
                Text(
                    stringResource(Res.string.import_file_content_with_file_name, fileName),
                    style = MaterialTheme.typography.body1
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // workaround: add negative margin to align checkbox with text
                    modifier = Modifier.offset(x = (-16).dp)
                ) {
                    Checkbox(
                        replaceEnabled,
                        onCheckedChange = {
                            replaceEnabled = it
                        })
                    Text(
                        stringResource(Res.string.format_replace_newlines_with_spaces),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(replaceEnabled)
            }) {
                Text("Import", style = MaterialTheme.typography.button)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancel()
            }) {
                Text("Cancel", style = MaterialTheme.typography.button)
            }
        },
    )
}