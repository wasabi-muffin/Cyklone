package com.gmvalentino.test

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope

expect abstract class BaseTest() {
    val coroutineContext: CoroutineContext
    fun <T> runTest(block: suspend CoroutineScope.() -> T)
}
