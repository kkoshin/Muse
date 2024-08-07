package io.github.kkoshin.muse.firebase.fcm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.foodiestudio.sugar.notification.toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import io.github.kkoshin.muse.debugLog
import kotlinx.serialization.Serializable
import logcat.asLog

@Serializable
object FCMSettingArgs

@Composable
fun FCMSettingScreen(modifier: Modifier = Modifier) {
    var fcmToken by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                debugLog {
                    "Fetching FCM registration token failed\n" + task.exception?.asLog()
                }
                context.toast("Fetching FCM registration token failed")
                return@OnCompleteListener
            }

            debugLog {
                "fcmToken: ${task.result}"
            }

            // Get new FCM registration token
            fcmToken = task.result
        })
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                title = { Text(text = "FCM") },
            )
        },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Text(text = "FCM Token")
            Text(text = fcmToken)
        }
    }

}