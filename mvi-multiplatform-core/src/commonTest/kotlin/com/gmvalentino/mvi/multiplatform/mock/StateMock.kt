package com.gmvalentino.mvi.multiplatform.mock

import com.gmvalentino.mvi.multiplatform.contract.State

sealed class StateMock : State {
    object None : StateMock()
    object Loading : StateMock()
    data class Stable(val text: String) : StateMock()
    data class Error(val throwable: Throwable) : StateMock()
}
