package io.github.kkoshin.muse.repo

import io.github.kkoshin.muse.database.AppDatabase
import kotlin.test.Test
import kotlin.test.assertNotNull

class SqlDriverTest {
    @Test
    fun testDriverCreation() {
        val factory = DriverFactory()
        val driver = factory.createDriver()
        assertNotNull(driver)
        
        // Let's also try to create the database if possible
        // but we might need to manually create schema for JDBC in-memory
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        assertNotNull(database)
    }
}
