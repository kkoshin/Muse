package io.github.kkoshin.muse

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.inappmessaging.inAppMessaging
import com.google.firebase.inappmessaging.model.BannerMessage
import com.google.firebase.inappmessaging.model.CardMessage
import com.google.firebase.inappmessaging.model.ImageOnlyMessage
import com.google.firebase.inappmessaging.model.InAppMessage
import com.google.firebase.inappmessaging.model.MessageType
import com.google.firebase.inappmessaging.model.ModalMessage
import io.github.kkoshin.muse.editor.EditorViewModel
import io.github.kkoshin.muse.firebase.inapp.CustomFirebaseInAppMessagingDisplay
import io.github.kkoshin.muse.tts.TTSManager
import io.github.kkoshin.muse.tts.TTSProvider
import io.github.kkoshin.muse.tts.vendor.ElevenLabTTSProvider
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class App : Application() {
    private val appModule = module {
        single<TTSProvider> {
//            MockTTSProvider()
            ElevenLabTTSProvider()
        }
        singleOf(::MuseRepo)
        viewModel { EditorViewModel(get(), get()) }
        singleOf(::TTSManager)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
        initFirebaseConfig()
    }

    private fun initFirebaseConfig() {
        val tag = "Firebase"

        registerActivityLifecycleCallbacks(CustomFirebaseInAppMessagingDisplay())

        Firebase.inAppMessaging.apply {
            addImpressionListener {
                logcat(tag) {
                    """
                            impressionDetected[${it.campaignMetadata?.campaignId}]: ${it.campaignMetadata?.campaignName}
                            ${getInAppMessageLog(it)}
                        """.trimIndent()
                }
            }
            // 带 url 的按钮会触发点击事件，反之走的是 dismiss 回调
            addClickListener { inAppMessage, action ->
                logcat(tag) {
                    """
                        messageClicked[${inAppMessage.campaignMetadata?.campaignId}]: ${inAppMessage.campaignMetadata?.campaignName}
                        ${getInAppMessageLog(inAppMessage)}
                        action: ${action.actionUrl}
                    """.trimIndent()
                }
            }
            addDismissListener {
                logcat(tag) {
                    """
                        messageDismissed[${it.campaignMetadata?.campaignId}]: ${it.campaignMetadata?.campaignName}
                    """.trimIndent()
                }
            }
        }
    }
}

private fun getInAppMessageLog(inAppMessage: InAppMessage): String {
    return when (inAppMessage.messageType) {
        MessageType.CARD -> {
            with(inAppMessage as CardMessage) {
                """
                    title: ${title.text}
                    body: ${body?.text}
                    background: ${getBackgroundHexColor()}
                    primaryAction: ${primaryAction.button?.text}
                    secondaryAction: ${secondaryAction?.button?.text}
                """.trimIndent()
            }
        }

        MessageType.MODAL -> {
            with(inAppMessage as ModalMessage) {
                """
                    title: ${title.text}
                    body: ${body?.text}
                    background: ${getBackgroundHexColor()}
                """.trimIndent()
            }
        }

        MessageType.IMAGE_ONLY -> {
            with(inAppMessage as ImageOnlyMessage) {
                """
                    image: ${imageData.imageUrl}
                """.trimIndent()
            }
        }

        MessageType.BANNER -> {
            with(inAppMessage as BannerMessage) {
                """
                    title: ${title.text}
                    body: ${body?.text}
                    background: ${getBackgroundHexColor()}
                """.trimIndent()
            }
        }

        else -> {
            ""
        }
    }
}