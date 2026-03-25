package io.github.kkoshin.muse.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.CharacterQuota
import io.github.kkoshin.muse.designsystem.component.ScreenScaffold
import io.github.kkoshin.muse.designsystem.theme.AppTheme
import io.github.kkoshin.muse.platformbridge.CURRENT_PLATFORM
import io.github.kkoshin.muse.platformbridge.Platform
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import museroot.muse.generated.resources.Res
import museroot.muse.generated.resources.setting
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle

@Serializable
object SettingArgs

@Composable
fun SettingScreen(
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

    var availableVoiceIds: Set<String>? by remember {
        mutableStateOf(null)
    }

    var quota: CharacterQuota? by remember {
        mutableStateOf(null)
    }

    val apiKeyValue: String? by accountManager.apiKey.collectAsState(null)

    LaunchedEffect(apiKeyValue) {
        if (apiKeyValue.isNullOrEmpty()) return@LaunchedEffect
        accountManager.setElevenLabsApiKey(apiKeyValue!!)
        availableVoiceIds = speechProcessorManager.queryAvailableVoiceIds() ?: emptySet()
        quota = speechProcessorManager.queryQuota().getOrNull()
        quota?.status?.let {
            accountManager.setSubscriptionStatus(it)
        }
    }

    val scrollBehavior = MiuixScrollBehavior()

    val headerModifier =
        Modifier.padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(AppTheme.colorScheme.background)
    val centerModifier = Modifier.padding(horizontal = 16.dp).background(AppTheme.colorScheme.background)
    val soloModifier =
        Modifier.padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp)).background(AppTheme.colorScheme.background)
    val footerModifier =
        Modifier.padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(AppTheme.colorScheme.background)

    ScreenScaffold(
        title = stringResource(Res.string.setting),
        scrollBehavior = scrollBehavior,
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = paddingValues
            ) {
                preferenceCategory(
                    key = "elevenlabs",
                    title = {
                        SmallTitle(
                            "ElevenLabs",
                        )
                    },
                )
                editTextPreference(
                    modifier = headerModifier,
                    key = "api_key",
                    value = apiKeyValue ?: "",
                    onValueUpdate = { newValue ->
                        if (newValue.isNotEmpty()) {
                            scope.launch {
                                accountManager.setElevenLabsApiKey(newValue)
                            }
                        }
                    },
                    title = "API key",
                    summary = if (apiKeyValue.isNullOrEmpty()) "Not set" else apiKeyValue!!.replaceRange(
                        0,
                        apiKeyValue!!.length - 2,
                        "•".repeat(apiKeyValue!!.length - 2),
                    ),
                    dialogTitle = "ElevenLabs API Key",
                    inputLabel = "API Key",
                    widgetContainer = {
                        IconButton(onClick = {
                            onOpenURL("https://elevenlabs.io/app/developers/api-keys")
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Launch, "launch")
                        }
                    },
                )
                preference(
                    modifier = centerModifier,
                    key = "quota",
                    enabled = apiKeyValue != null && availableVoiceIds != null,
                    title = "Character quota",
                    summary = quota?.let {
                        "${it.remaining}/${it.total}"
                    } ?: "-/-",
                )
                preference(
                    modifier = footerModifier,
                    key = "voice_setting",
                    enabled = apiKeyValue != null && availableVoiceIds != null,
                    title = "Voices accent",
                    summary = availableVoiceIds?.let {
                        if (it.isEmpty()) {
                            "No voices selected"
                        } else {
                            "${it.size} voice(s) selected"
                        }
                    },
                    onClick = {
                        onLaunchVoiceScreen(availableVoiceIds!!)
                    },
                )

                when (CURRENT_PLATFORM) {
                    Platform.Android -> {
                        preferenceCategory(
                            key = "project",
                            title = {
                                SmallTitle(
                                    "Project",
                                )
                            },
                        )
                        preference(
                            modifier = soloModifier,
                            key = "export_folder",
                            title = "Export folder",
                            summary = folderPath,
                        )
                    }

                    Platform.Ios -> {}
                }
                preferenceCategory(
                    key = "about",
                    title = {
                        SmallTitle(
                            "About",
                        )
                    },
                )

                preference(
                    modifier = headerModifier,
                    key = "license",
                    title = "Open source license",
                    onClick = {
                        onLaunchOpenSourceScreen()
                    },
                )

                preference(
                    modifier = centerModifier,
                    key = "feedback",
                    title = "Send feedback",
                    summary = "Bug report, feature request, etc.",
                    onClick = {
                        onOpenURL("https://github.com/kkoshin/Muse/issues")
                    },
                )
                preference(
                    modifier = centerModifier,
                    key = "telegram",
                    title = "Discuss on Telegram",
                    summary = "Primary timezone: UTC+8",
                    onClick = {
                        onOpenURL("https://t.me/muse_app")
                    },
                )
                preference(
                    modifier = footerModifier,
                    key = "version",
                    title = "Version",
                    summary = "$versionName($versionCode)",
                    onClick = {
                        onOpenURL("https://github.com/kkoshin/Muse/releases")
                    },
                )
            }
        },
    )
}