package io.github.gmvalentino8.cyklone.factory

import io.github.gmvalentino8.cyklone.components.DefaultStore
import io.github.gmvalentino8.cyklone.components.Interpreter
import io.github.gmvalentino8.cyklone.middleware.Middleware
import io.github.gmvalentino8.cyklone.components.Processor
import io.github.gmvalentino8.cyklone.components.Reducer
import io.github.gmvalentino8.cyklone.components.Store
import io.github.gmvalentino8.cyklone.contract.Action
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Intent
import io.github.gmvalentino8.cyklone.contract.Result
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState

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
