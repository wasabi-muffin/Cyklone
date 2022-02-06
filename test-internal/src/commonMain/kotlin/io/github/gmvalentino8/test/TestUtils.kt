package io.github.gmvalentino8.test

import app.cash.turbine.FlowTurbine

suspend fun <T> FlowTurbine<T>.awaitItemAssert(assertions: T.() -> Unit) {
    assertions(awaitItem())
}
