package com.gmvalentino.cyklone.tests

import app.cash.turbine.test
import com.gmvalentino.cyklone.components.DefaultStore
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.middleware.ActionMiddleware
import com.gmvalentino.cyklone.middleware.IntentMiddleware
import com.gmvalentino.cyklone.middleware.ResultMiddleware
import com.gmvalentino.cyklone.middleware.StateMiddleware
import com.gmvalentino.cyklone.mock.MockAction
import com.gmvalentino.cyklone.mock.MockEvent
import com.gmvalentino.cyklone.mock.MockIntent
import com.gmvalentino.cyklone.mock.MockInterpreter
import com.gmvalentino.cyklone.mock.MockProcessor
import com.gmvalentino.cyklone.mock.MockReducer
import com.gmvalentino.cyklone.mock.MockResult
import com.gmvalentino.cyklone.mock.MockViewState
import com.gmvalentino.test.BaseTest
import com.gmvalentino.test.awaitItemAssert
import io.kotest.assertions.timing.eventually
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.fail
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class StoreTest : BaseTest() {
    private val interpreter = MockInterpreter<MockIntent, MockAction, MockViewState, MockEvent>()
    private val processor = MockProcessor<MockAction, MockResult, MockViewState, MockEvent>()
    private val reducer = MockReducer<MockResult, MockViewState, MockEvent>()
    private lateinit var defaultStore: DefaultStore<MockIntent, MockAction, MockResult, MockViewState, MockEvent>

    @BeforeTest
    fun setup() {
        interpreter.mock = { intent, _ ->
            when (intent) {
                is MockIntent.UpdateText -> MockAction.UpdateText(intent.text)
                is MockIntent.Submit -> MockAction.Submit(intent.run)
            }
        }

        processor.mock = { action, _ ->
            when (action) {
                is MockAction.UpdateText -> flow {
                    emit(MockResult.Loading)
                    emit(MockResult.UpdateText(action.text))
                }
                is MockAction.Submit -> flow {
                    try {
                        emit(MockResult.Loading)
                        action.run()
                    } catch (e: Throwable) {
                        emit(MockResult.Error(e))
                    }
                }
            }
        }

        reducer.mock = { result, state ->
            val viewState = when (state.viewState) {
                is MockViewState.None -> {
                    when (result) {
                        is MockResult.Loading -> MockViewState.Loading
                        is MockResult.UpdateText -> MockViewState.Stable(result.text)
                        is MockResult.Error -> MockViewState.Error(result.throwable)
                    }
                }
                is MockViewState.Loading -> {
                    when (result) {
                        is MockResult.UpdateText -> MockViewState.Stable(result.text)
                        is MockResult.Error -> MockViewState.Error(result.throwable)
                        else -> state.viewState
                    }
                }
                is MockViewState.Stable -> {
                    when (result) {
                        is MockResult.UpdateText -> MockViewState.Stable(result.text)
                        is MockResult.Loading -> MockViewState.Loading
                        else -> state.viewState
                    }
                }
                is MockViewState.Error -> state.viewState
            }
            State<MockViewState, MockEvent>(viewState)
        }

        defaultStore = DefaultStore(
            interpreter = interpreter,
            processor = processor,
            reducer = reducer,
            initialState = State<MockViewState, MockEvent>(MockViewState.None),
            coroutineContext = coroutineContext
        )
    }

    @Test
    fun test_initial_state() {
        runTest {
            defaultStore.state.test {
                awaitItem().viewState.shouldBeTypeOf<MockViewState.None>()
                expectNoEvents()
                cancel()
            }
        }
    }

    @Test
    fun test_changes_before_collecting() = runTest {
        repeat(10) { defaultStore.dispatch(MockIntent.UpdateText(it.toString())) }
        defaultStore.dispatch(MockIntent.UpdateText("last"))

        eventually { defaultStore.state.value.viewState.shouldBeTypeOf<MockViewState.Stable>() }

        defaultStore.state.test {
            awaitItemAssert {
                val vs = viewState
                vs.shouldBeTypeOf<MockViewState.Stable>()
                vs.text shouldBe "last"
            }
            expectNoEvents()
            cancel()
        }
    }

    @Test
    fun test_dispatch() {
        runTest {
            defaultStore.state.test {
                awaitItem().viewState.shouldBeTypeOf<MockViewState.None>()
                repeat(1000) { defaultStore.dispatch(MockIntent.UpdateText(it.toString())) }
                repeat(1000) {
                    awaitItem().viewState.shouldBeTypeOf<MockViewState.Loading>()
                    awaitItem().viewState.let { state ->
                        state.shouldBeTypeOf<MockViewState.Stable>()
                        state.text shouldBe it.toString()
                    }
                }

                defaultStore.dispatch(MockIntent.Submit())
                awaitItem().viewState.shouldBeTypeOf<MockViewState.Loading>()

                expectNoEvents()
                cancel()
            }
        }
    }

    @Test
    fun test_dispose() {
        runTest {
            defaultStore.dispose()
            defaultStore.dispatch(MockIntent.Submit { fail("Should not reach here") })
        }
    }

    @Test
    fun test_exception_thrown() {
        runTest {
            defaultStore.state.test {
                awaitItem().viewState.shouldBeTypeOf<MockViewState.None>()
                defaultStore.dispatch(MockIntent.Submit { throw IllegalStateException("message") })
                awaitItem().viewState.shouldBeTypeOf<MockViewState.Loading>()
                awaitItemAssert {
                    val vs = viewState
                    vs.shouldBeTypeOf<MockViewState.Error>()
                    vs.throwable.shouldBeTypeOf<IllegalStateException>()
                    vs.throwable.message shouldBe "message"
                }
                expectNoEvents()
                cancel()
            }
        }
    }
//    @Test
//    fun test_collect() {
//        val stateCounter = MutableStateFlow(0)
//        runTest {
//            val job = defaultStore.collect(
//                onState = {
//                    stateCounter.value++
//                    when (stateCounter.value) {
//                        1 -> it shouldBe MockViewState.None
//                        2 -> it shouldBe MockViewState.Loading
//                    }
//                }
//            )
//
//            defaultStore.dispatch(MockIntent.Submit {})
//            eventually { defaultStore.state.value.viewState.shouldBeTypeOf<MockViewState.Loading>() }
//            stateCounter.value shouldBe 2
//
//            job.cancel()
//            defaultStore.dispatch(MockIntent.UpdateText(""))
//            eventually { defaultStore.state.value.viewState.shouldBeTypeOf<MockViewState.Stable>() }
//            stateCounter.value shouldBe 2
//        }
//    }
    @Test
    fun test_apply_middlewares() {
        var counter = 0

        class MiddlewareMock :
            IntentMiddleware<MockIntent, MockViewState, MockEvent>,
            ActionMiddleware<MockAction, MockViewState, MockEvent>,
            ResultMiddleware<MockResult, MockViewState, MockEvent>,
            StateMiddleware<MockViewState, MockEvent> {
            override fun modifyIntents(input: Flow<MockIntent>, state: StateFlow<State<MockViewState, MockEvent>>): Flow<MockIntent> =
                input.onEach { counter += 1 }

            override fun modifyActions(input: Flow<MockAction>, state: StateFlow<State<MockViewState, MockEvent>>): Flow<MockAction> =
                input.onEach { counter += 2 }

            override fun modifyResults(input: Flow<MockResult>, state: StateFlow<State<MockViewState, MockEvent>>): Flow<MockResult> =
                input.onEach { counter += 3 }

            override fun modifyStates(input: Flow<State<MockViewState, MockEvent>>): Flow<State<MockViewState, MockEvent>> =
                input.onEach { counter += 4 }.map { State(MockViewState.Loading) }
        }

        defaultStore = DefaultStore(
            interpreter = interpreter,
            processor = processor,
            reducer = reducer,
            initialState = State(MockViewState.None),
            coroutineContext = coroutineContext,
            middlewares = listOf(MiddlewareMock())
        )

        runTest {
            defaultStore.state.test {
                awaitItem().viewState.shouldBeTypeOf<MockViewState.None>()
                defaultStore.dispatch(MockIntent.Submit {})
                awaitItem().viewState.shouldBeTypeOf<MockViewState.Loading>()
                counter shouldBe 10
            }
        }
    }
}
