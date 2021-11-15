package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State

/**
 * [Reducer] receives [Result]s from the [Processor] and creates a new [State]
 */
interface Reducer<in RESULT : Result, STATE : State> {
    /**
     * A pure function that applies a [Result] to the current [State] and returns a new [State]
     */
    suspend fun reduce(result: RESULT, state: STATE): STATE
}
