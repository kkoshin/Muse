package io.github.kkoshin.muse.feature.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.Route
import io.github.kkoshin.muse.designsystem.component.MuseButton
import io.github.kkoshin.muse.designsystem.component.MuseScaffold
import io.github.kkoshin.muse.designsystem.component.MuseTextButton
import io.github.kkoshin.muse.designsystem.component.MuseTopAppBar
import io.github.kkoshin.muse.platformbridge.AppBackButton
import kotlinx.serialization.Serializable

private val cardShape = RoundedCornerShape(12.dp)

@Serializable
object ApiKeyHelpArgs : Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyHelpScreen(
    onGoToSettings: () -> Unit,
    onOpenURL: (String) -> Unit,
) {
    MuseScaffold(
        topBar = {
            MuseTopAppBar(
                navigationIcon = { AppBackButton() },
                title = { Text("About API Key") },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ── Hero card ──
                Surface(
                    shape = cardShape,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(56.dp),
                        ) {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(14.dp),
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "What is an API Key?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "An API Key is like your personal pass to use 11labs services. " +
                                "Think of it as an account credential that lets this app make " +
                                "text-to-speech requests on your behalf.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Why card ──
                Surface(
                    shape = cardShape,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                Icons.Outlined.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(8.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Why does this app need one?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "This app does not include a built-in or shared API key. " +
                                    "Each user needs their own key from 11labs to keep usage " +
                                    "tied to your own account and quota.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Steps card ──
                Surface(
                    shape = cardShape,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "What you need to do",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "It only takes a minute, and you only need to do it once.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        StepItem(
                            number = "1",
                            title = "Create a free account",
                            description = "Sign up at elevenlabs.io",
                        )
                        StepItem(
                            number = "2",
                            title = "Get your API key",
                            description = "Find it under API Keys in your account settings",
                        )
                        StepItem(
                            number = "3",
                            title = "Paste it in Settings",
                            description = "Open this app's Settings and fill in your key",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Links card ──
                Surface(
                    shape = cardShape,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        LinkRow(
                            icon = Icons.Filled.Key,
                            label = "11labs API Keys page",
                            onClick = { onOpenURL("https://elevenlabs.io/app/settings/api-keys") },
                        )
                        LinkRow(
                            icon = Icons.Outlined.Info,
                            label = "Visit 11labs official site",
                            onClick = { onOpenURL("https://elevenlabs.io") },
                        )
                        LinkRow(
                            icon = YoutubeLogo,
                            label = "Watch tutorial on YouTube",
                            tint = Color.Unspecified,
                            onClick = { onOpenURL("https://www.youtube.com/results?search_query=elevenlabs+api+key+tutorial") },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── CTA ──
                MuseButton(
                    onClick = onGoToSettings,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text(
                        text = "Go to Settings",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        },
    )
}

@Composable
private fun StepItem(number: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(28.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            )
        }
    }
}

@Suppress("ObjectPropertyName")
private var _youtubeLogo: ImageVector? = null
private val YoutubeLogo: ImageVector
    get() {
        if (_youtubeLogo != null) return _youtubeLogo!!
        _youtubeLogo = ImageVector.Builder(
            name = "YoutubeLogo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group(
                pivotX = 12f,
                pivotY = 12f,
                scaleX = 0.84f,
                scaleY = 0.84f,
            ) {
                path(fill = SolidColor(Color.Red)) {
                    moveTo(23.5f, 6.2f)
                    curveTo(23.2f, 5.1f, 22.4f, 4.3f, 21.3f, 4f)
                    curveTo(19.4f, 3.5f, 12f, 3.5f, 12f, 3.5f)
                    reflectiveCurveTo(4.6f, 3.5f, 2.7f, 4f)
                    curveTo(1.6f, 4.3f, 0.8f, 5.1f, 0.5f, 6.2f)
                    curveTo(0f, 8.1f, 0f, 12f, 0f, 12f)
                    reflectiveCurveTo(0f, 15.9f, 0.5f, 17.8f)
                    curveTo(0.8f, 18.9f, 1.6f, 19.7f, 2.7f, 20f)
                    curveTo(4.6f, 20.5f, 12f, 20.5f, 12f, 20.5f)
                    reflectiveCurveTo(19.4f, 20.5f, 21.3f, 20f)
                    curveTo(22.4f, 19.7f, 23.2f, 18.9f, 23.5f, 17.8f)
                    curveTo(24f, 15.9f, 24f, 12f, 24f, 12f)
                    reflectiveCurveTo(24f, 8.1f, 23.5f, 6.2f)
                    close()
                }
                path(fill = SolidColor(Color.White)) {
                    moveTo(9.6f, 15.6f)
                    verticalLineTo(8.4f)
                    lineTo(15.9f, 12f)
                    lineTo(9.6f, 15.6f)
                    close()
                }
            }
        }.build()
        return _youtubeLogo!!
    }

@Composable
private fun LinkRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
) {
    MuseTextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = tint,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                Icons.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
            )
        }
    }
}
