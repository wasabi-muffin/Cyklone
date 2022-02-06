package com.gmvalentino.cyklone.statemachine.middleware

import co.touchlab.kermit.Logger
import com.gmvalentino.cyklone.middleware.ActionMiddleware
import com.gmvalentino.cyklone.middleware.IntentMiddleware
import com.gmvalentino.cyklone.middleware.ResultMiddleware
import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.ProcessEventResult
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.SendEventResult
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState
import com.gmvalentino.cyklone.statemachine.components.StateMachine
import com.gmvalentino.cyklone.statemachine.contract.SideEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter

class StateMachineMiddleware<I : Intent, A : Action, R : Result, VS : ViewState, E : Event, SE : SideEffect>(
    private val stateMachine: StateMachine<I, A, R, VS, E, SE>,
) : IntentMiddleware<I, VS, E>, ActionMiddleware<A, VS, E>, ResultMiddleware<R, VS, E> {
    override fun modifyIntents(input: Flow<I>, state: StateFlow<State<VS, E>>): Flow<I> = input.filter { intent ->
        stateMachine.graph
            .filterKeys { keys -> keys.matches(state.value.viewState) }.values
            .flatMap { node -> node.intents.keys }
            .any { intentMatcher -> intentMatcher.matches(intent) }
            .also { isValid ->
                if (!isValid) Logger.e { "Invalid transition:\nState: ${state.value::class.qualifiedName}\nIntent: ${intent::class.qualifiedName}" }
            }
    }

    override fun modifyActions(input: Flow<A>, state: StateFlow<State<VS, E>>): Flow<A> = input.filter { action ->
        stateMachine.graph
            .filterKeys { keys -> keys.matches(state.value.viewState) }.values
            .flatMap { node -> node.actions.keys }
            .any { actionMatcher -> actionMatcher.matches(action) }
            .also { isValid ->
                if (!isValid) Logger.e { "Invalid transition:\nState: ${state.value::class.qualifiedName}\nAction: ${action::class.qualifiedName}" }
            }
    }

    override fun modifyResults(input: Flow<R>, state: StateFlow<State<VS, E>>): Flow<R> = input.filter { result ->
        result is SendEventResult<*> || result is ProcessEventResult<*> || stateMachine.graph
            .filterKeys { keys -> keys.matches(state.value.viewState) }.values
            .flatMap { node -> node.transitions.entries }
            .any { resultMatcher -> resultMatcher.key.matches(result) }
            .also { isValid ->
                if (!isValid) Logger.e { "Invalid transition:\nState: ${state.value::class.qualifiedName}\nResult: ${result::class.qualifiedName}" }
            }
    }
}
