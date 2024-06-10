@file:Suppress("ktlint:standard:no-unused-imports")

package com.github.kkoshin.muse.export

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Effects
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
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
@kotlin.OptIn(ExperimentalSugarApi::class)
@Composable
fun ExportScreen(
    modifier: Modifier = Modifier,
    args: ExportArgs,
    viewModel: ExportViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val appFileHelper = remember {
        AppFileHelper(context.applicationContext)
    }
    val videoExportPipeline =
        rememberVideoExportPipeline(
            context = context,
            input = args.audioUri.toUri(),
            effects = Effects(
                listOf(),
                listOf(),
            ),
        )

    val audioExportPipeline =
        rememberAudioExportPipeline(
            context = context,
            input = args.audioUri.toUri(),
        )

    var selectedMp3: Uri? by remember {
        mutableStateOf(null)
    }
    val mp3FilePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
            if (it != null) {
                selectedMp3 = it
                viewModel.testDecodeMp3(selectedMp3!!)
            }
        }

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
                    Button(onClick = {
                        if (selectedMp3 != null) {
                            viewModel.testDecodeMp3(selectedMp3!!)
                        } else {
                            mp3FilePicker.launch(arrayOf("audio/*"))
                        }
                    }) {
                        Text(text = "Decode Mp3 to WAV")
                    }
                    ExportButton(modifier = Modifier, exportPipeline = audioExportPipeline)
                    ExportButton(modifier = Modifier, exportPipeline = videoExportPipeline)
                }
            }
        },
    )
}
