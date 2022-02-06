package com.gmvalentino.mvi.multiplatform.statemachine.components

import com.gmvalentino.mvi.multiplatform.components.Interpreter
import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import com.gmvalentino.mvi.multiplatform.contract.ViewState
import com.gmvalentino.mvi.multiplatform.statemachine.contract.SideEffect

open class StateMachineInterpreter<I : Intent, A : Action, R : Result, VS: ViewState, E: Event, SE : SideEffect>(
    private val stateMachine: StateMachine<I, A, R, VS, E, SE>
) : Interpreter<I, A, VS, E> {
    override suspend fun interpret(intent: I, state: State<VS, E>): A? = stateMachine.graph
        .filterKeys { key -> key.matches(state.viewState) }.values
        .flatMap { stateNode -> stateNode.intents.entries }
        .find { intentMatcher -> intentMatcher.key.matches(intent) }
        ?.value?.invoke(state.viewState, intent)
}
