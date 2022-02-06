package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import com.gmvalentino.mvi.multiplatform.contract.ViewState
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
