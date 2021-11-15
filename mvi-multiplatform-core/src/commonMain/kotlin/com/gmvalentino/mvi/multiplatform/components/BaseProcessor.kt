package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Abstract class to handle [Event]s during processiing
 */
abstract class BaseProcessor<in STATE : State, ACTION : Action, RESULT : Result, EVENT : Event> :
    Processor<STATE, ACTION, RESULT, EVENT> {

    // ref: https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    private val _events = Channel<EVENT>(Channel.BUFFERED)
    override val events: Flow<EVENT> = _events.receiveAsFlow()

    override suspend fun publish(event: EVENT) {
        _events.send(event)
    }
}
