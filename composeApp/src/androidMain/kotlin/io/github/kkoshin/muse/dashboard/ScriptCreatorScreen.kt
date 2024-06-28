package io.github.kkoshin.muse.dashboard

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

@Serializable
object ScriptCreatorArgs {
    const val RESULT_KEY = "script_creator_result_script_id"
}

@Composable
fun ScriptCreatorScreen(
    modifier: Modifier = Modifier,
    script: Script? = null,
    onResult: (Script?) -> Unit,
) {
    var content by remember {
        mutableStateOf(script?.text ?: "")
    }
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val clipboardManager = LocalClipboardManager.current

    BackHandler {
        onResult(null)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
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
                            // TODO: 持久化刚创建的这个内容
                            onResult(Script(text = content))
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
                textStyle = MaterialTheme.typography.h5,
                onValueChange = {
                    content = it
                },
                decorationBox = { field ->
                    Box {
                        field()
                        if (content.isEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                Text(
                                    "Enter text",
                                    style = MaterialTheme.typography.h5,
                                    color = Color.DarkGray,
                                )
                                Button(
                                    shape = RoundedCornerShape(50),
                                    onClick = {
                                        content = clipboardManager.getText().toString()
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
                            }
                        }
                    }
                },
            )
        },
    )

//    Text(text = "Phrases:", Modifier.padding(horizontal = 16.dp))
//    FlowRow(
//        Modifier
//            .fillMaxSize()
//            .padding(horizontal = 16.dp)
//            .verticalScroll(rememberScrollState()),
//        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        verticalArrangement = Arrangement.spacedBy(2.dp),
//    ) {
//        phrases.forEach {
//            OutlinedButton(onClick = { /*TODO*/ }) {
//                Text(text = it)
//            }
//        }
//    }
}