package com.gmvalentino.cyklone.middleware

import com.gmvalentino.cyklone.contract.Event
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.contract.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class StateSaverMiddleware<VS : ViewState, E : Event>(
    private val save: (state: State<VS, E>) -> Unit,
) : StateMiddleware<VS, E> {
    override fun modifyStates(input: Flow<State<VS, E>>): Flow<State<VS, E>> = input.onEach { state ->
        save(state)
    }
}
