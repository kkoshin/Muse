package io.github.kkoshin.muse.feature.setting

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import io.github.kkoshin.muse.designsystem.component.EditTextDialog
import io.github.kkoshin.muse.designsystem.component.InputDialogProperties
import top.yukonga.miuix.kmp.basic.BasicComponent

fun LazyListScope.preference(
    key: String,
    enabled: Boolean = true,
    title: String,
    summary: String? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    icon: @Composable (() -> Unit)? = null,
    widgetContainer: @Composable (RowScope.() -> Unit)? = null,
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
    title: String,
    modifier: Modifier = Modifier,
    noinline icon: @Composable (() -> Unit)? = null,
    summary: String? = null,
    noinline widgetContainer: @Composable (RowScope.() -> Unit)? = null,
    dialogTitle: String,
    inputLabel: String,
    value: T,
    crossinline onValueUpdate: (String) -> Unit,
) {
    item(key = key, contentType = "Preference") {
        var dialogVisible by remember { mutableStateOf(false) }
        Preference(
            title = title,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            icon = icon,
            summary = summary,
            widgetContainer = widgetContainer,
            onClick = {
                dialogVisible = true
            },
        )
        if (dialogVisible) {
            EditTextDialog(
                onDismissRequest = {
                    dialogVisible = false
                },
                title = dialogTitle,
                properties = InputDialogProperties(
                    value = value.toString(),
                    label = inputLabel,
                    keyboardType = when (T::class) {
                        String::class -> KeyboardType.Text
                        Number::class -> KeyboardType.Number
                        else -> KeyboardType.Text
                    }
                ),
                onConfirm = {
                    onValueUpdate(it)
                }
            )
        }
    }
}

@Composable
fun Preference(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    title: String,
    icon: @Composable (() -> Unit)? = null,
    summary: String? = null,
    widgetContainer: @Composable (RowScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    BasicComponent(
        modifier = modifier,
        title = title,
        summary = summary,
        enabled = enabled,
        startAction = icon,
        endActions = widgetContainer,
        onClick = onClick,
    )
}
