package com.gmvalentino.cyklone.factory

import com.gmvalentino.cyklone.components.DefaultStore
import com.gmvalentino.cyklone.components.Interpreter
import com.gmvalentino.cyklone.middleware.Middleware
import com.gmvalentino.cyklone.components.Processor
import com.gmvalentino.cyklone.components.Reducer
import com.gmvalentino.cyklone.components.Store
import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState

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
