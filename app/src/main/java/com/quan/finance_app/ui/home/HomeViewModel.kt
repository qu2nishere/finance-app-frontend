package com.quan.finance_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quan.finance_app.data.LedgerRepository
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.remote.TransactionDto
import com.quan.finance_app.data.remote.WalletDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val wallets: List<WalletDto> = emptyList(),
    val totalBalance: Long = 0L,
    val monthIncome: Long = 0L,
    val monthExpense: Long = 0L,
    val recentTransactions: List<TransactionDto> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val ledgerRepository: LedgerRepository = ServiceLocator.ledgerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                coroutineScope {
                    val walletsDeferred = async { ledgerRepository.getWallets() }
                    val summaryDeferred = async { ledgerRepository.getMonthlySummary() }
                    val recentDeferred = async { ledgerRepository.getRecentTransactions(5) }

                    Triple(
                        walletsDeferred.await(),
                        summaryDeferred.await(),
                        recentDeferred.await()
                    )
                }
            }.onSuccess { (walletsResult, summaryResult, recentResult) ->
                val wallets = walletsResult.getOrNull() ?: emptyList()
                val summary = summaryResult.getOrNull()
                val recent = recentResult.getOrNull() ?: emptyList()

                val total = wallets
                    .filter { it.includeInTotal }
                    .sumOf { it.currentBalance }

                _state.update {
                    it.copy(
                        wallets = wallets,
                        totalBalance = total,
                        monthIncome = summary?.totalIncome ?: 0L,
                        monthExpense = summary?.totalExpense ?: 0L,
                        recentTransactions = recent,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Không thể kết nối đến máy chủ"
                    )
                }
            }
        }
    }
}
