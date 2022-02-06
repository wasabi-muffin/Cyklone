package com.gmvalentino.cyklone.mock

import com.gmvalentino.cyklone.contract.Action

sealed class MockAction : Action {
    data class UpdateText(val text: String) : MockAction()
    data class Submit(val run: () -> Unit = {}) : MockAction()
}
