package io.github.kkoshin.muse.firebase.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import logcat.logcat

/**
 * 在后台应用中处理通知消息
 * 当您的应用在后台运行时，Android 会将通知消息传送至系统任务栏。默认情况下，用户点按通知即可打开应用启动器。
 *
 * 此类消息包括既具有通知也具有数据载荷的消息（以及所有从 Notifications 控制台发送的消息）。
 * 在这些情况下，通知将传送至设备的系统任务栏，数据载荷则传送至启动器 Activity 的 intent 的 extras 属性。
 */
class MessageService : FirebaseMessagingService () {

    private val tag = "FCM"

    /**
     * 满足以下任一条件会触发这个回调，并且这个回调不能执行时长不能太久
     * 1. 应用在前台的情况下收到推送
     * 2. 应用不在前台，收到的推送不包含 notification, 仅包含 data
     */
    override fun onMessageReceived(message: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        logcat(tag) {
            "From: ${message.from}"
        }

        // Check if message contains a data payload.
        if (message.data.isNotEmpty()) {
            logcat(tag) {
                "Message data payload: ${message.data}"
            }
            // Check if data needs to be processed by long running job
//            if (needsToBeScheduled()) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
        }

        // Check if message contains a notification payload.
        message.notification?.let {
            logcat(tag) {
                "Message Notification Body: ${it.body}"
            }
        }
    }

    /**
     * 该推送可能依旧过期了，需要的话可以做一些处理
     */
    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    /**
     * 注册令牌可能会在发生下列情况时更改：
     *
     *  - 应用在新设备上恢复
     *  - 用户卸载/重新安装应用
     *  - 用户清除应用数据。
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logcat(tag) {
            "onNewToken: $token"
        }
    }
}