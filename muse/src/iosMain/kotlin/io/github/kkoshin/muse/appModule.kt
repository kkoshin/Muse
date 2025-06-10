package io.github.kkoshin.muse

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.manager.ElevenLabProcessor
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.database.AppDatabase
import io.github.kkoshin.muse.feature.dashboard.DashboardViewModel
import io.github.kkoshin.muse.feature.editor.EditorViewModel
import io.github.kkoshin.muse.platformbridge.MediaStoreHelper
import io.github.kkoshin.muse.platformbridge.ToastManager
import io.github.kkoshin.muse.repo.DriverFactory
import io.github.kkoshin.muse.repo.MusePathManager
import io.github.kkoshin.muse.repo.MuseRepo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import okio.Path.Companion.toPath
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

val appModule = module {
    single<CoroutineScope> { MainScope() }
    single<TTSProvider> {
        ElevenLabProcessor(get(), get())
    }
    single {
        SpeechProcessorManager(get(), get(), preferencesDataStore("voices"))
    }
    singleOf(::MediaStoreHelper)
    viewModel {
        DashboardViewModel(get())
    }
    viewModelOf(::EditorViewModel)
    single<MuseRepo> {
        MuseRepo(
            AppDatabase(DriverFactory().createDriver()),
            MusePathManager(),
        )
    }
    single<AccountManager> {
        AccountManager(preferencesDataStore("account"))
    }
    single<ToastManager> {
        object : ToastManager {
            override fun show(message: String?) {
                if (message != null) {
                    // TODO: Implement this
                    println(message)
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun preferencesDataStore(name: String): DataStore<Preferences> {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    val dataStoreFileName = "$name.preferences_pb"
    return PreferenceDataStoreFactory.createWithPath(produceFile = {
        (requireNotNull(documentDirectory).path + "/" + dataStoreFileName).toPath(normalize = false)
    })
}