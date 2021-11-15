package com.gmvalentino.test

import app.cash.turbine.FlowTurbine

suspend fun <T> FlowTurbine<T>.awaitItemAssert(assertions: T.() -> Unit) {
    assertions(awaitItem())
}
