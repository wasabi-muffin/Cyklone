package com.gmvalentino.mvi.multiplatform.tests

import app.cash.turbine.test
import com.gmvalentino.mvi.multiplatform.components.ActionModifier
import com.gmvalentino.mvi.multiplatform.components.IntentModifier
import com.gmvalentino.mvi.multiplatform.components.ResultModifier
import com.gmvalentino.mvi.multiplatform.components.StateModifier
import com.gmvalentino.mvi.multiplatform.mock.ActionMock
import com.gmvalentino.mvi.multiplatform.mock.IntentMock
import com.gmvalentino.mvi.multiplatform.mock.ResultMock
import com.gmvalentino.mvi.multiplatform.mock.StateMock
import com.gmvalentino.test.BaseTest
import com.gmvalentino.test.awaitItemAssert
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.withIndex

@ExperimentalCoroutinesApi
@ExperimentalTime
class ModifiersTest : BaseTest() {
    private val testStates = arrayOf(
        StateMock.None,
        StateMock.Loading,
        StateMock.Stable(""),
        StateMock.Error(Throwable())
    )

    @Test
    fun test_generic_intent_modifier() {
        val intents = arrayOf(
            IntentMock.Submit(),
            IntentMock.UpdateText("1"),
            IntentMock.UpdateText("2"),
            IntentMock.UpdateText("3")
        )
        val intentsFlow = flowOf(*intents)
        val statesFlow = MutableStateFlow(testStates.first())
        val testIntentModifier = IntentModifier<IntentMock, StateMock> { input, state ->
            input.withIndex().onEach { (index, _) ->
                statesFlow.value = testStates[index]
            }.map {
                when (state.value) {
                    is StateMock.None -> IntentMock.UpdateText("None")
                    StateMock.Loading -> IntentMock.UpdateText("Loading")
                    is StateMock.Stable -> IntentMock.UpdateText("Stable")
                    is StateMock.Error -> IntentMock.Submit()
                }
            }
        }

        runTest {
            testIntentModifier.modifyIntents(intentsFlow, statesFlow).test {
                awaitItemAssert {
                    shouldBeTypeOf<IntentMock.UpdateText>()
                    text shouldBe "None"
                }
                awaitItemAssert {
                    shouldBeTypeOf<IntentMock.UpdateText>()
                    text shouldBe "Loading"
                }
                awaitItemAssert {
                    shouldBeTypeOf<IntentMock.UpdateText>()
                    text shouldBe "Stable"
                }
                awaitItem().shouldBeTypeOf<IntentMock.Submit>()
                awaitComplete()
                cancel()
            }
        }
    }

    @Test
    fun test_generic_action_modifier() {
        val actions = arrayOf(
            ActionMock.UpdateText("1"),
            ActionMock.UpdateText("2"),
            ActionMock.UpdateText("3"),
            ActionMock.Submit {}
        )
        val actionsFlow = flowOf(*actions)
        val statesFlow = MutableStateFlow(testStates.first())
        val actionModifier = ActionModifier<ActionMock, StateMock> { input, state ->
            input.withIndex().onEach { (index, _) ->
                statesFlow.value = testStates[index]
            }.map {
                when (state.value) {
                    is StateMock.None -> ActionMock.UpdateText("None")
                    StateMock.Loading -> ActionMock.UpdateText("Loading")
                    is StateMock.Stable -> ActionMock.UpdateText("Stable")
                    is StateMock.Error -> ActionMock.Submit()
                }
            }
        }

        runTest {
            actionModifier.modifyActions(actionsFlow, statesFlow).test {
                awaitItemAssert {
                    shouldBeTypeOf<ActionMock.UpdateText>()
                    text shouldBe "None"
                }
                awaitItemAssert {
                    shouldBeTypeOf<ActionMock.UpdateText>()
                    text shouldBe "Loading"
                }
                awaitItemAssert {
                    shouldBeTypeOf<ActionMock.UpdateText>()
                    text shouldBe "Stable"
                }
                awaitItem().shouldBeTypeOf<ActionMock.Submit>()
                awaitComplete()
                cancel()
            }
        }
    }

    @Test
    fun test_generic_result_modifier() {
        val results = arrayOf(
            ResultMock.Loading,
            ResultMock.UpdateText("1"),
            ResultMock.UpdateText("2"),
            ResultMock.Error(Throwable())
        )
        val resultsFlow = flowOf(*results)
        val statesFlow = MutableStateFlow(testStates.first())
        val resultModifier = ResultModifier<ResultMock, StateMock> { input, state ->
            input.withIndex().onEach { (index, _) ->
                statesFlow.value = testStates[index]
            }.map {
                when (state.value) {
                    is StateMock.None -> ResultMock.Loading
                    StateMock.Loading -> ResultMock.UpdateText("Loading")
                    is StateMock.Stable -> ResultMock.UpdateText("Stable")
                    is StateMock.Error -> ResultMock.Error(Throwable())
                }
            }
        }

        runTest {
            resultModifier.modifyResults(resultsFlow, statesFlow).test {
                awaitItem().shouldBeTypeOf<ResultMock.Loading>()
                awaitItemAssert {
                    shouldBeTypeOf<ResultMock.UpdateText>()
                    text shouldBe "Loading"
                }
                awaitItemAssert {
                    shouldBeTypeOf<ResultMock.UpdateText>()
                    text shouldBe "Stable"
                }
                awaitItem().shouldBeTypeOf<ResultMock.Error>()
                awaitComplete()
                cancel()
            }
        }
    }

    @Test
    fun test_generic_state_modifier() {
        val statesFlow = flowOf(*testStates)
        val testStateModifier = StateModifier<StateMock> { input ->
            input.withIndex().map { (index, _) -> StateMock.Stable(index.toString()) }
        }

        runTest {
            testStateModifier.modifyStates(statesFlow).test {
                repeat(testStates.size) { index ->
                    awaitItemAssert {
                        shouldBeTypeOf<StateMock.Stable>()
                        text shouldBe index.toString()
                    }
                }
                awaitComplete()
                cancel()
            }
        }
    }
}
