package com.gmvalentino.mvi.multiplatform.factory

import com.gmvalentino.mvi.multiplatform.components.DefaultStore
import com.gmvalentino.mvi.multiplatform.components.Interpreter
import com.gmvalentino.mvi.multiplatform.middleware.Middleware
import com.gmvalentino.mvi.multiplatform.components.Processor
import com.gmvalentino.mvi.multiplatform.components.Reducer
import com.gmvalentino.mvi.multiplatform.components.Store
import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import com.gmvalentino.mvi.multiplatform.contract.ViewState

class DefaultStoreFactory<I : Intent, A : Action, R : Result, VS : ViewState, E : Event>(
    private val interpreter: Interpreter<I, A, VS, E>,
    private val processor: Processor<A, R, VS, E>,
    private val reducer: Reducer<R, VS, E>,
) : StoreFactory<I, A, R, VS, E> {
    override fun create(initialState: State<VS, E>, middlewares: List<Middleware>): Store<I, VS, E> = DefaultStore(
        initialState = initialState,
        interpreter = interpreter,
        processor = processor,
        reducer = reducer,
        middlewares = middlewares
    )
}
