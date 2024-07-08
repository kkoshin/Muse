package io.github.kkoshin.muse.matrix

import android.content.Context
import com.tencent.matrix.plugin.DefaultPluginListener
import com.tencent.matrix.report.Issue
import com.tencent.matrix.util.MatrixLog
import logcat.logcat

class MatrixPluginListener(
    context: Context?,
) : DefaultPluginListener(context) {
    override fun onReportIssue(issue: Issue) {
        super.onReportIssue(issue)
        MatrixLog.e(TAG, issue.toString())
        // add your code to process data
        logcat {
            "MatrixPluginListener onReportIssue:$issue"
        }
    }

    companion object {
        const val TAG: String = "Matrix.TestPluginListener"
    }
}