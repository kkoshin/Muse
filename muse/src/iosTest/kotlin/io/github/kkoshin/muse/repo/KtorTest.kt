package io.github.kkoshin.muse.repo

import io.github.kkoshin.elevenlabs.ktorClient
import kotlin.test.Test
import kotlin.test.assertNotNull

class KtorTest {

    @Test
    fun testClientInitialization() {
        // This should trigger the actual implementation in iOS (HttpClient(Darwin))
        val client = ktorClient
        assertNotNull(client)
        println("Ktor Client initialized: $client")
    }
}
