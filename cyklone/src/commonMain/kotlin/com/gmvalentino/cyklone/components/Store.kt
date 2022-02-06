package com.gmvalentino.cyklone.components

import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

/**
 * The store exposes a stream of [State] and a stream of [Event] for the client to react to.
 */
interface Store<I : Intent, VS : ViewState, E : Event> {
    /**
     * Stream of [State] exposed to the client
     */
    val state: StateFlow<State<VS, E>>

    /**
     * Dispatches an [Intent]
     */
    fun dispatch(intent: I)

    /**
     * Cancels all jobs within the store
     */
    fun dispose()

    /**
     * Collect used for Kotlin Native
     */
    fun collect(
        onState: ((State<VS, E>) -> Unit),
    ): Job
}
