package io.github.gmvalentino8.cyklone.components

import io.github.gmvalentino8.cyklone.contract.Action
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Result
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState
import kotlinx.coroutines.flow.Flow

/**
 * Processors receives [Action] from the [Interpreter] and processes it
 */
interface Processor<A : Action, R : Result, VS : ViewState, E : Event> {
    /**
     * Execute an [Action] given a current [State] and returns a stream of [Result]
     *
     * Side-effects should be processed here (i.e. Usecases, Repository)
     */
    suspend fun process(action: A, state: State<VS, E>): Flow<R>
}
