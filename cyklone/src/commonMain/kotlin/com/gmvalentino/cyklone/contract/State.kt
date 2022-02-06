package com.gmvalentino.cyklone.contract

data class State<VS: ViewState, E: Event>(
    val viewState: VS,
    val event: E? = null,
    val events: List<E> = emptyList()
) : Contract {
    fun send(event: E): State<VS, E> {
        val updatedEvents = events + event
        return this.copy(event = updatedEvents.firstOrNull(), events = updatedEvents)
    }

    fun process(event: E): State<VS, E> {
        val updatedEvents = events.filterNot { it.id == event.id }
        return this.copy(event = updatedEvents.firstOrNull(), events = updatedEvents)
    }
}
