package com.gmvalentino.mvi.multiplatform.mock

import com.gmvalentino.mvi.multiplatform.contract.Result

sealed class ResultMock : Result {
    object Loading : ResultMock()
    data class UpdateText(val text: String) : ResultMock()
    data class Error(val throwable: Throwable) : ResultMock()
}
