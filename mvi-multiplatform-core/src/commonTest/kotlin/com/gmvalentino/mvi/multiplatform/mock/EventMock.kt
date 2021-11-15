package com.gmvalentino.mvi.multiplatform.mock

import com.gmvalentino.mvi.multiplatform.contract.Event

sealed class EventMock : Event {
    object One : EventMock()
    object Two : EventMock()
}
