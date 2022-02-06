package io.github.gmvalentino8.cyklone.mock

import io.github.gmvalentino8.cyklone.components.Processor
import io.github.gmvalentino8.cyklone.contract.Action
import io.github.gmvalentino8.cyklone.contract.Result
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState
import kotlinx.coroutines.flow.Flow

class MockProcessor<A : Action, R : Result, VS : ViewState, E : Event> : Processor<A, R, VS, E> {
    lateinit var mock: suspend (A, State<VS, E>) -> Flow<R>
    override suspend fun process(action: A, state: State<VS, E>): Flow<R> = mock(action, state)
}
