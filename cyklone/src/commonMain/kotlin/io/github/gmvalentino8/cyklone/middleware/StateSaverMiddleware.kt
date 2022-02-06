package io.github.gmvalentino8.cyklone.middleware

import io.github.gmvalentino8.cyklone.contract.Event
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.contract.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class StateSaverMiddleware<VS : ViewState, E : Event>(
    private val save: (state: State<VS, E>) -> Unit,
) : StateMiddleware<VS, E> {
    override fun modifyStates(input: Flow<State<VS, E>>): Flow<State<VS, E>> = input.onEach { state ->
        save(state)
    }
}
