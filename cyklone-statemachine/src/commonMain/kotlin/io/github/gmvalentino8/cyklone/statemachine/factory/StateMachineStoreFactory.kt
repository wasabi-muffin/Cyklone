package io.github.gmvalentino8.cyklone.statemachine.factory

import io.github.gmvalentino8.cyklone.components.DefaultStore
import io.github.gmvalentino8.cyklone.middleware.Middleware
import io.github.gmvalentino8.cyklone.components.Store
import io.github.gmvalentino8.cyklone.contract.Action
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Intent
import io.github.gmvalentino8.cyklone.contract.Result
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState
import io.github.gmvalentino8.cyklone.factory.StoreFactory
import io.github.gmvalentino8.cyklone.statemachine.components.StateMachine
import io.github.gmvalentino8.cyklone.statemachine.components.StateMachineInterpreter
import io.github.gmvalentino8.cyklone.statemachine.components.StateMachineProcessor
import io.github.gmvalentino8.cyklone.statemachine.components.StateMachineReducer
import io.github.gmvalentino8.cyklone.statemachine.contract.SideEffect
import io.github.gmvalentino8.cyklone.statemachine.middleware.StateMachineMiddleware

class StateMachineStoreFactory<I : Intent, A : Action, R : Result, VS : ViewState, E : Event, SE : SideEffect>(
    private val stateMachine: StateMachine<I, A, R, VS, E, SE>,
    private val processor: StateMachineProcessor<I, A, R, VS, E, SE>,
) : StoreFactory<I, A, R, VS, E> {
    override fun create(initialState: State<VS, E>, middlewares: List<Middleware>): Store<I, VS, E> {
        return DefaultStore(
            initialState = initialState,
            interpreter = StateMachineInterpreter(stateMachine),
            processor = processor,
            reducer = StateMachineReducer(stateMachine),
            middlewares = listOf(StateMachineMiddleware(stateMachine)) + middlewares
        )
    }
}
