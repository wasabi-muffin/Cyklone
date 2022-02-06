package com.gmvalentino.cyklone.components

import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState

/**
 * Interpreters receive [Intent] from user input and maps it to an [Action]
 */
interface Interpreter<in I : Intent, out A : Action, VS : ViewState, E : Event> {
    suspend fun interpret(intent: I, state: State<VS, E>): A?
}
