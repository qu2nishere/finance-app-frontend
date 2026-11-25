package com.quan.finance_app.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quan.finance_app.data.CashflowSummaryData
import com.quan.finance_app.data.CategoryBreakdownItem
import com.quan.finance_app.data.MonthlyTransactionsData
import com.quan.finance_app.data.MonthlyTrendData
import com.quan.finance_app.data.ReportRepository
import com.quan.finance_app.data.ReportResult
import com.quan.finance_app.data.ServiceLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale

data class IncomeFilterOption(
    val categoryId: String?,
    val label: String
)

data class ReportUiState(
    val year: Int = YearMonth.now(USER_ZONE).year,
    val month: Int = YearMonth.now(USER_ZONE).monthValue,
    val selectedTab: Int = 0,
    val selectedIncomeCategoryId: String? = null,
    val selectedIncomeCategoryLabel: String = ALL_INCOME_LABEL,
    val selectedDate: LocalDate? = null,
    val incomeTrend: MonthlyTrendData? = null,
    val expenseTrend: MonthlyTrendData? = null,
    val cashflowIncomeTrend: MonthlyTrendData? = null,
    val cashflowSummary: CashflowSummaryData? = null,
    val incomeFilterOptions: List<IncomeFilterOption> = DEFAULT_INCOME_OPTIONS,
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val expenseTransactions: MonthlyTransactionsData? = null,
    val isTrendLoading: Boolean = false,
    val isExpenseTrendLoading: Boolean = false,
    val isCashflowIncomeLoading: Boolean = false,
    val isCashflowLoading: Boolean = false,
    val isTransactionsLoading: Boolean = false,
    val trendError: String? = null,
    val expenseTrendError: String? = null,
    val cashflowIncomeError: String? = null,
    val cashflowError: String? = null,
    val transactionsError: String? = null,
    val canGoNext: Boolean = false
) {
    val periodKey: String
        get() = String.format(Locale.ROOT, "%04d-%02d", year, month)

    companion object {
        val USER_ZONE: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")
        const val ALL_INCOME_LABEL = "Tất cả nguồn thu"

        val DEFAULT_INCOME_OPTIONS = listOf(
            IncomeFilterOption(null, ALL_INCOME_LABEL),
            IncomeFilterOption("22222222-0000-4000-8000-000000000001", "Lương"),
            IncomeFilterOption("22222222-0000-4000-8000-000000000002", "Thưởng"),
            IncomeFilterOption("22222222-0000-4000-8000-000000000003", "Freelance")
        )
    }
}

