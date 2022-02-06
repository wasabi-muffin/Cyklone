package io.github.gmvalentino8.cyklone.components

import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Result
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState

/**
 * [Reducer] receives [Result]s from the [Processor] and creates a new [State]
 */
interface Reducer<in R : Result, VS : ViewState, E : Event> {
    /**
     * A pure function that applies a [Result] to the current [State] and returns a new [State]
     */
    suspend fun reduce(result: R, state: State<VS, E>): State<VS, E>?
}
