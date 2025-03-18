package io.github.kkoshin.muse.feature.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun LazyListScope.preferenceCategory(
    key: String,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    item(key = key, contentType = "PreferenceCategory") {
        PreferenceCategory(title = title, modifier = modifier)
    }
}

@Composable
fun PreferenceCategory(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
) {
    Box(modifier.padding(horizontal = 16.dp).padding(top = 12.dp, bottom = 8.dp)) {
        title()
    }
}