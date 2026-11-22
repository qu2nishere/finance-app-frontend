package com.quan.finance_app.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quan.finance_app.data.BudgetRepository
import com.quan.finance_app.data.BudgetResult
import com.quan.finance_app.data.GoalStatus
import com.quan.finance_app.data.SavingsGoalItem
import com.quan.finance_app.data.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class WalletOption(
    val id: String,
    val name: String
)

data class GoalsUiState(
    val loading: Boolean = false,
    val submitting: Boolean = false,
    val errorMessage: String? = null,
    val actionError: String? = null,
    val activeGoals: List<SavingsGoalItem> = emptyList(),
    val completedGoals: List<SavingsGoalItem> = emptyList(),
    val totalSaved: Long = 0L,
    val totalTarget: Long = 0L,
    val wallets: List<WalletOption> = emptyList(),
    val actionCompleted: Boolean = false
) {
    val allGoals: List<SavingsGoalItem>
        get() = activeGoals + completedGoals
}

class GoalsViewModel(
    private val budgetRepository: BudgetRepository = ServiceLocator.budgetRepository,
    private val walletsProvider: suspend () -> List<WalletOption> = {
        ServiceLocator.ledgerRepository.getWallets()
            .getOrDefault(emptyList())
            .map { WalletOption(it.id, it.name) }
    }
) : ViewModel() {

    private val _state = MutableStateFlow(GoalsUiState())
    val state: StateFlow<GoalsUiState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            val wallets = runCatching { walletsProvider() }.getOrDefault(emptyList())
            when (val result = budgetRepository.getGoals()) {
                is BudgetResult.Success -> {
                    applyGoals(result.data, wallets)
                }
                is BudgetResult.Failure -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            errorMessage = result.message,
                            wallets = wallets
                        )
                    }
                }
            }
        }
    }

    fun findGoalById(goalId: String?): SavingsGoalItem? {
        if (goalId.isNullOrBlank()) return null
        return _state.value.allGoals.firstOrNull { it.id == goalId }
    }

    fun clearActionError() {
        _state.update { it.copy(actionError = null) }
    }

    fun consumeActionCompleted() {
        _state.update { it.copy(actionCompleted = false) }
    }

    fun createGoal(
        name: String,
        targetAmount: Long,
        icon: String = "CardGiftcard",
        color: String = "#0FA162",
        targetDate: LocalDate? = null,
        wallet: WalletOption? = null,
        priority: Int = 0,
        note: String? = null
    ) {
        if (_state.value.submitting) return
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            _state.update { it.copy(actionError = "Tên mục tiêu không được để trống") }
            return
        }
        if (targetAmount <= 0L) {
            _state.update { it.copy(actionError = "Số tiền mục tiêu phải lớn hơn 0₫") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(submitting = true, actionError = null) }
            when (val result = budgetRepository.createGoal(
                name = trimmedName,
                icon = icon,
                color = color,
                targetAmount = targetAmount,
                targetDate = targetDate,
                walletId = wallet?.id,
                walletName = wallet?.name,
                priority = priority,
                note = note?.trim()?.ifBlank { null }
            )) {
                is BudgetResult.Success -> {
                    _state.update { it.copy(submitting = false, actionCompleted = true) }
                    load()
                }
                is BudgetResult.Failure -> {
                    _state.update { it.copy(submitting = false, actionError = result.message) }
                }
            }
        }
    }

    fun updateGoal(
        goalId: String,
        name: String,
        targetAmount: Long,
        icon: String,
        color: String,
        targetDate: LocalDate?,
        wallet: WalletOption?,
        priority: Int,
        note: String?
    ) {
        if (_state.value.submitting) return
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            _state.update { it.copy(actionError = "Tên mục tiêu không được để trống") }
            return
        }
        if (targetAmount <= 0L) {
            _state.update { it.copy(actionError = "Số tiền mục tiêu phải lớn hơn 0₫") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(submitting = true, actionError = null) }
            when (val result = budgetRepository.updateGoal(
                id = goalId,
                name = trimmedName,
                icon = icon,
                color = color,
                targetAmount = targetAmount,
                targetDate = targetDate,
                walletId = wallet?.id,
                walletName = wallet?.name,
                priority = priority,
                note = note?.trim()?.ifBlank { null }
            )) {
                is BudgetResult.Success -> {
                    _state.update { it.copy(submitting = false, actionCompleted = true) }
                    load()
                }
                is BudgetResult.Failure -> {
                    _state.update { it.copy(submitting = false, actionError = result.message) }
                }
            }
        }
    }

    fun deleteGoal(goalId: String) {
        if (_state.value.submitting) return
        viewModelScope.launch {
            _state.update { it.copy(submitting = true, actionError = null) }
            when (val result = budgetRepository.deleteGoal(goalId)) {
                is BudgetResult.Success -> {
                    _state.update { it.copy(submitting = false, actionCompleted = true) }
                    load()
                }
                is BudgetResult.Failure -> {
                    _state.update { it.copy(submitting = false, actionError = result.message) }
                }
            }
        }
    }

    fun deposit(goalId: String, amount: Long, note: String? = null, onSuccess: () -> Unit = {}) {
        if (_state.value.submitting) return
        if (amount <= 0L) {
            _state.update { it.copy(actionError = "Số tiền nạp phải lớn hơn 0₫") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(submitting = true, actionError = null) }
            when (val result = budgetRepository.depositToGoal(goalId, amount, note?.trim()?.ifBlank { null })) {
                is BudgetResult.Success -> {
                    val updatedGoal = result.data.goal
                    val merged = _state.value.allGoals.map { existing ->
                        if (existing.id == updatedGoal.id) updatedGoal else existing
                    }.let { list ->
                        if (list.any { it.id == updatedGoal.id }) list else list + updatedGoal
                    }
                    applyGoals(merged, _state.value.wallets)
                    _state.update { it.copy(submitting = false) }
                    onSuccess()
                    load()
                }
                is BudgetResult.Failure -> {
                    _state.update { it.copy(submitting = false, actionError = result.message) }
                }
            }
        }
    }

    private fun applyGoals(goals: List<SavingsGoalItem>, wallets: List<WalletOption>) {
        val active = goals.filter { it.status == GoalStatus.ACTIVE }
        val completed = goals.filter { it.status == GoalStatus.COMPLETED }
        val displayed = active + completed
        _state.update {
            it.copy(
                loading = false,
                errorMessage = null,
                activeGoals = active,
                completedGoals = completed,
                totalSaved = displayed.sumOf { g -> g.savedAmount },
                totalTarget = displayed.sumOf { g -> g.targetAmount },
                wallets = wallets
            )
        }
    }
}
