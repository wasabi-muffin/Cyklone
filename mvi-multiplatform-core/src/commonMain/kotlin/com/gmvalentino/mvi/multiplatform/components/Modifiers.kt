package com.gmvalentino.mvi.multiplatform.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.State

/**
 * Container for different [Modifier]s
 *
 * TODO: Reconsider Architecture
 */
class Modifiers<INTENT : Intent, ACTION : Action, RESULT : Result, STATE : State>(
    val intentModifiers: IntentModifiers<INTENT, STATE> = IntentModifiers(),
    val actionModifiers: ActionModifiers<ACTION, STATE> = ActionModifiers(),
    val resultModifiers: ResultModifiers<RESULT, STATE> = ResultModifiers(),
    val stateModifiers: StateModifiers<STATE> = StateModifiers()
)

class IntentModifiers<INTENT : Intent, STATE : State>(
    vararg val modifiers: IntentModifier<INTENT, STATE>
)

class ActionModifiers<ACTION : Action, STATE : State>(
    vararg val modifiers: ActionModifier<ACTION, STATE>
)

class ResultModifiers<RESULT : Result, STATE : State>(
    vararg val modifiers: ResultModifier<RESULT, STATE>
)

class StateModifiers<STATE : State>(
    vararg val modifiers: StateModifier<STATE>
)
