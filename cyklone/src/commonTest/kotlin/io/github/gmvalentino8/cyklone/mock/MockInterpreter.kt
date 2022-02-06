package io.github.gmvalentino8.cyklone.mock

import io.github.gmvalentino8.cyklone.components.Interpreter
import io.github.gmvalentino8.cyklone.contract.Action
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Intent
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState

class MockInterpreter<I : Intent, A : Action, VS : ViewState, E : Event> : Interpreter<I, A, VS, E> {
    lateinit var mock: suspend (I, State<VS, E>) -> A?
    override suspend fun interpret(intent: I, state: State<VS, E>): A? = mock(intent, state)
}
