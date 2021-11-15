package com.gmvalentino.mvi.multiplatform.tests

import app.cash.turbine.test
import com.gmvalentino.mvi.multiplatform.components.ActionModifier
import com.gmvalentino.mvi.multiplatform.components.ActionModifiers
import com.gmvalentino.mvi.multiplatform.components.BaseStore
import com.gmvalentino.mvi.multiplatform.components.IntentModifier
import com.gmvalentino.mvi.multiplatform.components.IntentModifiers
import com.gmvalentino.mvi.multiplatform.components.Modifiers
import com.gmvalentino.mvi.multiplatform.components.ResultModifier
import com.gmvalentino.mvi.multiplatform.components.ResultModifiers
import com.gmvalentino.mvi.multiplatform.components.StateModifier
import com.gmvalentino.mvi.multiplatform.components.StateModifiers
import com.gmvalentino.mvi.multiplatform.mock.ActionMock
import com.gmvalentino.mvi.multiplatform.mock.BaseProcessorMock
import com.gmvalentino.mvi.multiplatform.mock.EventMock
import com.gmvalentino.mvi.multiplatform.mock.IntentMock
import com.gmvalentino.mvi.multiplatform.mock.InterpreterMock
import com.gmvalentino.mvi.multiplatform.mock.ReducerMock
import com.gmvalentino.mvi.multiplatform.mock.ResultMock
import com.gmvalentino.mvi.multiplatform.mock.StateMock
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class StoreTest : BaseTest() {
    private val interpreter = InterpreterMock<IntentMock, ActionMock>()
    private val processor = BaseProcessorMock<StateMock, ActionMock, ResultMock, EventMock>()
    private val reducer = ReducerMock<ResultMock, StateMock>()
    private lateinit var baseStore: BaseStore<IntentMock, ActionMock, ResultMock, StateMock, EventMock>

    @BeforeTest
    fun setup() {
        interpreter.mock = { intent ->
            when (intent) {
                is IntentMock.UpdateText -> ActionMock.UpdateText(intent.text)
                is IntentMock.Submit -> ActionMock.Submit(intent.run)
            }
        }

        processor.mock = { _, action ->
            when (action) {
                is ActionMock.UpdateText -> flow {
                    emit(ResultMock.Loading)
                    emit(ResultMock.UpdateText(action.text))
                    processor.publish(EventMock.One)
                }
                is ActionMock.Submit -> flow {
                    try {
                        emit(ResultMock.Loading)
                        action.run()
                    } catch (e: Throwable) {
                        emit(ResultMock.Error(e))
                    } finally {
                        processor.publish(EventMock.Two)
                    }
                }
            }
        }

        reducer.mock = { result, state ->
            when (state) {
                is StateMock.None -> {
                    when (result) {
                        is ResultMock.Loading -> StateMock.Loading
                        is ResultMock.UpdateText -> StateMock.Stable(result.text)
                        is ResultMock.Error -> StateMock.Error(result.throwable)
                    }
                }
                is StateMock.Loading -> {
                    when (result) {
                        is ResultMock.UpdateText -> StateMock.Stable(result.text)
                        is ResultMock.Error -> StateMock.Error(result.throwable)
                        else -> state
                    }
                }
                is StateMock.Stable -> {
                    when (result) {
                        is ResultMock.UpdateText -> StateMock.Stable(result.text)
                        is ResultMock.Loading -> StateMock.Loading
                        else -> state
                    }
                }
                is StateMock.Error -> state
            }
        }

        baseStore = object : BaseStore<IntentMock, ActionMock, ResultMock, StateMock, EventMock>(
            interpreter = interpreter,
            processor = processor,
            reducer = reducer,
            initialState = StateMock.None,
            coroutineContext = coroutineContext
        ) {}
    }

    @Test
    fun test_initial_state() {
        runTest {
            baseStore.state.test {
                awaitItem().shouldBeTypeOf<StateMock.None>()
                expectNoEvents()
                cancel()
            }
        }
    }

    @Test
    fun test_changes_before_collecting() = runTest {
        repeat(10) { baseStore.dispatch(IntentMock.UpdateText(it.toString())) }
        baseStore.dispatch(IntentMock.UpdateText("last"))

        eventually { baseStore.state.value.shouldBeTypeOf<StateMock.Stable>() }

        baseStore.state.test {
            awaitItemAssert {
                shouldBeTypeOf<StateMock.Stable>()
                text shouldBe "last"
            }
            expectNoEvents()
            cancel()
        }
    }

    @Test
    fun test_dispatch() {
        runTest {
            baseStore.state.test {
                awaitItem().shouldBeTypeOf<StateMock.None>()
                repeat(1000) { baseStore.dispatch(IntentMock.UpdateText(it.toString())) }

                baseStore.events.test {
                    repeat(1000) {
                        awaitItem() shouldBe EventMock.One
                    }
                    expectNoEvents()
                    cancel()
                }

                repeat(1000) {
                    awaitItem().shouldBeTypeOf<StateMock.Loading>()
                    awaitItem().let { state ->
                        state.shouldBeTypeOf<StateMock.Stable>()
                        state.text shouldBe it.toString()
                    }
                }

                baseStore.dispatch(IntentMock.Submit())
                awaitItem().shouldBeTypeOf<StateMock.Loading>()

                expectNoEvents()
                cancel()
            }
        }
    }

    @Test
    fun test_dispose() {
        runTest {
            baseStore.dispose()
            baseStore.dispatch(IntentMock.Submit { fail("Should not reach here") })
        }
    }

    @Test
    fun test_exception_thrown() {
        runTest {
            baseStore.state.test {
                awaitItem().shouldBeTypeOf<StateMock.None>()
                baseStore.dispatch(IntentMock.Submit { throw IllegalStateException("message") })
                awaitItem().shouldBeTypeOf<StateMock.Loading>()
                awaitItemAssert {
                    shouldBeTypeOf<StateMock.Error>()
                    throwable.shouldBeTypeOf<IllegalStateException>()
                    throwable.message shouldBe "message"
                }
                expectNoEvents()
                cancel()
            }
        }
    }

    @Test
    fun test_event() {
        runTest {
            baseStore.events.test {
                baseStore.dispatch(IntentMock.UpdateText(""))
                baseStore.dispatch(IntentMock.Submit())
                awaitItem() shouldBe EventMock.One
                awaitItem() shouldBe EventMock.Two
                expectNoEvents()
                cancel()
            }
        }
    }

    @Test
    fun test_collect() {
        val stateCounter = MutableStateFlow(0)
        val eventCounter = MutableStateFlow(0)
        runTest {
            val job = baseStore.collect(
                onState = {
                    stateCounter.value++
                    when (stateCounter.value) {
                        1 -> it shouldBe StateMock.None
                        2 -> it shouldBe StateMock.Loading
                    }
                },
                onEvent = {
                    eventCounter.value++
                    when (eventCounter.value) {
                        1 -> it shouldBe EventMock.Two
                    }
                }
            )
            val childJobs = job.children.toList()
            childJobs.size shouldBe 2
            childJobs.forEach {
                it.isActive shouldBe true
                it.start() shouldBe false
            }

            baseStore.dispatch(IntentMock.Submit {})
            eventually { baseStore.state.value.shouldBeTypeOf<StateMock.Loading>() }
            stateCounter.value shouldBe 2
            eventCounter.value shouldBe 1

            job.cancel()
            baseStore.dispatch(IntentMock.UpdateText(""))
            eventually { baseStore.state.value.shouldBeTypeOf<StateMock.Stable>() }
            childJobs.forEach { it.isActive shouldBe false }
            stateCounter.value shouldBe 2
            eventCounter.value shouldBe 1
        }
    }

    @Test
    fun test_apply_modifiers() {
        var counter = 0

        class ModifierMock :
            IntentModifier<IntentMock, StateMock>,
            ActionModifier<ActionMock, StateMock>,
            ResultModifier<ResultMock, StateMock>,
            StateModifier<StateMock> {
            override fun modifyIntents(
                input: Flow<IntentMock>,
                state: StateFlow<StateMock>
            ): Flow<IntentMock> = input.onEach { counter += 1 }

            override fun modifyActions(
                input: Flow<ActionMock>,
                state: StateFlow<StateMock>
            ): Flow<ActionMock> = input.onEach { counter += 2 }

            override fun modifyResults(
                input: Flow<ResultMock>,
                state: StateFlow<StateMock>
            ): Flow<ResultMock> = input.onEach { counter += 3 }

            override fun modifyStates(
                input: Flow<StateMock>
            ): Flow<StateMock> = input.onEach { counter += 4 }.map { StateMock.Loading }
        }

        baseStore = object : BaseStore<IntentMock, ActionMock, ResultMock, StateMock, EventMock>(
            interpreter = interpreter,
            processor = processor,
            reducer = reducer,
            initialState = StateMock.None,
            coroutineContext = coroutineContext,
            modifiers = Modifiers(
                intentModifiers = IntentModifiers(ModifierMock()),
                actionModifiers = ActionModifiers(ModifierMock()),
                resultModifiers = ResultModifiers(ModifierMock()),
                stateModifiers = StateModifiers(ModifierMock())
            )
        ) {}

        runTest {
            baseStore.state.test {
                awaitItem().shouldBeTypeOf<StateMock.None>()
                baseStore.dispatch(IntentMock.Submit {})
                awaitItem().shouldBeTypeOf<StateMock.Loading>()
                counter shouldBe 10
            }
        }
    }
}
