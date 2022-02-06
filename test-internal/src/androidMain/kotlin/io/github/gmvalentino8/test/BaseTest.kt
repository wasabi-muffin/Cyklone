package io.github.gmvalentino8.test

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

actual abstract class BaseTest {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()
    actual val coroutineContext: CoroutineContext
        get() = coroutineTestRule.coroutineContext

    @OptIn(ExperimentalCoroutinesApi::class)
    actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
        coroutineTestRule.runBlockingTest {
            block()
        }
    }
}
