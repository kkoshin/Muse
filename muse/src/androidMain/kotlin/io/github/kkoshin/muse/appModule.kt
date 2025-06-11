package io.github.kkoshin.muse

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.notification.toast
import com.github.foodiestudio.sugar.storage.AppFileHelper
import io.github.kkoshin.muse.core.manager.AccountManager
import io.github.kkoshin.muse.core.manager.ElevenLabProcessor
import io.github.kkoshin.muse.core.manager.SpeechProcessorManager
import io.github.kkoshin.muse.core.provider.AudioIsolationProvider
import io.github.kkoshin.muse.core.provider.STTProvider
import io.github.kkoshin.muse.core.provider.SoundEffectProvider
import io.github.kkoshin.muse.core.provider.TTSProvider
import io.github.kkoshin.muse.database.AppDatabase
import io.github.kkoshin.muse.feature.dashboard.DashboardViewModel
import io.github.kkoshin.muse.feature.editor.EditorViewModel
import io.github.kkoshin.muse.feature.export.ExportViewModel
import io.github.kkoshin.muse.feature.isolation.AudioIsolationViewModel
import io.github.kkoshin.muse.feature.noise.WhiteNoiseViewModel
import io.github.kkoshin.muse.feature.stt.SttViewModel
import io.github.kkoshin.muse.platformbridge.MediaStoreHelper
import io.github.kkoshin.muse.platformbridge.ToastManager
import io.github.kkoshin.muse.repo.DriverFactory
import io.github.kkoshin.muse.repo.MusePathManager
import io.github.kkoshin.muse.repo.MuseRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(name = "account")

@OptIn(ExperimentalSugarApi::class)
internal val baseModule = module {
    single<MuseRepo> {
        MuseRepo(
            AppDatabase(DriverFactory(get()).createDriver()),
            MusePathManager(get()),
        )
    }
    viewModelOf(::EditorViewModel)
    viewModelOf(::ExportViewModel)
    viewModel { DashboardViewModel(get()) }
    viewModel { AudioIsolationViewModel(get()) }
    viewModel { SttViewModel(get()) }
    viewModel { WhiteNoiseViewModel(get()) }
    singleOf(::SpeechProcessorManager)
    singleOf(::MediaStoreHelper)
    singleOf(::AppFileHelper)
    single<AccountManager> {
        val context = get<Context>()
        AccountManager(context.accountDataStore)
    }
    single<CoroutineScope> { MainScope() }
    single<ToastManager> {
        val context = get<Context>()
        object : ToastManager {
            override fun show(message: String?) {
                if (message != null) {
                    context.toast(message)
                }
            }
        }
    }
}

val appModule = module {
    includes(baseModule)
    single<TTSProvider> {
        ElevenLabProcessor(get(), get())
    }
    single<AudioIsolationProvider> {
        ElevenLabProcessor(get(), get())
    }
    single<SoundEffectProvider> {
        ElevenLabProcessor(get(), get())
    }
    single<STTProvider> {
        ElevenLabProcessor(get(), get())
    }
}