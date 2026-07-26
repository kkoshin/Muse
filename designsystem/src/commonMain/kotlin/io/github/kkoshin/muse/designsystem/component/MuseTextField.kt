@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.kkoshin.muse.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Muse Design System — Outlined text field.
 *
 * Thin wrapper over M3 [OutlinedTextField]. Colors resolve through
 * [OutlinedTextFieldDefaults.colors] which reads from [MuseTheme]'s color scheme.
 */
@Composable
fun MuseOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable () -> Unit = {},
    placeholder: @Composable () -> Unit = {},
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    prefix: @Composable () -> Unit = {},
    suffix: @Composable () -> Unit = {},
    supportingText: @Composable () -> Unit = {},
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        shape = shape,
        colors = colors,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
    )
}
