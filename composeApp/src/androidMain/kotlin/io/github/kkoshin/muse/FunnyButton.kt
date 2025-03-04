package io.github.kkoshin.muse

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import org.intellij.lang.annotations.Language

/**
 * Android 13 以上的 Button 可以使用 shader 来实现渐变色
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FunnyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier.drawWithCache {
            val shader = RuntimeShader(gradientColorSample)
            shader.setFloatUniform("iResolution",
                size.width, size.height)
            val brush = ShaderBrush(shader)
            onDrawWithContent {
                drawContent()
                drawRoundRect(brush)
            }
        },
        enabled = enabled,
        shape = shape,
        onClick = onClick,
    ) {
        content()
    }
}

@Language("agsl")
private const val solidColorSample = """
    layout(color) uniform half4 iColor;
    half4 main(float2 fragCoord) {
      return iColor;
    }
"""

@Language("agsl")
private const val gradientColorSample = """
    uniform float2 iResolution;
    layout(color) uniform half4 iColor;
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord/iResolution.xy;
        return half4(uv, 0, 1);
    }
"""

@Language("agsl")
private const val gradientColorAnimatedSample = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform float iDuration;
    half4 main(in float2 fragCoord) {
        float2 scaled = 1 - mod(fragCoord/iResolution.xy + iTime/(iDuration/2.0));
        return half4(abs(scaled, 2.0), 0, 1);
    }
"""