package io.github.kkoshin.muse.setting

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.BuildConfig
import kotlinx.serialization.Serializable
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference
import me.zhanghai.compose.preference.textFieldPreference
import me.zhanghai.compose.preference.twoTargetIconButtonPreference
import muse.composeapp.generated.resources.Res
import muse.composeapp.generated.resources.setting
import org.jetbrains.compose.resources.stringResource

@Serializable
object SettingArgs

/**
 * - free quota
 */
@Composable
fun SettingScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                title = {
                    Text(text = stringResource(Res.string.setting))
                },
            )
        },
        content = { paddingValues ->
            ProvidePreferenceLocals {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    preferenceCategory(
                        key = "interface",
                        title = {
                            Text("Interface", color = MaterialTheme.colors.primary)
                        },
                    )
                    preference(
                        key = "language",
                        title = {
                            Text("Language")
                        },
                        summary = {
                            Text("System default")
                        },
                        onClick = {
                            // TODO
                        },
                    )
                    twoTargetIconButtonPreference(
                        key = "theme",
                        title = {
                            Text("Theme color")
                        },
                        iconButtonIcon = {
                            Spacer(
                                Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colors.primary, shape = CircleShape),
                            )
                        },
                        summary = {
                            Text("Color that appears most frequently in the app")
                        },
                        onIconButtonClick = {
                            // TODO:
                        },
                    )
                    listPreference(
                        key = "night_mode",
                        defaultValue = "Follow system",
                        values = listOf(
                            "Follow system",
                            "Off",
                            "On",
                        ),
                        type = ListPreferenceType.DROPDOWN_MENU,
                        title = {
                            Text("Night mode")
                        },
                        summary = {
                            Text(it)
                        },
                    )
                    preferenceCategory(
                        key = "elevenlabs",
                        title = {
                            Text("Elevenlabs", color = MaterialTheme.colors.primary)
                        },
                    )
                    switchPreference(
                        key = "multiple_key_mode",
                        defaultValue = false,
                        title = { Text(text = "Multiple Key Mode") },
                    )
                    textFieldPreference(
                        key = "api_key",
                        defaultValue = "",
                        title = { Text(text = "API key") },
                        textToValue = { it },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                            )
                        },
                        summary = {
                            if (it.isEmpty()) {
                                Text(text = "Not set")
                            } else {
                                Text(text = it)
                            }
                        },
                    )
                    preference(
                        key = "export_folder",
                        title = {
                            Text("Export folder")
                        },
                        summary = {
                            Text("Ask every time")
                        },
                        onClick = {
                            // TODO
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
                            // TODO
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
                            Text("bug report, feature request, etc.")
                        },
                        onClick = {
                            val url = "https://github.com/kkoshin/Muse/issues"
                            val intent = CustomTabsIntent
                                .Builder()
                                .build()
                            intent.launchUrl(context, Uri.parse(url))
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
                            Text(text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
                        },
                    )
                }
            }
        },
    )
}