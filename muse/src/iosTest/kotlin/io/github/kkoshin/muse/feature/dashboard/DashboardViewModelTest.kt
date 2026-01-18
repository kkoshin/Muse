@file:OptIn(ExperimentalUuidApi::class)

package io.github.kkoshin.muse.feature.dashboard

import io.github.kkoshin.muse.repo.MuseRepo
import io.github.kkoshin.muse.repo.model.Script
import kotlinx.coroutines.test.runTest
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.ExperimentalUuidApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import io.github.kkoshin.muse.database.AppDatabase
import io.github.kkoshin.muse.repo.DriverFactory
import io.github.kkoshin.muse.repo.MusePathManager
import kotlinx.coroutines.flow.first
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class DashboardViewModelTest : KoinTest {

    private lateinit var repo: MuseRepo

    @BeforeTest
    fun setup() {
        startKoin {
            modules(module {
                single { MuseRepo(AppDatabase(DriverFactory().createDriver()), MusePathManager()) }
            })
        }
        repo = get()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testLoadScripts() = runTest {
        val viewModel = DashboardViewModel(repo)
        
        val testScript = Script(text = "Test Content")
        repo.insertScript(testScript)
        
        viewModel.loadScripts()
        
        val scripts = viewModel.scripts.first { it.isNotEmpty() }
        assertNotNull(scripts)
        assertEquals(1, scripts.size)
        assertEquals("Test Content", scripts[0].text)
    }
}