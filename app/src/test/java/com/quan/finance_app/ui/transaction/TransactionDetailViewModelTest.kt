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

    @Test fun lostDeleteResponseAndRepeatedTapCanRetryAsMissing() = runTest(dispatcher) {
        val row = TransactionDto("id", "w", null, null, null, null, null, "EXPENSE", 1, -1, "VND", "", "", null, null)
        var calls = 0
        val vm = TransactionDetailViewModel(remove = {
            calls++
            if (calls == 1) Result.failure(java.io.IOException("Dropped response"))
            else Result.failure(retrofit2.HttpException(retrofit2.Response.error<Unit>(404,
                okhttp3.ResponseBody.create(null, ""))))
        }, fetch = { row })
        vm.load(row.id)
        advanceUntilIdle()
        vm.delete(row.id)
        vm.delete(row.id)
        advanceUntilIdle()
        assertEquals(1, calls)
        assertNotNull(vm.state.value.message)
        vm.delete(row.id)
        advanceUntilIdle()
        assertEquals(Removal.ALREADY_GONE, vm.state.value.removal)
    }

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
