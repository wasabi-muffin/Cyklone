package com.gmvalentino.cyklone.mock

import com.gmvalentino.cyklone.components.Processor
import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState
import kotlinx.coroutines.flow.Flow

class MockProcessor<A : Action, R : Result, VS : ViewState, E : Event> : Processor<A, R, VS, E> {
    lateinit var mock: suspend (A, State<VS, E>) -> Flow<R>
    override suspend fun process(action: A, state: State<VS, E>): Flow<R> = mock(action, state)
}
