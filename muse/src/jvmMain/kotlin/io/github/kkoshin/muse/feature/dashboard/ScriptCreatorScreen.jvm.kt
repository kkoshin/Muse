package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.kkoshin.muse.platformbridge.DocumentPicker

@Composable
actual fun rememberPicker(onResult: (text: String) -> Unit) : DocumentPicker = remember { DocumentPicker() }
