package io.github.kkoshin.muse.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

fun LazyListScope.preference(
    key: String,
    enabled: Boolean = true,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    widgetContainer: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    item(key = key, contentType = "Preference") {
        Preference(
            title = title,
            modifier = modifier,
            enabled = enabled,
            icon = icon,
            summary = summary,
            widgetContainer = widgetContainer,
            onClick = onClick,
        )
    }
}

inline fun <reified T> LazyListScope.editTextPreference(
    key: String,
    enabled: Boolean = true,
    noinline title: @Composable () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    noinline icon: @Composable (() -> Unit)? = null,
    noinline summary: @Composable (() -> Unit)? = null,
    noinline widgetContainer: @Composable (() -> Unit)? = null,
    dialogTitle: String,
    inputLabel: String,
    value: T,
    crossinline onValueUpdate: (String) -> Unit,
) {
    item(key = key, contentType = "Preference") {
        var dialogVisible by remember { mutableStateOf(false) }
        Preference(
            title = title,
            modifier = modifier,
            enabled = enabled,
            icon = icon,
            summary = summary,
            widgetContainer = widgetContainer,
            onClick = {
                dialogVisible = true
            },
        )
        if (dialogVisible) {
            EditTextPreferenceDialog(
                onDismissRequest = {
                    dialogVisible = false
                },
                value = value.toString(),
                title = dialogTitle,
                label = inputLabel,
                onConfirm = {
                    onValueUpdate(it)
                },
                keyboardType = when (T::class) {
                    String::class -> KeyboardType.Text
                    Number::class -> KeyboardType.Number
                    else -> KeyboardType.Text
                }
            )
        }
    }
}

@Composable
fun EditTextPreferenceDialog(
    onDismissRequest: () -> Unit,
    title: String,
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onConfirm: (String) -> Unit
) {
    var input by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(title, modifier = Modifier.padding(bottom = 8.dp))
        },
        text = {
            OutlinedTextField(
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                value = input,
                onValueChange = {
                    input = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                )
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                onConfirm(input.text)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.38f),
                ),
                onClick = {
                    onDismissRequest()
                }) {
                Text("Cancel")
            }
        },
    )

    LaunchedEffect(Unit) {
        kotlin.runCatching {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun Preference(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    title: @Composable () -> Unit,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    widgetContainer: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier
            .clickable(enabled) { onClick?.invoke() }
            .heightIn(min = 56.dp)
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        icon?.invoke()
        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
            title()
            summary?.invoke()
        }
        widgetContainer?.invoke()
    }
}
