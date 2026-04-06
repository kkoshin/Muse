package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.kkoshin.muse.designsystem.theme.AppTheme
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField

@Composable
private fun AppDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .background(AppTheme.colorScheme.background, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            title()
            text()
            Row {
                buttons(this)
            }
        }
    }
}

@Stable
data class InputDialogProperties(
    val label: String? = null,
    val value: String,
    val keyboardType: KeyboardType = KeyboardType.Text,
)

@Composable
fun EditTextDialog(
    title: String,
    properties: InputDialogProperties,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var input by remember(properties) {
        mutableStateOf(
            TextFieldValue(
                properties.value,
                TextRange(properties.value.length)
            )
        )
    }
    val focusRequester = remember { FocusRequester() }

    AppDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(title, modifier = Modifier.padding(bottom = 8.dp))
        },
        text = {
            TextField(
                label = properties.label ?: "",
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                useLabelAsPlaceholder = true,
                value = input,
                onValueChange = {
                    input = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = properties.keyboardType,
                )
            )
        },
        buttons = {
            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(),
                onClick = {
                    onDismissRequest()
                }) {
                Text("Cancel")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                modifier = Modifier.weight(1f),
                enabled = input.text.isNotEmpty(),
                colors = ButtonDefaults.buttonColorsPrimary(),
                onClick = {
                    onDismissRequest()
                    onConfirm(input.text.trim())
                }) {
                Text("Confirm")
            }
        },
    )

    LaunchedEffect(Unit) {
        kotlin.runCatching {
            focusRequester.requestFocus()
        }
    }
}