package com.gmvalentino.cyklone.middleware

import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * [Middleware] apply transformations on a certain stream
 */
sealed interface Middleware

/**
 * Apply transformations on [Intent] streams
 */
fun interface IntentMiddleware<I : Intent, VS : ViewState, E : Event> : Middleware {
    fun modifyIntents(input: Flow<I>, state: StateFlow<State<VS, E>>): Flow<I>
}

/**
 * Apply transformations on [Action] streams
 */
fun interface ActionMiddleware<A : Action, VS : ViewState, E : Event> : Middleware {
    fun modifyActions(input: Flow<A>, state: StateFlow<State<VS, E>>): Flow<A>
}

/**
 * Apply transformations on [Result] streams
 */
fun interface ResultMiddleware<R : Result, VS : ViewState, E : Event> : Middleware {
    fun modifyResults(input: Flow<R>, state: StateFlow<State<VS, E>>): Flow<R>
}

/**
 * Apply transformations on [State] streams
 */
fun interface StateMiddleware<VS : ViewState, E : Event> : Middleware {
    fun modifyStates(input: Flow<State<VS, E>>): Flow<State<VS, E>>
}
