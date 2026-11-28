package com.quan.finance_app.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quan.finance_app.data.LedgerRepository
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.remote.CategoryDto
import com.quan.finance_app.data.remote.CreateTransactionDto
import com.quan.finance_app.data.remote.WalletDto
import java.time.LocalDateTime
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionUiState(
    val categories: List<CategoryDto> = emptyList(),
    val wallets: List<WalletDto> = emptyList(),
    val selectedWallet: WalletDto? = null,
    val selectedCategory: CategoryDto? = null,
    val isExpense: Boolean = true,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

class TransactionViewModel(
    private val ledgerRepository: LedgerRepository = ServiceLocator.ledgerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionUiState())
    val state: StateFlow<TransactionUiState> = _state.asStateFlow()

    fun reset() {
        viewModelScope.coroutineContext.cancelChildren()
        _state.value = TransactionUiState()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                coroutineScope {
                    val catDef = async { ledgerRepository.getCategories() }
                    val walDef = async { ledgerRepository.getWallets() }
                    catDef.await().getOrThrow() to walDef.await().getOrThrow()
                }
            }.onSuccess { (categories, wallets) ->
                val firstWallet = wallets.firstOrNull()
                val defaultCategory = categories.firstOrNull {
                    if (_state.value.isExpense) it.flow == "EXPENSE" else it.flow == "INCOME"
                }

                _state.update {
                    it.copy(
                        categories = categories,
                        wallets = wallets,
                        selectedWallet = it.selectedWallet ?: firstWallet,
                        selectedCategory = it.selectedCategory ?: defaultCategory,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                if (err is kotlinx.coroutines.CancellationException) throw err
                _state.update {
                    it.copy(isLoading = false, errorMessage = err.message ?: "Không tải được danh mục/ví")
                }
            }
        }
    }

    fun setExpense(isExpense: Boolean) {
        if (_state.value.isExpense == isExpense) return
        val defaultCat = _state.value.categories.firstOrNull {
            if (isExpense) it.flow == "EXPENSE" else it.flow == "INCOME"
        }
        _state.update {
            it.copy(
                isExpense = isExpense,
                selectedCategory = defaultCat
            )
        }
    }

    fun selectWallet(wallet: WalletDto) {
        _state.update { it.copy(selectedWallet = wallet) }
    }

    fun selectCategory(category: CategoryDto) {
        _state.update { it.copy(selectedCategory = category) }
    }

    fun createCategory(
        name: String,
        flow: String,
        onSuccess: (CategoryDto) -> Unit = {}
    ) {
        viewModelScope.launch {
            ledgerRepository.createCategory(com.quan.finance_app.data.remote.CreateCategoryDto(name = name, flow = flow))
                .onSuccess { newCategory ->
                    _state.update { current ->
                        val updated = if (current.categories.none { it.id == newCategory.id }) {
                            current.categories + newCategory
                        } else {
                            current.categories
                        }
                        current.copy(
                            categories = updated,
                            selectedCategory = newCategory
                        )
                    }
                    onSuccess(newCategory)
                }
                .onFailure { err ->
                    _state.update { it.copy(errorMessage = err.message ?: "Không thể tạo danh mục") }
                }
        }
    }

    fun createWallet(
        name: String,
        kind: String = "CASH",
        initialBalance: Long = 0L,
        onSuccess: (WalletDto) -> Unit = {}
    ) {
        viewModelScope.launch {
            ledgerRepository.createWallet(com.quan.finance_app.data.remote.CreateWalletDto(name = name, kind = kind, initialBalance = initialBalance))
                .onSuccess { newWallet ->
                    _state.update { current ->
                        val updated = if (current.wallets.none { it.id == newWallet.id }) {
                            current.wallets + newWallet
                        } else {
                            current.wallets
                        }
                        current.copy(
                            wallets = updated,
                            selectedWallet = newWallet
                        )
                    }
                    onSuccess(newWallet)
                }
                .onFailure { err ->
                    _state.update { it.copy(errorMessage = err.message ?: "Không thể tạo ví") }
                }
        }
    }

    fun submitTransaction(
        amount: Long,
        dateTime: LocalDateTime,
        note: String,
        name: String = "",
        onSuccess: () -> Unit
    ) {
        val current = _state.value
        if (current.isSubmitting) return
        if (dateTime > TransactionTime.now()) {
            _state.update { it.copy(errorMessage = "Thời điểm giao dịch phải ở hiện tại hoặc quá khứ.") }
            return
        }
        val wallet = current.selectedWallet
        if (wallet == null) {
            _state.update { it.copy(errorMessage = "Vui lòng chọn ví") }
            return
        }
        if (amount <= 0) {
            _state.update { it.copy(errorMessage = "Số tiền phải lớn hơn 0") }
            return
        }

        val type = if (current.isExpense) "EXPENSE" else "INCOME"
        val occurredAtStr = TransactionTime.toInstant(dateTime).toString()

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null) }
            val req = CreateTransactionDto(
                walletId = wallet.id,
                categoryId = current.selectedCategory?.id,
                type = type,
                amount = amount,
                occurredAt = occurredAtStr,
                note = note.ifBlank { null },
                // Tên giao dịch lưu ở cột transactions.merchant (VARCHAR(150)) có sẵn
                merchant = name.trim().ifBlank { null }
            )

            ledgerRepository.createTransaction(req)
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false) }
                    onSuccess()
                }
                .onFailure { err ->
                    _state.update {
                        it.copy(isSubmitting = false, errorMessage = err.message ?: "Lỗi khi lưu giao dịch")
                    }
                }
        }
    }
}
