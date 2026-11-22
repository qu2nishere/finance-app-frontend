package com.quan.finance_app.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quan.finance_app.data.BudgetRepository
import com.quan.finance_app.data.BudgetResult
import com.quan.finance_app.data.CategoryLimitItem
import com.quan.finance_app.data.CurrentBudgetData
import com.quan.finance_app.data.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpenseCategoryOption(
    val id: String,
    val name: String,
    val icon: String?,
    val color: String?
)

data class BudgetUiState(
    val loading: Boolean = false,
    val submitting: Boolean = false,
    val errorMessage: String? = null,
    val actionError: String? = null,
    val budget: CurrentBudgetData? = null,
    val allExpenseCategories: List<ExpenseCategoryOption> = emptyList(),
    val availableCategories: List<ExpenseCategoryOption> = emptyList(),
    val sheetOpen: Boolean = false,
    val editingLimit: CategoryLimitItem? = null
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository = ServiceLocator.budgetRepository,
    private val expenseCategoriesProvider: suspend () -> List<ExpenseCategoryOption> = {
        ServiceLocator.ledgerRepository.getCategories()
            .getOrDefault(emptyList())
            .filter { it.flow.equals("EXPENSE", ignoreCase = true) }
            .map { ExpenseCategoryOption(it.id, it.name, it.icon, it.color) }
    }
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetUiState())
    val state: StateFlow<BudgetUiState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorMessage = null) }
            val categories = runCatching { expenseCategoriesProvider() }.getOrDefault(emptyList())
            when (val result = budgetRepository.getCurrentBudget()) {
                is BudgetResult.Success -> {
                    val existingIds = result.data.limits.map { it.categoryId }.toSet()
                    _state.update {
                        it.copy(
                            loading = false,
                            errorMessage = null,
                            budget = result.data,
                            allExpenseCategories = categories,
                            availableCategories = categories.filterNot { cat -> cat.id in existingIds }
                        )
                    }
                }
                is BudgetResult.Failure -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            errorMessage = result.message,
                            allExpenseCategories = categories,
                            availableCategories = filterAvailable(categories, it.budget)
                        )
                    }
                }
            }
        }
    }

    fun openCreateSheet() {
        _state.update {
            it.copy(
                sheetOpen = true,
                editingLimit = null,
                actionError = null,
                availableCategories = filterAvailable(it.allExpenseCategories, it.budget)
            )
        }
        if (_state.value.allExpenseCategories.isEmpty()) {
            load()
        }
    }

    fun openEditSheet(limit: CategoryLimitItem) {
        _state.update {
            it.copy(
                sheetOpen = true,
                editingLimit = limit,
                actionError = null
            )
        }
    }

    fun dismissSheet() {
        if (_state.value.submitting) return
        _state.update { it.copy(sheetOpen = false, editingLimit = null, actionError = null) }
    }

    fun clearActionError() {
        _state.update { it.copy(actionError = null) }
    }

    fun createLimit(
        category: ExpenseCategoryOption,
        limitAmount: Long,
        alertThreshold: Int = 80
    ) {
        if (_state.value.submitting) return
        if (limitAmount <= 0L) {
            _state.update { it.copy(actionError = "Số tiền hạn mức phải lớn hơn 0₫") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(submitting = true, actionError = null) }
            when (val result = budgetRepository.createLimit(
                categoryId = category.id,
                categoryName = category.name,
                categoryIcon = category.icon,
                categoryColor = category.color,
                limitAmount = limitAmount,
                alertThreshold = alertThreshold,
                periodKey = _state.value.budget?.periodKey?.ifBlank { null }
            )) {
                is BudgetResult.Success -> {
                    _state.update { it.copy(submitting = false, sheetOpen = false, editingLimit = null) }
                    load()
                }
                is BudgetResult.Failure -> {
                    val friendly = if (result.code == "LIMIT_ALREADY_EXISTS") {
                        "Danh mục này đã có hạn mức trong tháng này rồi."
                    } else {
                        result.message
                    }
                    _state.update { it.copy(submitting = false, actionError = friendly) }
                }
            }
        }
    }

    fun updateLimit(limitId: String, limitAmount: Long, alertThreshold: Int) {
        if (_state.value.submitting) return
        if (limitAmount <= 0L) {
            _state.update { it.copy(actionError = "Số tiền hạn mức phải lớn hơn 0₫") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(submitting = true, actionError = null) }
            when (val result = budgetRepository.updateLimit(limitId, limitAmount, alertThreshold)) {
                is BudgetResult.Success -> {
                    _state.update { it.copy(submitting = false, sheetOpen = false, editingLimit = null) }
                    load()
                }
                is BudgetResult.Failure -> {
                    _state.update { it.copy(submitting = false, actionError = result.message) }
                }
            }
        }
    }

    fun deleteLimit(limitId: String) {
        if (_state.value.submitting) return
        viewModelScope.launch {
            _state.update { it.copy(submitting = true, actionError = null) }
            when (val result = budgetRepository.deleteLimit(limitId)) {
                is BudgetResult.Success -> {
                    _state.update { it.copy(submitting = false, sheetOpen = false, editingLimit = null) }
                    load()
                }
                is BudgetResult.Failure -> {
                    _state.update { it.copy(submitting = false, actionError = result.message) }
                }
            }
        }
    }

    private fun filterAvailable(
        allCategories: List<ExpenseCategoryOption>,
        budget: CurrentBudgetData?
    ): List<ExpenseCategoryOption> {
        val usedIds = budget?.limits?.map { it.categoryId }?.toSet().orEmpty()
        return allCategories.filterNot { it.id in usedIds }
    }
}
