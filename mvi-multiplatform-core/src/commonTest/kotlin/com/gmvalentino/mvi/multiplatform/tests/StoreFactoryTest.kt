package com.gmvalentino.mvi.multiplatform.tests

import com.gmvalentino.mvi.multiplatform.components.StoreFactory
import com.gmvalentino.test.BaseTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.gmvalentino.mvi.multiplatform.mock.ActionMock
import com.gmvalentino.mvi.multiplatform.mock.BaseProcessorMock
import com.gmvalentino.mvi.multiplatform.mock.EventMock
import com.gmvalentino.mvi.multiplatform.mock.IntentMock
import com.gmvalentino.mvi.multiplatform.mock.InterpreterMock
import com.gmvalentino.mvi.multiplatform.mock.ReducerMock
import com.gmvalentino.mvi.multiplatform.mock.ResultMock
import com.gmvalentino.mvi.multiplatform.mock.StateMock
import kotlin.test.Test

@ExperimentalCoroutinesApi
class StoreFactoryTest : BaseTest() {

    private val interpreter = InterpreterMock<IntentMock, ActionMock>()
    private val processor = BaseProcessorMock<StateMock, ActionMock, ResultMock, EventMock>()
    private val reducer = ReducerMock<ResultMock, StateMock>()
    private val storeFactory = StoreFactory(
        interpreter = interpreter,
        processor = processor,
        reducer = reducer
    )

    @Test
    fun test_get_or_create() {
        runTest {
            val store = storeFactory.getOrCreate(initialState = StateMock.None)
            val store2 = storeFactory.getOrCreate(initialState = StateMock.Loading)

            store.state.value shouldBe StateMock.None
            store2.state.value shouldBe StateMock.None
            store shouldBe store2
        }
    }
}
