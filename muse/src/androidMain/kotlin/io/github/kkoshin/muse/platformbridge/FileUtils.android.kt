package io.github.kkoshin.muse.platformbridge

import android.content.Context
import android.content.Intent
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Sink
import okio.sink
import org.koin.java.KoinJavaComponent

/**
 * 分享音频文件
 */
actual fun shareAudioFile(path: Path): Result<Unit> {
    val context: Context = KoinJavaComponent.get(Context::class.java)
    return runCatching {
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "audio/mpeg"
            putExtra(Intent.EXTRA_STREAM, path.toUri())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(this)
        }
    }
}

actual fun openFile(path: Path): Result<Unit> {
    val context: Context = KoinJavaComponent.get(Context::class.java)
    return runCatching {
        Intent(Intent.ACTION_VIEW).let {
            it.setDataAndType(path.toUri(), "audio/*")
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
}

@OptIn(ExperimentalSugarApi::class)
actual fun createCacheFile(fileName: String, sensitive: Boolean): Path {
    check(fileName.isNotEmpty())
    val appFileHelper = KoinJavaComponent.get<AppFileHelper>(AppFileHelper::class.java)
    return appFileHelper.requireCacheDir(sensitive).resolve(fileName).toOkioPath()
}

actual fun Path.toSink(): Sink {
    val str = this.toString()
    return if (str.startsWith("content:/")) {
        val context: Context = KoinJavaComponent.get(Context::class.java)
        context.contentResolver
            .openOutputStream(this.toUri())!!
            .sink()
    } else {
        SystemFileSystem.sink(this)
    }
}