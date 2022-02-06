package com.gmvalentino.mvi.multiplatform.statemachine.middleware

import co.touchlab.kermit.Logger
import com.gmvalentino.mvi.multiplatform.middleware.ActionMiddleware
import com.gmvalentino.mvi.multiplatform.middleware.IntentMiddleware
import com.gmvalentino.mvi.multiplatform.middleware.ResultMiddleware
import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.ProcessEventResult
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.SendEventResult
import com.gmvalentino.mvi.multiplatform.contract.State
import com.gmvalentino.mvi.multiplatform.contract.ViewState
import com.gmvalentino.mvi.multiplatform.statemachine.components.StateMachine
import com.gmvalentino.mvi.multiplatform.statemachine.contract.SideEffect
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
