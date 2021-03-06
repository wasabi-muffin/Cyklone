package io.github.gmvalentino8.cyklone.statemachine.components

import io.github.gmvalentino8.cyklone.components.Processor
import io.github.gmvalentino8.cyklone.contract.Action
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Intent
import io.github.gmvalentino8.cyklone.contract.Result
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState
import io.github.gmvalentino8.cyklone.statemachine.contract.SideEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

abstract class StateMachineProcessor<I : Intent, A : Action, R : Result, VS : ViewState, E : Event, SE : SideEffect>(
    private val stateMachine: StateMachine<I, A, R, VS, E, SE>,
) : Processor<A, R, VS, E> {
    @Suppress("UNCHECKED_CAST")
    override suspend fun process(
        action: A,
        state: State<VS, E>,
    ): Flow<R> = stateMachine.graph
        .filterKeys { key -> key.matches(state.viewState) }.values
        .flatMap { stateNode -> stateNode.actions.entries }
        .find { actionMatcher -> actionMatcher.key.matches(action) }?.value
        ?.asFlow()
        ?.map { node ->
            when (node) {
                is StateMachine.ActionNode.ResultNode<A, *, VS> -> node.value(state.viewState, action) as? R
                is StateMachine.ActionNode.SideEffectNode<A, *, VS> -> (node.value(state.viewState, action) as? SE)?.let { process(it, state) }
            }
        }
        ?.filterNotNull()
        ?: flowOf()

    abstract suspend fun process(sideEffect: SE, state: State<VS, E>): R?
}
