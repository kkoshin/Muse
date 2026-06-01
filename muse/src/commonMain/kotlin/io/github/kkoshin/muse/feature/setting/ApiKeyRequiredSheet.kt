package io.github.kkoshin.muse.feature.setting

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt

@Composable
fun ApiKeyRequiredSheet(
    onGoToSettings: () -> Unit,
    onWhatIsApiKey: () -> Unit,
    onDismiss: () -> Unit,
) {
    val offsetY = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, tween(300))
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f * (1f - offsetY.value)))
                    .clickable { onDismiss() },
            )

            // Sheet panel
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .offset { IntOffset(0, (offsetY.value * 1000).roundToInt()) }
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                elevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 20.dp),
                ) {
                    Text(
                        text = "Setup needed to continue",
                        style = MaterialTheme.typography.h6,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "This feature uses 11labs. You'll need to add your own API key in Settings before you can use it.",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onGoToSettings,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                        ),
                    ) {
                        Text("Go to Settings")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButton(
                        onClick = onWhatIsApiKey,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            "What is API Key?",
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                        )
                    }
                }
            }
        }
    }
}
