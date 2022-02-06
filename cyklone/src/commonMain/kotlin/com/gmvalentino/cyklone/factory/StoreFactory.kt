package com.gmvalentino.cyklone.factory

import com.gmvalentino.cyklone.middleware.Middleware
import com.gmvalentino.cyklone.components.Store
import com.gmvalentino.cyklone.contract.Action
import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.Intent
import com.gmvalentino.cyklone.contract.Result
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState

interface StoreFactory<I : Intent, A : Action, R : Result, VS : ViewState, E : Event> {
    fun create(initialState: State<VS, E>, middlewares: List<Middleware> = emptyList()): Store<I, VS, E>
}
