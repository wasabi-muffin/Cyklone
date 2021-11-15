package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import kotlinx.coroutines.flow.Flow

/**
 * Processors receives [Action] from the [Interpreter] and processes it
 */
interface Processor<in STATE : State, ACTION : Action, RESULT : Result, EVENT : Event> {
    /**
     * Stream of [Event] for one-shot events during processing
     */
    val events: Flow<EVENT>

    /**
     * Execute an [Action] given a current [State] and returns a stream of [Result]
     *
     * Side-effects should be processed here (i.e. Usecases, Repository)
     */
    suspend fun process(
        action: ACTION,
        state: STATE
    ): Flow<RESULT>

    /**
     * Publishes an [Event]
     */
    suspend fun publish(event: EVENT)
}
