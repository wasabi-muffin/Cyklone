package io.github.gmvalentino8.cyklone.mock

import io.github.gmvalentino8.cyklone.contract.Intent

sealed class MockIntent : Intent {
    data class UpdateText(val text: String) : MockIntent()
    data class Submit(val run: () -> Unit = {}) : MockIntent()
}
