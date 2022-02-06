package io.github.gmvalentino8.cyklone.mock

import io.github.gmvalentino8.cyklone.contract.Action

sealed class MockAction : Action {
    data class UpdateText(val text: String) : MockAction()
    data class Submit(val run: () -> Unit = {}) : MockAction()
}
