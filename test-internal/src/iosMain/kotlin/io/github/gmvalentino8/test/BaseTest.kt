package io.github.gmvalentino8.test

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.CoreFoundation.CFRunLoopGetCurrent
import platform.CoreFoundation.CFRunLoopRun
import platform.CoreFoundation.CFRunLoopStop

actual abstract class BaseTest {
    actual val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    @OptIn(DelicateCoroutinesApi::class)
    actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
        var error: Throwable? = null
        GlobalScope.launch(coroutineContext) {
            try {
                block()
            } catch (t: Throwable) {
                error = t
            } finally {
                CFRunLoopStop(CFRunLoopGetCurrent())
            }
        }
        CFRunLoopRun()
        error?.also { throw it }
    }
}
