package io.github.gmvalentino8.cyklone.mock

import io.github.gmvalentino8.cyklone.contract.ViewState

sealed class MockViewState : ViewState {
    object None : MockViewState()
    object Loading : MockViewState()
    data class Stable(val text: String) : MockViewState()
    data class Error(val throwable: Throwable) : MockViewState()
}
