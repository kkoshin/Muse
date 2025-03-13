package io.github.kkoshin.muse.feature.dashboard

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.github.foodiestudio.sugar.notification.toast
import io.github.kkoshin.muse.repo.MAX_TEXT_LENGTH
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.rememberKoinInject

@Composable
actual fun ScriptCreatorScreen(
    modifier: Modifier,
    script: Script?,
    onResult: (scriptId: Uuid?) -> Unit,
) {
    var content by remember {
        mutableStateOf(script?.text ?: "")
    }
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val clipboardManager = LocalClipboardManager.current
    val repo = rememberKoinInject<MuseRepo>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                scope.launch(Dispatchers.IO) {
                    val text = readTextContent(context, uri, false)
                    if (text.length > MAX_TEXT_LENGTH) {
                        withContext(Dispatchers.Main) {
                            context.toast("Text is too long, import failed.")
                        }
                    } else {
                        content = text
                    }
                }
            }
        }

    BackHandler {
        onResult(null)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        backPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                windowInsets = WindowInsets.statusBars,
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
                actions = {
                    IconButton(
                        enabled = content.isNotEmpty(),
                        onClick = {
                            scope.launch {
                                Script(text = content).let {
                                    repo.insertScript(it)
                                    onResult(it.id)
                                }
                            }
                        },
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                    }
                },
            )
        },
        content = { paddingValues ->
            BasicTextField(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                value = content,
                textStyle = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onSurface),
                onValueChange = {
                    if (it.length <= MAX_TEXT_LENGTH) {
                        content = it
                    } else {
                        context.toast("The text has exceeded the maximum limit of $MAX_TEXT_LENGTH characters")
                    }
                },
                cursorBrush = SolidColor(MaterialTheme.colors.onBackground),
                decorationBox = { field ->
                    Box {
                        field()
                        if (content.isEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                Text(
                                    "Enter text",
                                    style = MaterialTheme.typography.h5,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    OutlinedButton(
                                        shape = RoundedCornerShape(50),
                                        onClick = {
                                            clipboardManager.getText()?.toString()
                                                ?.take(MAX_TEXT_LENGTH)?.let {
                                                    content = it
                                                }
                                        },
                                    ) {
                                        Icon(
                                            Icons.Default.ContentPaste,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(Modifier.size(8.dp))
                                        Text("Paste")
                                    }
                                    Button(
                                        shape = RoundedCornerShape(50),
                                        onClick = {
                                            filePicker.launch("text/*")
                                        },
                                    ) {
                                        Icon(
                                            Icons.Outlined.FileOpen,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(Modifier.size(8.dp))
                                        Text("Import")
                                    }
                                }
                            }
                        }
                    }
                },
            )
        },
    )
}
