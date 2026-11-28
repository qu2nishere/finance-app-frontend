package com.quan.finance_app.ui.transaction

import com.quan.finance_app.data.remote.TransactionDto
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun cleanup() = Dispatchers.resetMain()

    @Test fun previousIdAndPreviousSessionCannotPublishLateResults() = runTest(dispatcher) {
        val vm = TransactionDetailViewModel { id ->
            withContext(NonCancellable) { delay(100) }
            TransactionDto(id, "w", null, null, null, null, null, "EXPENSE", 1, -1, "VND", "", "", null, null)
        }
        vm.load("first")
        runCurrent()
        vm.load("second")
        advanceUntilIdle()
        assertEquals("second", vm.state.value.transaction?.id)
        vm.load("private")
        runCurrent()
        vm.reset()
        advanceUntilIdle()
        assertEquals(DetailState(), vm.state.value)
    }
}
