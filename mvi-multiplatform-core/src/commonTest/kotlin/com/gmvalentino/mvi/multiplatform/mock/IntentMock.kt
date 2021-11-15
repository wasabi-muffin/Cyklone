package com.gmvalentino.mvi.multiplatform.mock

import com.gmvalentino.mvi.multiplatform.contract.Intent

sealed class IntentMock : Intent {
    data class UpdateText(val text: String) : IntentMock()
    data class Submit(val run: () -> Unit = {}) : IntentMock()
}
