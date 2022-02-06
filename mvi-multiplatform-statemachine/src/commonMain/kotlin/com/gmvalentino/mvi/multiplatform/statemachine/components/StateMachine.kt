package com.gmvalentino.mvi.multiplatform.statemachine.components

import com.gmvalentino.mvi.multiplatform.contract.Action
import com.gmvalentino.mvi.multiplatform.contract.Contract
import com.gmvalentino.mvi.multiplatform.contract.Event
import com.gmvalentino.mvi.multiplatform.contract.Intent
import com.gmvalentino.mvi.multiplatform.contract.Result
import com.gmvalentino.mvi.multiplatform.contract.ViewState
import com.gmvalentino.mvi.multiplatform.statemachine.contract.SideEffect
import kotlin.reflect.KClass

open class StateMachine<I : Intent, A : Action, R : Result, VS : ViewState, E : Event, SE : SideEffect>(
    val graph: Map<Matcher<VS, VS>, StateNode<I, A, R, VS>>,
) {
    constructor(builder: StateMachineBuilder<I, A, R, VS, E, SE>.() -> Unit) :
        this(StateMachineBuilder<I, A, R, VS, E, SE>().apply(builder).build().graph)

    class Matcher<T : Contract, out R : T> private constructor(private val kClass: KClass<R>) {
        private val predicates = mutableListOf<(T) -> Boolean>({ kClass.isInstance(it) })

        fun matches(value: T) = predicates.all { it(value) }

        companion object {
            fun <T : Contract, R : T> any(kClass: KClass<R>): Matcher<T, R> = Matcher(kClass)

            inline fun <T : Contract, reified R : T> any(): Matcher<T, R> = any(R::class)
        }
    }

    class StateNode<I : Intent, A : Action, R : Result, VS : ViewState> {
        val actions = mutableMapOf<Matcher<A, A>, List<ActionNode<A, VS>>>()
        val intents = mutableMapOf<Matcher<I, I>, (VS, I) -> A>()
        val transitions = mutableMapOf<Matcher<R, R>, (VS, R) -> VS>()
    }

    sealed class ActionNode<A : Action, VS : ViewState> {
        data class ResultNode<A : Action, R : Result, VS : ViewState>(val value: (VS, A) -> R?) : ActionNode<A, VS>()
        data class SideEffectNode<A : Action, SE : SideEffect, VS : ViewState>(val value: (VS, A) -> SE?) : ActionNode<A, VS>()
    }

    class StateMachineBuilder<I : Intent, A : Action, R : Result, VS : ViewState, E : Event, SE : SideEffect> {
        private val graph = LinkedHashMap<Matcher<VS, VS>, StateNode<I, A, R, VS>>()

        fun <VIEWSTATE : VS> state(stateMatcher: Matcher<VS, VIEWSTATE>, config: StateNodeBuilder<VIEWSTATE>.() -> Unit) {
            graph[stateMatcher] = StateNodeBuilder<VIEWSTATE>().apply(config).build()
        }

        inline fun <reified VIEWSTATE : VS> state(noinline config: StateNodeBuilder<VIEWSTATE>.() -> Unit) = state(Matcher.any(), config)

        fun build(): StateMachine<I, A, R, VS, E, SE> = StateMachine(graph.toMap())

        @Suppress("UNCHECKED_CAST")
        inner class StateNodeBuilder<VIEWSTATE : VS> {
            private val stateNode = StateNode<I, A, R, VS>()

            fun <INTENT : I> interpret(intentMatcher: Matcher<I, INTENT>, interpret: VIEWSTATE.(INTENT) -> A) {
                stateNode.intents[intentMatcher] = { state, intent -> interpret(state as VIEWSTATE, intent as INTENT) }
            }

            inline fun <reified INTENT : I> interpret(noinline interpret: VIEWSTATE.(INTENT) -> A) = interpret(Matcher.any(), interpret)

            fun <ACTION : A> process(actionMatcher: Matcher<A, ACTION>, builder: ActionNodeBuilder<ACTION, VIEWSTATE>.() -> Unit) {
                stateNode.actions[actionMatcher] = ActionNodeBuilder<ACTION, VIEWSTATE>().apply(builder).build() as List<ActionNode<A, VS>>
            }

            inline fun <reified ACTION : A> process(noinline builder: ActionNodeBuilder<ACTION, VIEWSTATE>.() -> Unit) =
                process(Matcher.any(), builder)

            fun <RESULT : R> reduce(resultMatcher: Matcher<R, RESULT>, transition: VIEWSTATE.(RESULT) -> VS) {
                stateNode.transitions[resultMatcher] = { state, result -> transition(state as VIEWSTATE, result as RESULT) }
            }

            inline fun <reified RESULT : R> reduce(noinline transition: VIEWSTATE.(RESULT) -> VS) = reduce(Matcher.any(), transition)

            fun build() = stateNode

            inner class ActionNodeBuilder<ACTION : A, VIEWSTATE : VS> {
                private val actionNodes = mutableListOf<ActionNode<ACTION, VIEWSTATE>>()

                fun result(value: VIEWSTATE.(ACTION) -> R?) = actionNodes.add(ActionNode.ResultNode(value))

                fun sideEffect(value: VIEWSTATE.(ACTION) -> SE?) = actionNodes.add(ActionNode.SideEffectNode(value))

                fun build() = actionNodes
            }
        }
    }
}
