package com.gmvalentino.mvi.multiplatform.tests

import app.cash.turbine.test
import com.gmvalentino.mvi.multiplatform.mock.ActionMock
import com.gmvalentino.mvi.multiplatform.mock.BaseProcessorMock
import com.gmvalentino.mvi.multiplatform.mock.EventMock
import com.gmvalentino.mvi.multiplatform.mock.ResultMock
import com.gmvalentino.mvi.multiplatform.mock.StateMock
import com.gmvalentino.test.BaseTest
import com.gmvalentino.test.awaitItemAssert
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
@OptIn(ExperimentalTime::class)
internal class ProcessorTest : BaseTest() {
    private val processor = BaseProcessorMock<StateMock, ActionMock, ResultMock, EventMock>()

    @BeforeTest
    fun setup() {
        processor.mock = { state, _ ->
            when (state) {
                StateMock.None -> flowOf(ResultMock.Loading)
                StateMock.Loading -> flowOf(ResultMock.Loading, ResultMock.UpdateText("Loading"))
                is StateMock.Stable -> flowOf(ResultMock.UpdateText("Stable"))
                is StateMock.Error -> flowOf(ResultMock.UpdateText("Error"))
            }
        }
    }

    @Test
    fun test_process_with_single_result() {
        runTest {
            processor.process(ActionMock.Submit(), StateMock.None).test {
                awaitItem().shouldBeTypeOf<ResultMock.Loading>()
                awaitComplete()
            }
        }
    }

    @Test
    fun test_process_with_multiple_results() {
        runTest {
            processor.process(ActionMock.Submit(), StateMock.Loading).test {
                awaitItem().shouldBeTypeOf<ResultMock.Loading>()
                awaitItemAssert {
                    shouldBeTypeOf<ResultMock.UpdateText>()
                    text shouldBe "Loading"
                }
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun test_publish_events() {
        runTest {
            processor.publish(EventMock.One)
            processor.publish(EventMock.Two)
            processor.publish(EventMock.One)

            processor.events.test {
                awaitItem() shouldBe EventMock.One
                awaitItem() shouldBe EventMock.Two
                awaitItem() shouldBe EventMock.One
                cancelAndConsumeRemainingEvents()
            }
        }
    }
}
