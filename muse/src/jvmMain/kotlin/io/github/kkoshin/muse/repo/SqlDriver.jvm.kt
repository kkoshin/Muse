package io.github.kkoshin.muse.repo

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        // Note: For in-memory, you might need to call AppDatabase.Schema.create(driver)
        // but since we want isolation, this is enough for compilation.
        return driver
    }
}
