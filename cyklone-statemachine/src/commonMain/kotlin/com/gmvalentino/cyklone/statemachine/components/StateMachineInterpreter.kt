package com.gmvalentino.cyklone.statemachine.components

import com.gmvalentino.cyklone.components.Interpreter
import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState
import com.gmvalentino.cyklone.statemachine.contract.SideEffect

open class StateMachineInterpreter<I : Intent, A : Action, R : Result, VS: ViewState, E: Event, SE : SideEffect>(
    private val stateMachine: StateMachine<I, A, R, VS, E, SE>
) : Interpreter<I, A, VS, E> {
    override suspend fun interpret(intent: I, state: State<VS, E>): A? = stateMachine.graph
        .filterKeys { key -> key.matches(state.viewState) }.values
        .flatMap { stateNode -> stateNode.intents.entries }
        .find { intentMatcher -> intentMatcher.key.matches(intent) }
        ?.value?.invoke(state.viewState, intent)
}
