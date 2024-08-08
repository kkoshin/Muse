package io.github.kkoshin.muse.firebase.inapp

import android.app.Activity
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplayCallbacks
import com.google.firebase.inappmessaging.display.internal.FirebaseInAppMessagingDisplayImpl
import com.google.firebase.inappmessaging.ktx.inAppMessaging
import com.google.firebase.inappmessaging.model.InAppMessage
import com.google.firebase.ktx.Firebase
import logcat.logcat

class CustomFirebaseInAppMessagingDisplay : FirebaseInAppMessagingDisplayImpl() {

    override fun displayMessage(
        inAppMessage: InAppMessage,
        callbacks: FirebaseInAppMessagingDisplayCallbacks
    ) {
        logcat("Firebase") {
            "[${inAppMessage.messageType}] displayMessage invoked!"
        }
        // show your custom UI here
        callbacks.impressionDetected()
    }

    // 必须在 onResume 的时候设置，否则依旧调用的是 Firebase 的默认实现
    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        Firebase.inAppMessaging.setMessageDisplayComponent(this)
    }
}