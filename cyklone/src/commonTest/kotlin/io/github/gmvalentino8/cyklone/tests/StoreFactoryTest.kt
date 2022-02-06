package io.github.gmvalentino8.cyklone.tests

import io.github.gmvalentino8.cyklone.components.DefaultStore
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.factory.DefaultStoreFactory
import io.github.gmvalentino8.cyklone.mock.MockAction
import io.github.gmvalentino8.cyklone.mock.MockEvent
import io.github.gmvalentino8.cyklone.mock.MockIntent
import io.github.gmvalentino8.cyklone.mock.MockInterpreter
import io.github.gmvalentino8.cyklone.mock.MockProcessor
import io.github.gmvalentino8.cyklone.mock.MockReducer
import io.github.gmvalentino8.cyklone.mock.MockResult
import io.github.gmvalentino8.cyklone.mock.MockViewState
import io.github.gmvalentino8.test.BaseTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class StoreFactoryTest : BaseTest() {
    private val interpreter = MockInterpreter<MockIntent, MockAction, MockViewState, MockEvent>()
    private val processor = MockProcessor<MockAction, MockResult, MockViewState, MockEvent>()
    private val reducer = MockReducer<MockResult, MockViewState, MockEvent>()
    private val storeProvider = DefaultStoreFactory(
        interpreter = interpreter,
        processor = processor,
        reducer = reducer
    )

    @Test
    fun test_create() {
        runTest {
            val store = storeProvider.create(initialState = State(MockViewState.None))
            store.state.value.viewState shouldBe MockViewState.None
            store.shouldBeTypeOf<DefaultStore<MockIntent, MockAction, MockResult, MockViewState, MockEvent>>()
        }
    }
}
