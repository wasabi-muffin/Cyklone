package com.gmvalentino.cyklone.mock

import com.gmvalentino.cyklone.contract.Intent

sealed class MockIntent : Intent {
    data class UpdateText(val text: String) : MockIntent()
    data class Submit(val run: () -> Unit = {}) : MockIntent()
}
