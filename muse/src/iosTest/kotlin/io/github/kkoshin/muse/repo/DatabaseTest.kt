package io.github.kkoshin.muse.repo

import io.github.kkoshin.muse.database.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DatabaseTest {

    @Test
    fun testDatabaseCreation() {
        val driver = DriverFactory().createDriver()
        assertNotNull(driver)

        val database = AppDatabase(driver)
        val queries = database.scriptQueries

        // Test Insert
        queries.insertScript(
            id = "1",
            title = "Test Script",
            text = "This is a test script",
            created_At = 1234567890
        )

        // Test Query
        val script = queries.queryScirptById("1").executeAsOne()
        assertEquals("Test Script", script.title)
        assertEquals("This is a test script", script.text)
    }
}
