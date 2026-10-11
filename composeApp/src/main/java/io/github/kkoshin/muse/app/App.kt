package io.github.kkoshin.muse.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.StrictMode
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import io.github.kkoshin.muse.app.crash.installCrashReporter
import io.github.kkoshin.muse.app.diagnosis.CrashLogActivity
import io.github.kkoshin.muse.appModule
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
        installCrashReporter(this)
    }
}

/**
 * 崩溃日志快捷方式：只被 release/debug 变体的崩溃上报接缝调用。
 * play 变体里此处不可达，连同 [CrashLogActivity] 一起由 R8 裁掉。
 */
internal fun pushCrashLogShortcut(context: Context) {
    val shortcut = ShortcutInfoCompat.Builder(context, "id-crash-log")
        .setShortLabel("Crash Log")
        .setIcon(IconCompat.createWithResource(context, R.drawable.ic_dev_tools_shotcut))
        .setIntent(
            Intent(context, CrashLogActivity::class.java).apply {
                action = Intent.ACTION_VIEW
            }
        )
        .build()

    ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
}