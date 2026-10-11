package io.github.kkoshin.muse.app.crash

import android.app.Application
import io.github.kkoshin.muse.app.pushCrashLogShortcut
import xcrash.XCrash

internal fun installCrashReporter(app: Application) {
    XCrash.init(app)
    pushCrashLogShortcut(app)
}
