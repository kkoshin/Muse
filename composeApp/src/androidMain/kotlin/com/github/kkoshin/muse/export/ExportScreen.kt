@file:Suppress("ktlint:standard:no-unused-imports")

package com.github.kkoshin.muse.export

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Effects
import kotlinx.serialization.Serializable

@Serializable
data class ExportArgs(
    val audioUri: String,
)

/**
 * 1. show progress bar
 * 2. play the exported audio file
 */
@OptIn(UnstableApi::class)
@Composable
fun ExportScreen(
    modifier: Modifier = Modifier,
    args: ExportArgs,
) {
    val context = LocalContext.current
    val videoExportPipeline =
        rememberVideoExportPipeline(
            context = context,
            input = args.audioUri.toUri(),
            effects = Effects(
                listOf(),
                listOf(),
            ),
        )

    val audiExportPipeline =
        rememberAudioExportPipeline(
            context = context,
            input = args.audioUri.toUri(),
        )

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                title = { Text(text = "Export") },
            )
        },
        content = { paddingValues ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ExportButton(modifier = Modifier, exportPipeline = audiExportPipeline)
                    ExportButton(modifier = Modifier, exportPipeline = videoExportPipeline)
                }
            }
        },
    )
}
