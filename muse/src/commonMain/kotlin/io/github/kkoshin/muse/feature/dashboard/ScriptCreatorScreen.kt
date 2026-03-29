@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import io.github.kkoshin.muse.designsystem.component.ScreenScaffold
import io.github.kkoshin.muse.designsystem.theme.AppTheme
import io.github.kkoshin.muse.platformbridge.BackHandler
import io.github.kkoshin.muse.platformbridge.DocumentPicker
import io.github.kkoshin.muse.platformbridge.LocalToaster
import io.github.kkoshin.muse.repo.MAX_TEXT_LENGTH
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
object ScriptCreatorArgs {
    private const val RESULT_KEY = "script_creator_result_script_id"

    fun SavedStateHandle.getScriptId(): Uuid? {
        return get<String?>(RESULT_KEY)?.let(Uuid::parse)
    }

    fun SavedStateHandle.setScriptId(scriptId: Uuid?) {
        set(RESULT_KEY, scriptId?.toString())
    }
}

@Composable
expect fun rememberPicker(onResult: (text: String) -> Unit): DocumentPicker

@Composable
fun ScriptCreatorScreen(
    modifier: Modifier = Modifier,
    script: Script? = null,
    onResult: (scriptId: Uuid?) -> Unit,
) {
    var content by remember {
        mutableStateOf(script?.text ?: "")
    }
    val clipboardManager = LocalClipboardManager.current
    val repo = koinInject<MuseRepo>()
    val scope = rememberCoroutineScope()
    val toaster = LocalToaster.current

    val filePicker = rememberPicker { text ->
        content = text
    }

    BackHandler {
        onResult(null)
    }

    ScreenScaffold(
        modifier = modifier,
        title = "",
        navigationIcon = {
            IconButton(
                modifier = Modifier.padding(start = 16.dp),
                onClick = {
                    onResult(null)
                }) {
                Icon(MiuixIcons.Back, contentDescription = null)
            }
        },
        actions = {
            IconButton(
                modifier = Modifier.padding(end = 16.dp),
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
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    tint = if (content.isNotEmpty()) AppTheme.colorScheme.onSurface else AppTheme.colorScheme.disabledOnSurface
                )
            }
        },
        content = { paddingValues, scrollBehavior ->
            BasicTextField(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior!!.nestedScrollConnection),
                value = content,
                textStyle = AppTheme.textStyles.title2.copy(color = AppTheme.colorScheme.onSurface),
                onValueChange = {
                    if (it.length <= MAX_TEXT_LENGTH) {
                        content = it
                    } else {
                        toaster.show("The text has exceeded the maximum limit of $MAX_TEXT_LENGTH characters")
                    }
                },
                cursorBrush = SolidColor(AppTheme.colorScheme.onBackground),
                decorationBox = { field ->
                    Box {
                        field()
                        if (content.isEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                Text(
                                    "Enter text",
                                    style = AppTheme.textStyles.title2,
                                    color = AppTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Button(
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
                                        colors = ButtonDefaults.buttonColorsPrimary(),
                                        onClick = {
                                            filePicker.launch()
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
