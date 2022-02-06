package com.gmvalentino.cyklone.statemachine.factory

import com.gmvalentino.cyklone.components.DefaultStore
import com.gmvalentino.cyklone.middleware.Middleware
import com.gmvalentino.cyklone.components.Store
import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState
import com.gmvalentino.cyklone.factory.StoreFactory
import com.gmvalentino.cyklone.statemachine.components.StateMachine
import com.gmvalentino.cyklone.statemachine.components.StateMachineInterpreter
import com.gmvalentino.cyklone.statemachine.components.StateMachineProcessor
import com.gmvalentino.cyklone.statemachine.components.StateMachineReducer
import com.gmvalentino.cyklone.statemachine.contract.SideEffect
import com.gmvalentino.cyklone.statemachine.middleware.StateMachineMiddleware

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
