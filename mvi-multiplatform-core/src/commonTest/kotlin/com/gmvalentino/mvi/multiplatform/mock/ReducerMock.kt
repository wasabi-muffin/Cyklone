package com.gmvalentino.mvi.multiplatform.mock

import com.gmvalentino.mvi.multiplatform.components.Reducer
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State

class ReducerMock<RESULT : Result, STATE : State> : Reducer<RESULT, STATE> {
    lateinit var mock: suspend (RESULT, STATE) -> STATE
    override suspend fun reduce(result: RESULT, state: STATE): STATE = mock(result, state)
}
