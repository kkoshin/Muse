package io.github.kkoshin.muse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.DisposableEffect
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
        setContent {
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            val navController = rememberNavController(bottomSheetNavigator)

            AppTheme(this) {
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
