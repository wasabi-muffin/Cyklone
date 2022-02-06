package com.gmvalentino.cyklone.mock

import com.gmvalentino.cyklone.components.Interpreter
import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState

class MockInterpreter<I : Intent, A : Action, VS : ViewState, E : Event> : Interpreter<I, A, VS, E> {
    lateinit var mock: suspend (I, State<VS, E>) -> A?
    override suspend fun interpret(intent: I, state: State<VS, E>): A? = mock(intent, state)
}
