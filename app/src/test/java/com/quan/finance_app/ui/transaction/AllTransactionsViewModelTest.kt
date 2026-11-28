package com.quan.finance_app.ui.transaction

import com.quan.finance_app.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AllTransactionsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun cleanup() = Dispatchers.resetMain()

    @Test fun rapidSearchAndResetDiscardPendingWork() = runTest(dispatcher) {
        val seen = mutableListOf<String>()
        val vm = AllTransactionsViewModel { page, filter ->
            seen.add(filter.q)
            HistoryPageDto(emptyList(), page, 20, 1, 1, emptyList())
        }
        vm.setFilter(HistoryFilter(q = "a"))
        vm.setFilter(HistoryFilter(q = "ab"))
        advanceUntilIdle()
        assertEquals(listOf("ab"), seen)
        vm.setFilter(HistoryFilter(q = "private"))
        vm.reset()
        advanceUntilIdle()
        assertEquals(AllTransactionsUiState(), vm.state.value)
        assertEquals(listOf("ab"), seen)
    }

    @Test fun nextPageRetriesOnceAndRetainsLoadedIds() = runTest(dispatcher) {
        val calls = mutableListOf<Int>()
        val vm = AllTransactionsViewModel { page, _ ->
            calls.add(page)
            if (calls == listOf(0, 1)) throw IOException()
            val row = TransactionDto(page.toString(), "wallet", null, null, null, null, null,
                "INCOME", 10, 10, "VND", "", "", null, null)
            HistoryPageDto(listOf(row), page, 1, 2, 2, emptyList())
        }
        vm.next()
        advanceUntilIdle()
        vm.next()
        vm.next()
        advanceUntilIdle()
        assertEquals(listOf("0"), vm.state.value.transactions.map { it.id })
        assertNotNull(vm.state.value.error)
        vm.next()
        advanceUntilIdle()
        assertEquals(listOf("0", "1"), vm.state.value.transactions.map { it.id })
        assertEquals(listOf(0, 1, 1), calls)
        assertNull(vm.state.value.nextPage)
    }
}
