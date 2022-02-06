package io.github.gmvalentino8.cyklone.factory

import io.github.gmvalentino8.cyklone.middleware.Middleware
import io.github.gmvalentino8.cyklone.components.Store
import io.github.gmvalentino8.cyklone.contract.Action
import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.Intent
import io.github.gmvalentino8.cyklone.contract.Result
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState

interface StoreFactory<I : Intent, A : Action, R : Result, VS : ViewState, E : Event> {
    fun create(initialState: State<VS, E>, middlewares: List<Middleware> = emptyList()): Store<I, VS, E>
}
