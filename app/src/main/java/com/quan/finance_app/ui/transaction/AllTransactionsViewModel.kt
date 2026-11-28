package com.quan.finance_app.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.remote.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AllTransactionsUiState(
    val filter: HistoryFilter = HistoryFilter(),
    val transactions: List<TransactionDto> = emptyList(),
    val totals: List<CurrencyTotalsDto> = emptyList(),
    val count: Long = 0,
    val nextPage: Int? = 0,
    val loading: Boolean = false,
    val error: String? = null,
    val wallets: List<WalletDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val optionsError: Boolean = false
)

class AllTransactionsViewModel(
    private val optionsRepository: () -> com.quan.finance_app.data.LedgerRepository = { ServiceLocator.ledgerRepository },
    private val fetch: suspend (Int, HistoryFilter) -> HistoryPageDto = ServiceLocator.ledgerRepository::history
) : ViewModel() {
    private val mutable = MutableStateFlow(AllTransactionsUiState())
    val state = mutable.asStateFlow()
    private var request: Job? = null
    private var options: Job? = null
    private var opened = false

    fun open() {
        if (!opened) {
            opened = true
            fetchPage(0)
        }
        loadOptions()
    }

    fun reset() {
        viewModelScope.coroutineContext.cancelChildren()
        opened = false
        mutable.value = AllTransactionsUiState()
    }

    fun loadOptions() {
        options?.cancel()
        options = viewModelScope.launch {
            val ledger = optionsRepository()
            val wallets = ledger.getWallets()
            val categories = ledger.getCategories()
            mutable.update { it.copy(wallets = wallets.getOrDefault(it.wallets),
                categories = categories.getOrDefault(it.categories),
                optionsError = wallets.isFailure || categories.isFailure) }
        }
    }

    fun refreshIfOpened() {
        if (opened) fetchPage(0)
    }

    fun setFilter(filter: HistoryFilter) {
        val previous = mutable.value.filter
        if (previous == filter) return
        mutable.update { it.copy(filter = filter) }
        fetchPage(0, debounce = previous.q != filter.q)
    }

    fun next() {
        if (!mutable.value.loading) mutable.value.nextPage?.let { fetchPage(it) }
    }

    private fun fetchPage(page: Int, debounce: Boolean = false) {
        request?.cancel()
        val filter = mutable.value.filter
        mutable.update {
            if (page == 0) it.copy(transactions = emptyList(), count = 0, totals = emptyList(),
                nextPage = 0, loading = true, error = null)
            else it.copy(loading = true, error = null)
        }
        request = viewModelScope.launch {
            if (debounce) delay(250)
            try {
                val result = fetch(page, filter)
                kotlinx.coroutines.currentCoroutineContext().ensureActive()
                mutable.update { it.copy(
                    transactions = (it.transactions + result.items).distinctBy(TransactionDto::id),
                    count = result.totalElements, totals = result.totalsByCurrency,
                    nextPage = (page + 1).takeIf { next -> next < result.totalPages },
                    loading = false
                ) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                kotlinx.coroutines.currentCoroutineContext().ensureActive()
                mutable.update { it.copy(loading = false, error = "Không tải được giao dịch. Vui lòng thử lại.") }
            }
        }
    }
}
