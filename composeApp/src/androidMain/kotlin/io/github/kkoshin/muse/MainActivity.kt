package io.github.kkoshin.muse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.core.util.Consumer
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    /**
     * NavHost 默认能处理 standard 启动模式的链接,但如果我们期望是非 standard 模式的话，就需要处理 OnNewIntent
     * https://developer.android.com/guide/navigation/design/deep-link#handle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController: NavHostController = rememberNavController()
            MainScreen(navController)

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