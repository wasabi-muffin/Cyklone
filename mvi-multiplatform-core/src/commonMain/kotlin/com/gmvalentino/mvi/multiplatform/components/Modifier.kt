package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Contract
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * [Modifier] apply transformations on a certain stream
 *
 * Input streams and output streams must be of the same type
 */
fun interface Modifier<T : Contract, S : State> {
    fun apply(input: Flow<T>, state: StateFlow<S>): Flow<T>
}

/**
 * Apply transformations on [Intent] streams
 */
fun interface IntentModifier<INTENT : Intent, STATE : State> {
    fun modifyIntents(input: Flow<INTENT>, state: StateFlow<STATE>): Flow<INTENT>
}

/**
 * Apply transformations on [Action] streams
 */
fun interface ActionModifier<ACTION : Action, STATE : State> {
    fun modifyActions(input: Flow<ACTION>, state: StateFlow<STATE>): Flow<ACTION>
}

/**
 * Apply transformations on [Result] streams
 */
fun interface ResultModifier<RESULT : Result, STATE : State> {
    fun modifyResults(input: Flow<RESULT>, state: StateFlow<STATE>): Flow<RESULT>
}

/**
 * Apply transformations on [State] streams
 */
fun interface StateModifier<STATE : State> {
    fun modifyStates(input: Flow<STATE>): Flow<STATE>
}
