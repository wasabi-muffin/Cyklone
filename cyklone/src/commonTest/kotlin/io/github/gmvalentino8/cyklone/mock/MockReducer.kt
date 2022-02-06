package io.github.gmvalentino8.cyklone.mock

import io.github.gmvalentino8.cyklone.components.Reducer
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Result
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState

class MockReducer<R : Result, VS : ViewState, E : Event> : Reducer<R, VS, E> {
    lateinit var mock: suspend (R, State<VS, E>) -> State<VS, E>
    override suspend fun reduce(result: R, state: State<VS, E>): State<VS, E> = mock(result, state)
}
