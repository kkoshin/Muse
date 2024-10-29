package io.github.kkoshin.muse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

class MainActivity : ComponentActivity() {
    /**
     * NavHost 默认能处理 standard 启动模式的链接,但如果我们期望是非 standard 模式的话，就需要处理 OnNewIntent
     * https://developer.android.com/guide/navigation/design/deep-link#handle
     */
    @OptIn(ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            val navController = rememberNavController(bottomSheetNavigator)

            val darkTheme = isSystemInDarkTheme()

            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }

            MaterialTheme(
                colors = if (isSystemInDarkTheme()) {
                    darkColors()
                } else {
                    lightColors(
                        primary = Color(
                            0xFF5D9CED,
                        ),
                    )
                },
            ) {
                ModalBottomSheetLayout(
                    bottomSheetNavigator,
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                ) {
                    MainScreen(navController)
                }
            }

            DisposableEffect(navController) {
                val consumer = Consumer<Intent> { intent ->
                    navController.handleDeepLink(intent)
                }
                this@MainActivity.addOnNewIntentListener(consumer)
                onDispose {
                    this@MainActivity.removeOnNewIntentListener(consumer)
                }
            }
        }
    }
}

private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)