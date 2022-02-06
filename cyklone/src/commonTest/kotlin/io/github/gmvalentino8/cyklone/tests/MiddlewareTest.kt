package io.github.gmvalentino8.cyklone.tests

import app.cash.turbine.test
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.middleware.ActionMiddleware
import io.github.gmvalentino8.cyklone.middleware.IntentMiddleware
import io.github.gmvalentino8.cyklone.middleware.ResultMiddleware
import io.github.gmvalentino8.cyklone.middleware.StateMiddleware
import io.github.gmvalentino8.cyklone.mock.MockAction
import io.github.gmvalentino8.cyklone.mock.MockEvent
import io.github.gmvalentino8.cyklone.mock.MockIntent
import io.github.gmvalentino8.cyklone.mock.MockResult
import io.github.gmvalentino8.cyklone.mock.MockViewState
import io.github.gmvalentino8.test.BaseTest
import io.github.gmvalentino8.test.awaitItemAssert
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
class MiddlewareTest : BaseTest() {
    private val testStates = arrayOf(
        State<MockViewState, MockEvent>(MockViewState.None),
        State<MockViewState, MockEvent>(MockViewState.Loading),
        State<MockViewState, MockEvent>(MockViewState.Stable("")),
        State<MockViewState, MockEvent>(MockViewState.Error(Throwable()))
    )

    @Test
    fun test_generic_intent_middleware() {
        val intents = arrayOf(
            MockIntent.Submit(),
            MockIntent.UpdateText("1"),
            MockIntent.UpdateText("2"),
            MockIntent.UpdateText("3")
        )
        val intentsFlow = flowOf(*intents)
        val statesFlow = MutableStateFlow(testStates.first())
        val testIntentMiddleware = IntentMiddleware<MockIntent, MockViewState, MockEvent> { input, state ->
            input.withIndex().onEach { (index, _) ->
                statesFlow.value = testStates[index]
            }.map {
                when (state.value.viewState) {
                    is MockViewState.None -> MockIntent.UpdateText("None")
                    is MockViewState.Loading -> MockIntent.UpdateText("Loading")
                    is MockViewState.Stable -> MockIntent.UpdateText("Stable")
                    is MockViewState.Error -> MockIntent.Submit()
                }
            }
        }

        runTest {
            testIntentMiddleware.modifyIntents(intentsFlow, statesFlow).test {
                awaitItemAssert {
                    shouldBeTypeOf<MockIntent.UpdateText>()
                    text shouldBe "None"
                }
                awaitItemAssert {
                    shouldBeTypeOf<MockIntent.UpdateText>()
                    text shouldBe "Loading"
                }
                awaitItemAssert {
                    shouldBeTypeOf<MockIntent.UpdateText>()
                    text shouldBe "Stable"
                }
                awaitItem().shouldBeTypeOf<MockIntent.Submit>()
                awaitComplete()
                cancel()
            }
        }
    }

    @Test
    fun test_generic_action_middleware() {
        val actions = arrayOf(
            MockAction.UpdateText("1"),
            MockAction.UpdateText("2"),
            MockAction.UpdateText("3"),
            MockAction.Submit {}
        )
        val actionsFlow = flowOf(*actions)
        val statesFlow = MutableStateFlow(testStates.first())
        val actionMiddleware = ActionMiddleware<MockAction, MockViewState, MockEvent> { input, state ->
            input.withIndex().onEach { (index, _) ->
                statesFlow.value = testStates[index]
            }.map {
                when (state.value.viewState) {
                    is MockViewState.None -> MockAction.UpdateText("None")
                    is MockViewState.Loading -> MockAction.UpdateText("Loading")
                    is MockViewState.Stable -> MockAction.UpdateText("Stable")
                    is MockViewState.Error -> MockAction.Submit()
                }
            }
        }

        runTest {
            actionMiddleware.modifyActions(actionsFlow, statesFlow).test {
                awaitItemAssert {
                    shouldBeTypeOf<MockAction.UpdateText>()
                    text shouldBe "None"
                }
                awaitItemAssert {
                    shouldBeTypeOf<MockAction.UpdateText>()
                    text shouldBe "Loading"
                }
                awaitItemAssert {
                    shouldBeTypeOf<MockAction.UpdateText>()
                    text shouldBe "Stable"
                }
                awaitItem().shouldBeTypeOf<MockAction.Submit>()
                awaitComplete()
                cancel()
            }
        }
    }

    @Test
    fun test_generic_result_middleware() {
        val results = arrayOf(
            MockResult.Loading,
            MockResult.UpdateText("1"),
            MockResult.UpdateText("2"),
            MockResult.Error(Throwable())
        )
        val resultsFlow = flowOf(*results)
        val statesFlow = MutableStateFlow(testStates.first())
        val resultMiddleware = ResultMiddleware<MockResult, MockViewState, MockEvent> { input, state ->
            input.withIndex().onEach { (index, _) ->
                statesFlow.value = testStates[index]
            }.map {
                when (state.value.viewState) {
                    is MockViewState.None -> MockResult.Loading
                    is MockViewState.Loading -> MockResult.UpdateText("Loading")
                    is MockViewState.Stable -> MockResult.UpdateText("Stable")
                    is MockViewState.Error -> MockResult.Error(Throwable())
                }
            }
        }

        runTest {
            resultMiddleware.modifyResults(resultsFlow, statesFlow).test {
                awaitItem().shouldBeTypeOf<MockResult.Loading>()
                awaitItemAssert {
                    shouldBeTypeOf<MockResult.UpdateText>()
                    text shouldBe "Loading"
                }
                awaitItemAssert {
                    shouldBeTypeOf<MockResult.UpdateText>()
                    text shouldBe "Stable"
                }
                awaitItem().shouldBeTypeOf<MockResult.Error>()
                awaitComplete()
                cancel()
            }
        }
    }

    @Test
    fun test_generic_state_middleware() {
        val statesFlow = flowOf(*testStates)
        val testStateMiddleware = StateMiddleware<MockViewState, MockEvent> { input ->
            input.withIndex().map { (index, _) -> State(MockViewState.Stable(index.toString())) }
        }

        runTest {
            testStateMiddleware.modifyStates(statesFlow).test {
                repeat(testStates.size) { index ->
                    awaitItemAssert {
                        val vs = viewState
                        vs.shouldBeTypeOf<MockViewState.Stable>()
                        vs.text shouldBe index.toString()
                    }
                }
                awaitComplete()
                cancel()
            }
        }
    }
}
