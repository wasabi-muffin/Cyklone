package io.github.gmvalentino8.cyklone.mock

import io.github.gmvalentino8.cyklone.contract.Event

sealed class MockEvent : Event() {
    object One : MockEvent()
    object Two : MockEvent()
}
