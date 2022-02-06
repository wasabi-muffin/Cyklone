package com.gmvalentino.cyklone.components

import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState

/**
 * [Reducer] receives [Result]s from the [Processor] and creates a new [State]
 */
interface Reducer<in R : Result, VS : ViewState, E : Event> {
    /**
     * A pure function that applies a [Result] to the current [State] and returns a new [State]
     */
    suspend fun reduce(result: R, state: State<VS, E>): State<VS, E>?
}
