package com.quan.finance_app.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quan.finance_app.data.LedgerRepository
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.remote.TransactionDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AllTransactionsUiState(
    val transactions: List<TransactionDto> = emptyList(),
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
)

class AllTransactionsViewModel(
    private val ledgerRepository: LedgerRepository = ServiceLocator.ledgerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AllTransactionsUiState())
    val state: StateFlow<AllTransactionsUiState> = _state.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            ledgerRepository.getTransactionsPaged(page = 0, size = 50)
                .onSuccess { page ->
                    _state.update {
                        it.copy(
                            transactions = page.content,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { err ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Không tải được danh sách giao dịch"
                        )
                    }
                }
        }
    }

    fun deleteTransaction(id: String, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            ledgerRepository.deleteTransaction(id)
                .onSuccess {
                    _state.update { s ->
                        s.copy(
                            transactions = s.transactions.filter { it.id != id },
                            isDeleting = false
                        )
                    }
                    onDeleted()
                }
                .onFailure { err ->
                    _state.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = err.message ?: "Không xoá được giao dịch"
                        )
                    }
                }
        }
    }
}
