package io.github.gmvalentino8.cyklone.mock

import io.github.gmvalentino8.cyklone.contract.Result

sealed class MockResult : Result {
    object Loading : MockResult()
    data class UpdateText(val text: String) : MockResult()
    data class Error(val throwable: Throwable) : MockResult()
}
