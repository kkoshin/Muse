package io.github.kkoshin.muse.feature.setting

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.designsystem.component.MuseIconButton
import io.github.kkoshin.muse.designsystem.component.MuseScaffold
import io.github.kkoshin.muse.designsystem.component.MuseTopAppBar
import io.github.kkoshin.muse.platformbridge.AppBackButton
import io.github.kkoshin.muse.platformbridge.CURRENT_PLATFORM
import io.github.kkoshin.muse.platformbridge.Platform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import io.github.kkoshin.muse.Route
import kotlinx.serialization.Serializable
import museroot.muse.generated.resources.Res
import museroot.muse.generated.resources.ic_telegram_logo
import museroot.muse.generated.resources.setting
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
data class SettingArgs(val scrollToApiKey: Boolean = false) : Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    args: SettingArgs,
    versionName: String,
    versionCode: Int,
    folderPath: String,
    onLaunchVoiceScreen: (Set<String>) -> Unit,
    onLaunchOpenSourceScreen: () -> Unit,
    onOpenURL: (String) -> Unit,
) {
    val speechProcessorManager = koinInject<SpeechProcessorManager>()
    val accountManager = koinInject<AccountManager>()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var availableVoiceIds: Set<String>? by remember {
        mutableStateOf(null)
    }

    var quota: CharacterQuota? by remember {
        mutableStateOf(null)
    }

    val apiKeyValue: String? by accountManager.apiKey.collectAsState(null)

    var highlightApiKey by remember { mutableStateOf(false) }
    val highlightColor by animateColorAsState(
        targetValue = if (highlightApiKey) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(600),
    )

    LaunchedEffect(apiKeyValue) {
        if (apiKeyValue.isNullOrEmpty()) return@LaunchedEffect
        accountManager.setElevenLabsApiKey(apiKeyValue!!)
        availableVoiceIds = speechProcessorManager.queryAvailableVoiceIds() ?: emptySet()
        quota = speechProcessorManager.queryQuota().getOrNull()
        quota?.status?.let {
            accountManager.setSubscriptionStatus(it)
        }
    }

    LaunchedEffect(args.scrollToApiKey) {
        if (args.scrollToApiKey) {
            listState.animateScrollToItem(0)
            highlightApiKey = true
            delay(1200)
            highlightApiKey = false
        }
    }

    MuseScaffold(
        modifier = Modifier,
        topBar = {
            MuseTopAppBar(
                navigationIcon = {
                    AppBackButton()
                },
                title = {
                    Text(text = stringResource(Res.string.setting))
                },
            )
        },
        content = { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                preferenceCategory(
                    key = "elevenlabs",
                    title = {
                        Text("ElevenLabs", color = MaterialTheme.colorScheme.primary)
                    },
                )
                editTextPreference(
                    key = "api_key",
                    modifier = Modifier.fillMaxWidth().background(highlightColor),
                    value = apiKeyValue ?: "",
                    onValueUpdate = { newValue ->
                        if (newValue.isNotEmpty()) {
                            scope.launch {
                                accountManager.setElevenLabsApiKey(newValue)
                            }
                        }
                    },
                    title = { Text(text = "API key") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                        )
                    },
                    summary = {
                        SummaryText(
                            text = if (apiKeyValue.isNullOrEmpty()) "Not set" else apiKeyValue!!.replaceRange(
                                0,
                                apiKeyValue!!.length - 2,
                                "•".repeat(apiKeyValue!!.length - 2),
                            )
                        )
                    },
                    dialogTitle = "ElevenLabs API Key",
                    inputLabel = "API Key",
                    widgetContainer = {
                        MuseIconButton(onClick = {
                            onOpenURL("https://elevenlabs.io/app/developers/api-keys")
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Launch, "launch")
                        }
                    },
                )
                if (apiKeyValue != null) {
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
                            Text("Voices accent")
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
                }
                when(CURRENT_PLATFORM) {
                    Platform.Android -> preference(
                        key = "export_folder",
                        icon = {
                            Icon(Icons.Outlined.Folder, "export folder")
                        },
                        title = {
                            Text("Export folder")
                        },
                        summary = {
                            SummaryText(
                                folderPath,
                            )
                        },
                    )
                    Platform.Ios -> {}
                    Platform.Desktop -> {}
                }

                preferenceCategory(
                    key = "about",
                    title = {
                        Text("About", color = MaterialTheme.colorScheme.primary)
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
                        SummaryText("Bug report, feature request, etc.")
                    },
                    onClick = {
                        onOpenURL("https://github.com/kkoshin/Muse/issues")
                    },
                )
                preference(
                    key = "telegram",
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_telegram_logo),
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text("Discuss on Telegram")
                    },
                    summary = {
                        SummaryText(text = "Primary timezone: UTC+8")
                    },
                    onClick = {
                        onOpenURL("https://t.me/muse_app")
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
                        SummaryText(text = "$versionName($versionCode)")
                    },
                    onClick = {
                        onOpenURL("https://github.com/kkoshin/Muse/releases")
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
        color = if (!isSystemInDarkTheme()) Color.DarkGray.copy(alpha = 0.7f) else Color.LightGray.copy(
            alpha = 0.7f
        ),
        style = MaterialTheme.typography.bodyMedium,
    )
}