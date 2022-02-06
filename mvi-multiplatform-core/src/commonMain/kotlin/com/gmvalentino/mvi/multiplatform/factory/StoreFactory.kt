package com.gmvalentino.mvi.multiplatform.factory

import com.gmvalentino.mvi.multiplatform.middleware.Middleware
import com.gmvalentino.mvi.multiplatform.components.Store
import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import com.gmvalentino.mvi.multiplatform.contract.ViewState

interface StoreFactory<I : Intent, A : Action, R : Result, VS : ViewState, E : Event> {
    fun create(initialState: State<VS, E>, middlewares: List<Middleware> = emptyList()): Store<I, VS, E>
}
