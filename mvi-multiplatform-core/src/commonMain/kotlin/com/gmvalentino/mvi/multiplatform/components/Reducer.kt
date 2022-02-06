package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import com.gmvalentino.mvi.multiplatform.contract.ViewState

/**
 * [Reducer] receives [Result]s from the [Processor] and creates a new [State]
 */
interface Reducer<in R : Result, VS : ViewState, E : Event> {
    /**
     * A pure function that applies a [Result] to the current [State] and returns a new [State]
     */
    suspend fun reduce(result: R, state: State<VS, E>): State<VS, E>?
}
