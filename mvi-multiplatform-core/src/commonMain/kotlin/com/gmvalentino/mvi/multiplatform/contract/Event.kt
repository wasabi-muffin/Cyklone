package com.gmvalentino.mvi.multiplatform.contract

import com.gmvalentino.mvi.multiplatform.utils.UUID

/**
 * One-time effects for the client (i.e. navigation, snackbars, animation)
 */
abstract class Event {
    val id: String = UUID.randomUUID()
}

interface ProcessEventIntent<E : Event> : Intent {
    val event: E
}

interface ProcessEventAction<E : Event> : Action {
    val event: E
}

interface ProcessEventResult<E : Event> : Result {
    val event: E
}

interface SendEventResult<E : Event> : Result {
    val event: E
}
