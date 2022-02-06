package io.github.gmvalentino8.cyklone.tests

import app.cash.turbine.test
import io.github.gmvalentino8.cyklone.contract.State
import io.github.gmvalentino8.cyklone.mock.MockAction
import io.github.gmvalentino8.cyklone.mock.MockEvent
import io.github.gmvalentino8.cyklone.mock.MockProcessor
import io.github.gmvalentino8.cyklone.mock.MockResult
import io.github.gmvalentino8.cyklone.mock.MockViewState
import io.github.gmvalentino8.test.BaseTest
import io.github.gmvalentino8.test.awaitItemAssert
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
    private val processor = MockProcessor<MockAction, MockResult, MockViewState, MockEvent>()

    @BeforeTest
    fun setup() {
        processor.mock = { _, state ->
            when (state.viewState) {
                MockViewState.None -> flowOf(MockResult.Loading)
                MockViewState.Loading -> flowOf(MockResult.Loading, MockResult.UpdateText("Loading"))
                is MockViewState.Stable -> flowOf(MockResult.UpdateText("Stable"))
                is MockViewState.Error -> flowOf(MockResult.UpdateText("Error"))
            }
        }
    }

    @Test
    fun test_process_with_single_result() {
        runTest {
            processor.process(MockAction.Submit(), State(MockViewState.None)).test {
                awaitItem().shouldBeTypeOf<MockResult.Loading>()
                awaitComplete()
            }
        }
    }

    @Test
    fun test_process_with_multiple_results() {
        runTest {
            processor.process(MockAction.Submit(), State(MockViewState.Loading)).test {
                awaitItem().shouldBeTypeOf<MockResult.Loading>()
                awaitItemAssert {
                    shouldBeTypeOf<MockResult.UpdateText>()
                    text shouldBe "Loading"
                }
                cancelAndConsumeRemainingEvents()
            }
        }
    }
}
