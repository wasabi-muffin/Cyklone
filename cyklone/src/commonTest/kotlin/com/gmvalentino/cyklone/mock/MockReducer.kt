package com.gmvalentino.cyklone.mock

import com.gmvalentino.cyklone.components.Reducer
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState

class MockReducer<R : Result, VS : ViewState, E : Event> : Reducer<R, VS, E> {
    lateinit var mock: suspend (R, State<VS, E>) -> State<VS, E>
    override suspend fun reduce(result: R, state: State<VS, E>): State<VS, E> = mock(result, state)
}
