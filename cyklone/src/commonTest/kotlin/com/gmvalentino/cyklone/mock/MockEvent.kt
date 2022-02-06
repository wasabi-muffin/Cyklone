package com.gmvalentino.cyklone.mock

import com.gmvalentino.cyklone.contract.Event

sealed class MockEvent : Event() {
    object One : MockEvent()
    object Two : MockEvent()
}
