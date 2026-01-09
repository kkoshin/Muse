package io.github.kkoshin.muse.feature.noise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.kkoshin.muse.core.provider.SoundEffectConfig
import io.github.kkoshin.muse.feature.editor.formatDecimal
import kotlinx.serialization.Serializable
import muse.feature.generated.resources.Res
import muse.feature.generated.resources.sound_effect
import muse.feature.generated.resources.white_noise_start
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class ConfigView {
    None,
    Duration,
    PromptInfluence,
}

@Serializable
object WhiteNoiseConfigScreenArgs

@Composable
fun WhiteNoiseConfigScreen(
    modifier: Modifier = Modifier,
    onGenerate: (prompt: String, config: SoundEffectConfig) -> Unit
) {
    var prompt by remember {
        mutableStateOf("")
    }

    var config by remember {
        mutableStateOf(SoundEffectConfig())
    }

    var configView by remember {
        mutableStateOf(ConfigView.None)
    }

    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.sound_effect),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h6
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // TODO: handle back
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                windowInsets = WindowInsets.statusBars,
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            )
        },
        content = { paddingValues ->
            Column(
                modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
                    .padding(paddingValues),
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            MaterialTheme.colors.surface,
                            RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                        ),
                    value = prompt,
                    textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onSurface),
                    onValueChange = {
                        prompt = it
                    },
                    cursorBrush = SolidColor(MaterialTheme.colors.onBackground),
                    decorationBox = { field ->
                        Box(Modifier.padding(16.dp)) {
                            field()
                            if (prompt.isEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                    Text(
                                        "Enter prompt",
                                        style = MaterialTheme.typography.subtitle1,
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
                                                    ?.let {
                                                        prompt = it
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
                                    }
                                }
                            }
                        }
                    },
                )
                Column(
                    Modifier
                        .navigationBarsPadding()
                        .padding(vertical = 12.dp)
                ) {
                    AnimatedVisibility(
                        visible = configView != ConfigView.None,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        when (configView) {
                            ConfigView.Duration -> {
                                DurationConfig(
                                    modifier = Modifier,
                                    duration = config.duration
                                ) { config = config.copy(duration = it) }
                            }

                            ConfigView.PromptInfluence -> {
                                PromptInfluenceConfig(
                                    modifier = Modifier,
                                    config.promptInfluence
                                ) { config = config.copy(promptInfluence = it) }
                            }

                            else -> {}
                        }
                    }
                    Row(
                        Modifier
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ConfigMenuButton(
                            selected = configView == ConfigView.Duration,
                            Icons.Default.AccessTime,
                            config.duration.format(),
                            onClick = {
                                configView = if (configView == ConfigView.Duration) {
                                    ConfigView.None
                                } else {
                                    ConfigView.Duration
                                }
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        ConfigMenuButton(
                            selected = configView == ConfigView.PromptInfluence,
                            Icons.Default.Lightbulb,
                            formatDecimal(config.promptInfluence, 2),
                            onClick = {
                                configView = if (configView == ConfigView.PromptInfluence) {
                                    ConfigView.None
                                } else {
                                    ConfigView.PromptInfluence
                                }
                            }
                        )
                        Spacer(Modifier.weight(1f))
                        Button(
                            enabled = prompt.isNotBlank(),
                            shape = RoundedCornerShape(50),
                            onClick = {
                                onGenerate(prompt, config)
                            },
                            colors = ButtonDefaults.buttonColors(
                                disabledBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.12f)
                            ),
                        ) {
                            Icon(
                                Icons.Outlined.AutoFixHigh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(stringResource(Res.string.white_noise_start))
                        }
                    }
                }
            }
        }
    )
}

private fun Duration?.format(): String {
    if (this == null) return "Auto"
    return "${formatDecimal((this.inWholeMilliseconds / 1000.0).toFloat(), 2)}s"
}

@Composable
private fun ConfigMenuButton(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    IconButton(onClick = {
        onClick()
    }) {
        val color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = null,
                tint = color.copy(alpha = 0.7f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.caption,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DurationConfig(
    modifier: Modifier,
    duration: Duration?,
    onDurationChange: (Duration?) -> Unit
) {

    var number by remember {
        mutableDoubleStateOf(
            duration?.inWholeMilliseconds?.div(1000.0) ?: 10.0
        )
    }

    Column(modifier.padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "Duration",
            Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.subtitle1
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = duration == null,
                onCheckedChange = { checked ->
                    onDurationChange(if (checked) null else number.seconds)
                },
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary),
            )
            Text("Automatically pick the best duration", style = MaterialTheme.typography.body2)
        }
        Box {
            Row(Modifier.padding(horizontal = 8.dp)) {
                Text("0.5s", style = MaterialTheme.typography.overline)
                Spacer(Modifier.weight(1f))
                Text("22s", style = MaterialTheme.typography.overline)
            }
            Slider(
                enabled = duration != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                value = number.toFloat(),
                onValueChange = {
                    number = it.toDouble()
                    onDurationChange(number.seconds)
                },
                valueRange = 0.5f..22.0f
            )
        }
    }
}

@Composable
private fun PromptInfluenceConfig(
    modifier: Modifier,
    influence: Float,
    onInfluenceChange: (Float) -> Unit
) {
    Column(modifier.padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Prompt Influence",
            Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            text = "Slide the scale to make your generation perfectly adhere to your prompt or allow for a little creativity.",
        )
        Box {
            Row(Modifier.padding(horizontal = 8.dp)) {
                Text("More creative", style = MaterialTheme.typography.overline)
                Spacer(Modifier.weight(1f))
                Text("Follow Prompt", style = MaterialTheme.typography.overline)
            }
            Slider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                value = influence,
                onValueChange = onInfluenceChange,
                valueRange = 0.0f..1.0f,
            )
        }
    }
}