package io.github.gmvalentino8.cyklone.components

import io.github.gmvalentino8.cyklone.contract.Action
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Intent
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState

/**
 * Interpreters receive [Intent] from user input and maps it to an [Action]
 */
interface Interpreter<in I : Intent, out A : Action, VS : ViewState, E : Event> {
    suspend fun interpret(intent: I, state: State<VS, E>): A?
}
