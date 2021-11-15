package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Intent

/**
 * Interpreters receive [Intent] from user input and maps it to an [Action]
 */
interface Interpreter<in INTENT : Intent, out ACTION : Action> {
    suspend fun interpret(intent: INTENT): ACTION
}
