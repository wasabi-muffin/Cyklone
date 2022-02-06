package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.State
import com.gmvalentino.mvi.multiplatform.contract.ViewState

/**
 * Interpreters receive [Intent] from user input and maps it to an [Action]
 */
interface Interpreter<in I : Intent, out A : Action, VS : ViewState, E : Event> {
    suspend fun interpret(intent: I, state: State<VS, E>): A?
}
