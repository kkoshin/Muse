package io.github.kkoshin.muse.setting

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.github.kkoshin.muse.BuildConfig
import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.tts.CharacterQuota
import io.github.kkoshin.muse.tts.TTSManager
import kotlinx.serialization.Serializable
import muse.composeapp.generated.resources.Res
import muse.composeapp.generated.resources.setting
import okio.Path.Companion.toOkioPath
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.rememberKoinInject

@Serializable
object SettingArgs

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    onLaunchVoiceScreen: (Set<String>) -> Unit,
    onLaunchOpenSourceScreen: () -> Unit,
) {
    val context = LocalContext.current
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val ttsManager = rememberKoinInject<TTSManager>()

    var availableVoiceIds: Set<String>? by remember {
        mutableStateOf(null)
    }

    var quota: CharacterQuota? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(Unit) {
        availableVoiceIds = ttsManager.queryAvailableVoiceIds() ?: emptySet()
    }

    LaunchedEffect(Unit) {
        quota = ttsManager.queryQuota().getOrNull()
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                backgroundColor = MaterialTheme.colors.surface,
                navigationIcon = {
                    IconButton(onClick = {
                        backPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(text = stringResource(Res.string.setting))
                },
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
//                preferenceCategory(
//                    key = "interface",
//                    title = {
//                        Text("Interface", color = MaterialTheme.colors.primary)
//                    },
//                )
//                preference(
//                    key = "language",
//                    title = {
//                        Text("Language")
//                    },
//                    summary = {
//                        SummaryText("English")
//                    },
//                    onClick = {
//                        context.toast("TODO")
//                    },
//                )
                preferenceCategory(
                    key = "elevenlabs",
                    title = {
                        Text("ElevenLabs", color = MaterialTheme.colors.primary)
                    },
                )
                // TODO: config key
//                    textFieldPreference(
//                        key = "api_key",
//                        defaultValue = "",
//                        title = { Text(text = "API key") },
//                        textToValue = { it },
//                        icon = {
//                            Icon(
//                                imageVector = Icons.Outlined.Lock,
//                                contentDescription = null,
//                            )
//                        },
//                        summary = {
//                            if (it.isEmpty()) {
//                                SummaryText(text = "Not set")
//                            } else {
//                                SummaryText(text = it)
//                            }
//                        },
//                    )
                preference(
                    key = "quota",
                    enabled = availableVoiceIds != null,
                    icon = {
                        Icon(Icons.Outlined.Numbers, "voice")
                    },
                    title = {
                        Text("Character quota")
                    },
                    summary = {
                        SummaryText(
                            quota?.let {
                                "${it.remaining}/${it.total}"
                            } ?: "-/-"
                        )
                    },
                )
                preference(
                    key = "voice_setting",
                    enabled = availableVoiceIds != null,
                    icon = {
                        Icon(Icons.Outlined.Audiotrack, "voice")
                    },
                    title = {
                        Text("Voices setting")
                    },
                    summary = {
                        availableVoiceIds?.let {
                            SummaryText(
                                if (it.isEmpty()) {
                                    "No voices selected"
                                } else {
                                    "${it.size} voice(s) selected"
                                }
                            )
                        }
                    },
                    onClick = {
                        onLaunchVoiceScreen(availableVoiceIds!!)
                    },
                )
                preference(
                    key = "export_folder",
                    icon = {
                        Icon(Icons.Outlined.Folder, "export folder")
                    },
                    title = {
                        Text("Export folder")
                    },
                    summary = {
                        SummaryText(
                            Environment
                                .getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS,
                                ).toOkioPath()
                                .resolve("../${MuseRepo.getExportRelativePath(context)}", true)
                                .toString(),
                        )
                    },
                )

                preferenceCategory(
                    key = "about",
                    title = {
                        Text("About", color = MaterialTheme.colors.primary)
                    },
                )
                preference(
                    key = "license",
                    title = {
                        Text("Open source license")
                    },
                    onClick = {
                        onLaunchOpenSourceScreen()
                    },
                )
                preference(
                    key = "feedback",
                    title = {
                        Text("Send feedback")
                    },
                    icon = {
                        Icon(Icons.Default.MailOutline, contentDescription = null)
                    },
                    summary = {
                        SummaryText("bug report, feature request, etc.")
                    },
                    onClick = {
                        context.openURL("https://github.com/kkoshin/Muse/issues")
                    },
                )
                preference(
                    key = "version",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text("Version")
                    },
                    summary = {
                        SummaryText(text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
                    },
                    onClick = {
                        context.openURL("https://github.com/kkoshin/Muse/releases")
                    },
                )
            }
        },
    )
}

@Composable
private fun SummaryText(text: String) {
    Text(
        text,
        color = if (MaterialTheme.colors.isLight) Color.DarkGray.copy(0.7f) else Color.LightGray.copy(
            alpha = 0.7f
        ),
        style = MaterialTheme.typography.body2,
    )
}

internal fun Context.openURL(url: String) {
    val intent = CustomTabsIntent
        .Builder()
        .build()
    intent.launchUrl(this, Uri.parse(url))
}