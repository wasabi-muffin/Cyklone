package io.github.gmvalentino8.cyklone.contract

import io.github.gmvalentino8.cyklone.utils.UUID

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
