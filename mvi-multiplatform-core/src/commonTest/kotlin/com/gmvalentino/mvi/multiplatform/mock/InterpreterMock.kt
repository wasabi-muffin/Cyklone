package com.gmvalentino.mvi.multiplatform.mock

import com.gmvalentino.mvi.multiplatform.components.Interpreter
import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Intent

class InterpreterMock<INTENT : Intent, ACTION : Action> : Interpreter<INTENT, ACTION> {
    lateinit var mock: suspend (INTENT) -> ACTION
    override suspend fun interpret(intent: INTENT): ACTION = mock(intent)
}
