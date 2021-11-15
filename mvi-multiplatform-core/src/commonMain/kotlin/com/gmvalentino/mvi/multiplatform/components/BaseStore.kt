package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

/**
 * Abstract class that implements a [Store] that provides unidirectional dataflow logic
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
abstract class BaseStore<INTENT : Intent, in ACTION : Action, in RESULT : Result, STATE : State, EVENT : Event>(
    initialState: STATE,
    private val interpreter: Interpreter<INTENT, ACTION>,
    private val processor: BaseProcessor<STATE, ACTION, RESULT, EVENT>,
    private val reducer: Reducer<RESULT, STATE>,
    private val modifiers: Modifiers<INTENT, ACTION, RESULT, STATE> = Modifiers(),
    coroutineContext: CoroutineContext = Dispatchers.Main
) : Store<INTENT, STATE, EVENT> {

    private val scope = CoroutineScope(coroutineContext + Job())
    private val intents =
        MutableSharedFlow<INTENT>(replay = Int.MAX_VALUE, extraBufferCapacity = Int.MAX_VALUE)
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<STATE> = _state
    override val events: Flow<EVENT> =
        processor.events.shareIn(scope, SharingStarted.WhileSubscribed())

    init {
        scope.launch {
            intents
                .applyModifiers(*modifiers.intentModifiers.modifiers)
                .map { intent -> interpreter.interpret(intent) }
                .applyModifiers(*modifiers.actionModifiers.modifiers)
                .flatMapMerge { action -> processor.process(action, _state.value) }
                .applyModifiers(*modifiers.resultModifiers.modifiers)
                .map { result -> reducer.reduce(result, _state.value) }
                .applyModifiers(*modifiers.stateModifiers.modifiers)
                .collect { state -> _state.value = state }
        }
    }

    override fun dispatch(intent: INTENT) {
        scope.launch {
            intents.emit(intent)
        }
    }

    override fun dispose() {
        scope.cancel()
    }

    override fun collect(
        onState: ((STATE) -> Unit),
        onEvent: ((EVENT) -> Unit)
    ): Job = scope.launch {
        launch { state.collect { onState(it) } }
        launch { events.collect { onEvent(it) } }
    }

    /**
     * Helper functions to apply [Modifier] to their intended stream
     */
    private fun Flow<INTENT>.applyModifiers(
        vararg modifiers: IntentModifier<INTENT, STATE>
    ): Flow<INTENT> = modifiers.fold(this) { intents, modifier ->
        modifier.modifyIntents(intents, _state)
    }

    private fun Flow<ACTION>.applyModifiers(
        vararg modifiers: ActionModifier<ACTION, STATE>
    ): Flow<ACTION> = modifiers.fold(this) { actions, modifier ->
        modifier.modifyActions(actions, _state)
    }

    private fun Flow<RESULT>.applyModifiers(
        vararg modifiers: ResultModifier<RESULT, STATE>
    ): Flow<RESULT> = modifiers.fold(this) { results, modifier ->
        modifier.modifyResults(results, _state)
    }

    private fun Flow<STATE>.applyModifiers(
        vararg modifiers: StateModifier<STATE>
    ): Flow<STATE> = modifiers.fold(this) { states, modifier ->
        modifier.modifyStates(states)
    }
}
