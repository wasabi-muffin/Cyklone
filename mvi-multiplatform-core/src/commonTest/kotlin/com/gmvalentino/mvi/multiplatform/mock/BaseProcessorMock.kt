package com.gmvalentino.mvi.multiplatform.mock

import com.gmvalentino.mvi.multiplatform.components.BaseProcessor
import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import kotlinx.coroutines.flow.Flow

class BaseProcessorMock<STATE : State, ACTION : Action, RESULT : Result, EVENT : Event> : BaseProcessor<STATE, ACTION, RESULT, EVENT>() {
    lateinit var mock: suspend (STATE, ACTION) -> Flow<RESULT>
    override suspend fun process(action: ACTION, state: STATE): Flow<RESULT> = mock(state, action)
}
