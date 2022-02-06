package com.gmvalentino.cyklone.tests

import com.gmvalentino.cyklone.components.DefaultStore
import com.gmvalentino.cyklone.contract.State
import com.gmvalentino.cyklone.factory.DefaultStoreFactory
import com.gmvalentino.cyklone.mock.MockAction
import com.gmvalentino.cyklone.mock.MockEvent
import com.gmvalentino.cyklone.mock.MockIntent
import com.gmvalentino.cyklone.mock.MockInterpreter
import com.gmvalentino.cyklone.mock.MockProcessor
import com.gmvalentino.cyklone.mock.MockReducer
import com.gmvalentino.cyklone.mock.MockResult
import com.gmvalentino.cyklone.mock.MockViewState
import com.gmvalentino.test.BaseTest
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
