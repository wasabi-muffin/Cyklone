package io.github.gmvalentino8.cyklone.statemachine.components

import io.github.gmvalentino8.cyklone.components.Interpreter
import io.github.gmvalentino8.cyklone.contract.Action
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Intent
import io.github.gmvalentino8.cyklone.contract.Result
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState
import io.github.gmvalentino8.cyklone.statemachine.contract.SideEffect

open class StateMachineInterpreter<I : Intent, A : Action, R : Result, VS: ViewState, E: Event, SE : SideEffect>(
    private val stateMachine: StateMachine<I, A, R, VS, E, SE>
) : Interpreter<I, A, VS, E> {
    override suspend fun interpret(intent: I, state: State<VS, E>): A? = stateMachine.graph
        .filterKeys { key -> key.matches(state.viewState) }.values
        .flatMap { stateNode -> stateNode.intents.entries }
        .find { intentMatcher -> intentMatcher.key.matches(intent) }
        ?.value?.invoke(state.viewState, intent)
}
