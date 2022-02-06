package com.gmvalentino.cyklone.mock

import com.gmvalentino.cyklone.contract.Result

sealed class MockResult : Result {
    object Loading : MockResult()
    data class UpdateText(val text: String) : MockResult()
    data class Error(val throwable: Throwable) : MockResult()
}