class ReportViewModel(
    private val reportRepository: ReportRepository = ServiceLocator.reportRepository,
    initialYearMonth: YearMonth = YearMonth.now(ReportUiState.USER_ZONE),
    autoLoadOnInit: Boolean = true,
    private val currentYearMonthProvider: () -> YearMonth = { YearMonth.now(ReportUiState.USER_ZONE) }
) : ViewModel() {

    private val _state = MutableStateFlow(
        ReportUiState(
            year = initialYearMonth.year,
            month = initialYearMonth.monthValue,
            canGoNext = initialYearMonth.isBefore(currentYearMonthProvider())
        )
    )
    val state: StateFlow<ReportUiState> = _state.asStateFlow()

    private var allJob: Job? = null
    private var trendJob: Job? = null
    private var transactionsJob: Job? = null

    init {
        if (autoLoadOnInit) {
            refresh()
        }
    }

    fun reset() {
        allJob?.cancel()
        trendJob?.cancel()
        transactionsJob?.cancel()
        val nowYm = currentYearMonthProvider()
        _state.value = ReportUiState(
            year = nowYm.year,
            month = nowYm.monthValue,
            canGoNext = false
        )
    }

    fun selectTab(tabIndex: Int) {
        _state.update { it.copy(selectedTab = tabIndex) }
    }

    fun goPreviousMonth() {
        val current = _state.value
        val prev = YearMonth.of(current.year, current.month).minusMonths(1)
        val nowYm = currentYearMonthProvider()
        _state.update {
            it.copy(
                year = prev.year,
                month = prev.monthValue,
                selectedDate = null,
                canGoNext = prev.isBefore(nowYm)
            )
        }
        refresh()
    }

    fun goNextMonth() {
        val current = _state.value
        val currentYm = YearMonth.of(current.year, current.month)
        val nowYm = currentYearMonthProvider()
        if (!currentYm.isBefore(nowYm)) return
        val next = currentYm.plusMonths(1)
        _state.update {
            it.copy(
                year = next.year,
                month = next.monthValue,
                selectedDate = null,
                canGoNext = next.isBefore(nowYm)
            )
        }
        refresh()
    }

    fun selectIncomeCategory(categoryId: String?, label: String) {
        val current = _state.value
        if (current.selectedIncomeCategoryId == categoryId) return
        _state.update {
            it.copy(
                selectedIncomeCategoryId = categoryId,
                selectedIncomeCategoryLabel = label
            )
        }
        loadIncomeTrendOnly()
    }

    fun selectDate(date: LocalDate) {
        val current = _state.value
        val targetYm = YearMonth.from(date)
        val currentYm = YearMonth.of(current.year, current.month)
        if (targetYm != currentYm) {
            val nowYm = currentYearMonthProvider()
            _state.update {
                it.copy(
                    year = targetYm.year,
                    month = targetYm.monthValue,
                    selectedDate = date,
                    canGoNext = targetYm.isBefore(nowYm)
                )
            }
            refresh()
        } else {
            _state.update { it.copy(selectedDate = date) }
            loadTransactionsOnly()
        }
    }

    fun clearDateFilter() {
        if (_state.value.selectedDate == null) return
        _state.update { it.copy(selectedDate = null) }
        loadTransactionsOnly()
    }

    fun refresh() {
        allJob?.cancel()
        trendJob?.cancel()
        transactionsJob?.cancel()
        allJob = viewModelScope.launch {
            val snap = _state.value
            val period = snap.periodKey
            val needsSeparateCashflowIncome = snap.selectedIncomeCategoryId != null
            _state.update {
                it.copy(
                    isTrendLoading = true,
                    isExpenseTrendLoading = true,
                    isCashflowIncomeLoading = true,
                    isCashflowLoading = true,
                    isTransactionsLoading = true,
                    trendError = null,
                    expenseTrendError = null,
                    cashflowIncomeError = null,
                    cashflowError = null,
                    transactionsError = null
                )
            }
            coroutineScope {
                val incomeTrendDeferred = async {
                    reportRepository.getMonthlyTrend(
                        period = period,
                        months = 5,
                        type = "INCOME",
                        categoryId = snap.selectedIncomeCategoryId
                    )
                }
                val expenseTrendDeferred = async {
                    reportRepository.getMonthlyTrend(
                        period = period,
                        months = 5,
                        type = "EXPENSE"
                    )
                }
                val cashflowIncomeDeferred = if (needsSeparateCashflowIncome) {
                    async {
                        reportRepository.getMonthlyTrend(
                            period = period,
                            months = 5,
                            type = "INCOME",
                            categoryId = null
                        )
                    }
                } else {
                    null
                }
                val txnDeferred = async {
                    reportRepository.getTransactions(
                        period = period,
                        date = snap.selectedDate,
                        type = "EXPENSE"
                    )
                }
                val cashflowSummaryDeferred = async {
                    reportRepository.getCashflowSummary(period = period)
                }

                applyIncomeTrendResult(
                    result = incomeTrendDeferred.await(),
                    expectedPeriod = period,
                    alsoUpdateCashflow = !needsSeparateCashflowIncome
                )
                applyExpenseTrendResult(expenseTrendDeferred.await(), expectedPeriod = period)
                if (cashflowIncomeDeferred != null) {
                    applyCashflowIncomeResult(cashflowIncomeDeferred.await(), expectedPeriod = period)
                }
                applyCashflowSummaryResult(cashflowSummaryDeferred.await(), expectedPeriod = period)
                applyTransactionsResult(txnDeferred.await(), expectedPeriod = period)
            }
        }
    }

    private fun loadIncomeTrendOnly() {
        trendJob?.cancel()
        trendJob = viewModelScope.launch {
            val snap = _state.value
            val period = snap.periodKey
            val isUnfiltered = snap.selectedIncomeCategoryId == null
            _state.update { it.copy(isTrendLoading = true, trendError = null) }
            val res = reportRepository.getMonthlyTrend(
                period = period,
                months = 5,
                type = "INCOME",
                categoryId = snap.selectedIncomeCategoryId
            )
            applyIncomeTrendResult(res, expectedPeriod = period, alsoUpdateCashflow = isUnfiltered)
        }
    }

    private fun loadTransactionsOnly() {
        transactionsJob?.cancel()
        transactionsJob = viewModelScope.launch {
            val snap = _state.value
            val period = snap.periodKey
            _state.update { it.copy(isTransactionsLoading = true, transactionsError = null) }
            val res = reportRepository.getTransactions(
                period = period,
                date = snap.selectedDate,
                type = "EXPENSE"
            )
            applyTransactionsResult(res, expectedPeriod = period)
        }
    }

    private fun applyIncomeTrendResult(
        result: ReportResult<MonthlyTrendData>,
        expectedPeriod: String,
        alsoUpdateCashflow: Boolean
    ) {
        if (_state.value.periodKey != expectedPeriod) return
        when (result) {
            is ReportResult.Success -> {
                val data = result.data
                val mergedChips = mergeIncomeChips(data.categoryBreakdown)
                _state.update {
                    it.copy(
                        incomeTrend = data,
                        categoryBreakdown = data.categoryBreakdown,
                        incomeFilterOptions = mergedChips,
                        isTrendLoading = false,
                        trendError = null,
                        cashflowIncomeTrend = if (alsoUpdateCashflow) data else it.cashflowIncomeTrend,
                        isCashflowIncomeLoading = if (alsoUpdateCashflow) false else it.isCashflowIncomeLoading,
                        cashflowIncomeError = if (alsoUpdateCashflow) null else it.cashflowIncomeError
                    )
                }
            }
            is ReportResult.Failure -> {
                _state.update {
                    it.copy(
                        isTrendLoading = false,
                        trendError = result.message,
                        isCashflowIncomeLoading = if (alsoUpdateCashflow) false else it.isCashflowIncomeLoading,
                        cashflowIncomeError = if (alsoUpdateCashflow) result.message else it.cashflowIncomeError
                    )
                }
            }
        }
    }

    private fun applyExpenseTrendResult(
        result: ReportResult<MonthlyTrendData>,
        expectedPeriod: String
    ) {
        if (_state.value.periodKey != expectedPeriod) return
        when (result) {
            is ReportResult.Success -> {
                _state.update {
                    it.copy(
                        expenseTrend = result.data,
                        isExpenseTrendLoading = false,
                        expenseTrendError = null
                    )
                }
            }
            is ReportResult.Failure -> {
                _state.update {
                    it.copy(
                        isExpenseTrendLoading = false,
                        expenseTrendError = result.message
                    )
                }
            }
        }
    }

    private fun applyCashflowIncomeResult(
        result: ReportResult<MonthlyTrendData>,
        expectedPeriod: String
    ) {
        if (_state.value.periodKey != expectedPeriod) return
        when (result) {
            is ReportResult.Success -> {
                _state.update {
                    it.copy(
                        cashflowIncomeTrend = result.data,
                        isCashflowIncomeLoading = false,
                        cashflowIncomeError = null
                    )
                }
            }
            is ReportResult.Failure -> {
                _state.update {
                    it.copy(
                        isCashflowIncomeLoading = false,
                        cashflowIncomeError = result.message
                    )
                }
            }
        }
    }

    private fun applyCashflowSummaryResult(
        result: ReportResult<CashflowSummaryData>,
        expectedPeriod: String
    ) {
        if (_state.value.periodKey != expectedPeriod) return
        when (result) {
            is ReportResult.Success -> {
                _state.update {
                    it.copy(
                        cashflowSummary = result.data,
                        isCashflowLoading = false,
                        cashflowError = null
                    )
                }
            }
            is ReportResult.Failure -> {
                _state.update {
                    it.copy(
                        isCashflowLoading = false,
                        cashflowError = result.message
                    )
                }
            }
        }
    }

    private fun applyTransactionsResult(
        result: ReportResult<MonthlyTransactionsData>,
        expectedPeriod: String
    ) {
        if (_state.value.periodKey != expectedPeriod) return
        when (result) {
            is ReportResult.Success -> {
                _state.update {
                    it.copy(
                        expenseTransactions = result.data,
                        isTransactionsLoading = false,
                        transactionsError = null
                    )
                }
            }
            is ReportResult.Failure -> {
                _state.update {
                    it.copy(
                        isTransactionsLoading = false,
                        transactionsError = result.message
                    )
                }
            }
        }
    }

    private fun mergeIncomeChips(breakdown: List<CategoryBreakdownItem>): List<IncomeFilterOption> {
        val result = ArrayList<IncomeFilterOption>()
        result.addAll(ReportUiState.DEFAULT_INCOME_OPTIONS)
        val seenIds = result.mapNotNull { it.categoryId }.toMutableSet()
        for (item in breakdown) {
            val id = item.categoryId ?: continue
            if (seenIds.add(id)) {
                result.add(IncomeFilterOption(id, item.categoryName))
            }
        }
        return result
    }
}
