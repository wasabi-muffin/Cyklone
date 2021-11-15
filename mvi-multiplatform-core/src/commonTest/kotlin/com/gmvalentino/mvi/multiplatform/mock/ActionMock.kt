package com.gmvalentino.mvi.multiplatform.mock

import com.gmvalentino.mvi.multiplatform.contract.Action

sealed class ActionMock : Action {
    data class UpdateText(val text: String) : ActionMock()
    data class Submit(val run: () -> Unit = {}) : ActionMock()
}
