package com.gmvalentino.cyklone.statemachine.components

import com.gmvalentino.cyklone.components.Reducer
import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.ProcessEventResult
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.SendEventResult
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState
import com.gmvalentino.cyklone.statemachine.contract.SideEffect

open class StateMachineReducer<I : Intent, A : Action, R : Result, VS : ViewState, E : Event, SE : SideEffect>(
    private val stateMachine: StateMachine<I, A, R, VS, E, SE>,
) : Reducer<R, VS, E> {
    @Suppress("UNCHECKED_CAST")
    override suspend fun reduce(result: R, state: State<VS, E>): State<VS, E>? = when (result) {
        is SendEventResult<*> -> state.send(result.event as E)
        is ProcessEventResult<*> -> state.process(result.event as E)
        else -> stateMachine.graph
            .filterKeys { key -> key.matches(state.viewState) }.values
            .flatMap { stateNode -> stateNode.transitions.entries }
            .find { resultMatcher -> resultMatcher.key.matches(result) }
            ?.value?.invoke(state.viewState, result)
            ?.let { state.copy(viewState = it) }
    }
}
