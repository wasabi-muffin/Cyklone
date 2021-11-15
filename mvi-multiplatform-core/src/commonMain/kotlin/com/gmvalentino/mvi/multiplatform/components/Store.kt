package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * The store exposes a stream of [State] and a stream of [Event] for the client to react to.
 */
interface Store<INTENT : Intent, STATE : State, EVENT : Event> {
    /**
     * Stream of [State] exposed to the client
     */
    val state: StateFlow<STATE>

    /**
     * Stream of [Event] exposed to the client
     */
    val events: Flow<EVENT>

    /**
     * Dispatches an [Intent]
     */
    fun dispatch(intent: INTENT)

    /**
     * Cancels all jobs within the store
     */
    fun dispose()

    /**
     * Collect used for Kotlin Native
     */
    fun collect(
        onState: ((STATE) -> Unit),
        onEvent: ((EVENT) -> Unit)
    ): Job
}

class StoreFactory<INTENT : Intent, ACTION : Action, RESULT : Result, STATE : State, EVENT : Event>(
    private val interpreter: Interpreter<INTENT, ACTION>,
    private val processor: BaseProcessor<STATE, ACTION, RESULT, EVENT>,
    private val reducer: Reducer<RESULT, STATE>
) {
    private var cached: Store<INTENT, STATE, EVENT>? = null

    fun getOrCreate(
        initialState: STATE,
        intentModifiers: IntentModifiers<INTENT, STATE> = IntentModifiers(),
        actionModifiers: ActionModifiers<ACTION, STATE> = ActionModifiers(),
        resultModifiers: ResultModifiers<RESULT, STATE> = ResultModifiers(),
        stateModifiers: StateModifiers<STATE> = StateModifiers()
    ): Store<INTENT, STATE, EVENT> {
        return cached ?: create(
            initialState,
            Modifiers(intentModifiers, actionModifiers, resultModifiers, stateModifiers)
        )
    }

    private fun create(
        initialState: STATE,
        modifiers: Modifiers<INTENT, ACTION, RESULT, STATE>
    ): Store<INTENT, STATE, EVENT> = object : BaseStore<INTENT, ACTION, RESULT, STATE, EVENT>(
        initialState = initialState,
        interpreter = interpreter,
        processor = processor,
        reducer = reducer,
        modifiers = modifiers
    ) {}.also { cached = it }
}
