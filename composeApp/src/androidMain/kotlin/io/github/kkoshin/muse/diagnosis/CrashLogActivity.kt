package io.github.kkoshin.muse.diagnosis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.kkoshin.muse.AppTheme

class CrashLogActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(this) {
                CrashLogScreen()
            }
        }
    }
}