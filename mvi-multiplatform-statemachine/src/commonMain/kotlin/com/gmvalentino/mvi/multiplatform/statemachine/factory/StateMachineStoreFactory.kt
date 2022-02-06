package com.gmvalentino.mvi.multiplatform.statemachine.factory

import com.gmvalentino.mvi.multiplatform.components.DefaultStore
import com.gmvalentino.mvi.multiplatform.middleware.Middleware
import com.gmvalentino.mvi.multiplatform.components.Store
import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import com.gmvalentino.mvi.multiplatform.contract.ViewState
import com.gmvalentino.mvi.multiplatform.factory.StoreFactory
import com.gmvalentino.mvi.multiplatform.statemachine.components.StateMachine
import com.gmvalentino.mvi.multiplatform.statemachine.components.StateMachineInterpreter
import com.gmvalentino.mvi.multiplatform.statemachine.components.StateMachineProcessor
import com.gmvalentino.mvi.multiplatform.statemachine.components.StateMachineReducer
import com.gmvalentino.mvi.multiplatform.statemachine.contract.SideEffect
import com.gmvalentino.mvi.multiplatform.statemachine.middleware.StateMachineMiddleware

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
