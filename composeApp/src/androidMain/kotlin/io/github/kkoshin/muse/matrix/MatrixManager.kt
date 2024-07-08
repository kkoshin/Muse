package io.github.kkoshin.muse.matrix

import android.app.Application
import android.content.Context
import com.tencent.matrix.Matrix
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.config.TraceConfig
import com.tencent.matrix.util.MatrixLog
import java.io.File

object MatrixManager {
    private const val TAG = "MatrixManager"

    fun init(application: Application) {
        MatrixLog.i(TAG, "Start Matrix configurations.")
        val dynamicConfig = DynamicConfigImpl()
        val tracePlugin: TracePlugin = configureTracePlugin(application, dynamicConfig)

        val builder = Matrix
            .Builder(application)
            .pluginListener(MatrixPluginListener(application))
            .plugin(tracePlugin)

        Matrix.init(builder.build())

        // Trace Plugin need call start() at the beginning.
        tracePlugin.start()
        MatrixLog.i(TAG, "Matrix configurations done.")
    }

    private fun configureTracePlugin(
        context: Context,
        dynamicConfig: DynamicConfigImpl,
    ): TracePlugin {
        val fpsEnable: Boolean = dynamicConfig.isFPSEnable
        val traceEnable: Boolean = dynamicConfig.isTraceEnable
        val signalAnrTraceEnable: Boolean = dynamicConfig.isSignalAnrTraceEnable

        val traceFileDir = File(context.applicationContext.getExternalFilesDir(null), "matrix_trace")
        if (!traceFileDir.exists()) {
            if (traceFileDir.mkdirs()) {
                MatrixLog.e(TAG, "failed to create traceFileDir")
            }
        }

        val anrTraceFile = File(
            traceFileDir,
            "anr_trace",
        ) // path : /data/user/0/sample.tencent.matrix/files/matrix_trace/anr_trace
        val printTraceFile = File(
            traceFileDir,
            "print_trace",
        ) // path : /data/user/0/sample.tencent.matrix/files/matrix_trace/print_trace

        val traceConfig: TraceConfig = TraceConfig
            .Builder()
            .dynamicConfig(dynamicConfig)
            .enableFPS(fpsEnable)
            .enableEvilMethodTrace(traceEnable)
            .enableAnrTrace(traceEnable)
            .enableStartup(traceEnable)
            .enableIdleHandlerTrace(traceEnable) // Introduced in Matrix 2.0
//            .enableMainThreadPriorityTrace(true) // Introduced in Matrix 2.0
            .enableSignalAnrTrace(signalAnrTraceEnable) // Introduced in Matrix 2.0
            .anrTracePath(anrTraceFile.absolutePath)
            .printTracePath(printTraceFile.absolutePath)
            .splashActivities("io.github.kkoshin.muse.MainActivity;")
            .isDebug(true)
            .isDevEnv(false)
            .build()

        // Another way to use SignalAnrTracer separately
        // useSignalAnrTraceAlone(anrTraceFile.getAbsolutePath(), printTraceFile.getAbsolutePath());
        return TracePlugin(traceConfig)
    }
}