package com.quan.finance_app.ui.report

import com.quan.finance_app.data.CashflowSummaryData
import com.quan.finance_app.data.CategoryBreakdownItem
import com.quan.finance_app.data.DailyTransactionGroup
import com.quan.finance_app.data.MonthlyBarPoint
import com.quan.finance_app.data.MonthlyTransactionsData
import com.quan.finance_app.data.MonthlyTrendData
import com.quan.finance_app.data.ReportRepository
import com.quan.finance_app.data.ReportResult
import com.quan.finance_app.data.ReportTransactionItem
import com.quan.finance_app.data.WalletCashflowItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepo: FakeReportRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeReportRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsIncomeExpenseCashflowAndTransactionsWithoutDuplicateIncomeCall() = runTest {
        val vm = ReportViewModel(
            reportRepository = fakeRepo,
            initialYearMonth = YearMonth.of(2026, 8),
            currentYearMonthProvider = { YearMonth.of(2026, 8) }
        )

        val state = vm.state.value
        assertEquals("2026-08", state.periodKey)
        assertFalse(state.canGoNext)
        assertFalse(state.isTrendLoading)
        assertFalse(state.isExpenseTrendLoading)
        assertFalse(state.isCashflowIncomeLoading)
        assertFalse(state.isCashflowLoading)
        assertFalse(state.isTransactionsLoading)
        assertNull(state.trendError)
        assertNull(state.expenseTrendError)
        assertNull(state.cashflowIncomeError)
        assertNull(state.cashflowError)
        assertNull(state.transactionsError)
        assertEquals(18_200_000L, state.incomeTrend?.selectedAmount)
        assertEquals(18_200_000L, state.cashflowIncomeTrend?.selectedAmount)
        assertEquals(9_712_000L, state.expenseTrend?.selectedAmount)
        assertEquals(11_510_000L, state.cashflowSummary?.openingBalance)
        assertEquals(19_998_000L, state.cashflowSummary?.closingBalance)
        assertEquals(1, state.cashflowSummary?.wallets?.size)
        assertEquals(5, state.incomeTrend?.points?.size)
        assertEquals(5, state.expenseTrend?.points?.size)
        assertEquals(285_000L, state.expenseTransactions?.monthTotalAmount)
        assertTrue(state.incomeFilterOptions.any { it.label == "Đầu tư" })
        // Khi chip = Tất cả, chỉ gọi 2 lần monthly-trend (1 INCOME + 1 EXPENSE), không gọi trùng
        assertEquals(2, fakeRepo.trendCalls.size)
        assertTrue(fakeRepo.trendCalls.any { it.type == "INCOME" && it.categoryId == null })
        assertTrue(fakeRepo.trendCalls.any { it.type == "EXPENSE" && it.categoryId == null })
    }

    @Test
    fun goPreviousAndNextMonth_updatesPeriodForAllCallsAndBlocksFutureMonth() = runTest {
        val vm = ReportViewModel(
            reportRepository = fakeRepo,
            initialYearMonth = YearMonth.of(2026, 8),
            currentYearMonthProvider = { YearMonth.of(2026, 8) }
        )

        // Đang ở tháng hiện tại → không cho tiến tới tháng tương lai
        assertFalse(vm.state.value.canGoNext)
        fakeRepo.trendCalls.clear()
        vm.goNextMonth()
        assertEquals("2026-08", vm.state.value.periodKey)
        assertTrue(fakeRepo.trendCalls.isEmpty())

        // Lùi tháng → canGoNext = true, gọi lại cả INCOME + EXPENSE + transactions với period mới
        vm.selectDate(LocalDate.of(2026, 8, 22))
        assertEquals(LocalDate.of(2026, 8, 22), vm.state.value.selectedDate)

        fakeRepo.trendCalls.clear()
        vm.goPreviousMonth()
        assertEquals("2026-07", vm.state.value.periodKey)
        assertTrue(vm.state.value.canGoNext)
        assertNull(vm.state.value.selectedDate)
        assertTrue(fakeRepo.trendCalls.all { it.period == "2026-07" })
        assertTrue(fakeRepo.trendCalls.any { it.type == "INCOME" })
        assertTrue(fakeRepo.trendCalls.any { it.type == "EXPENSE" })
        assertEquals("2026-07", fakeRepo.lastTransactionsPeriod)
        assertEquals("2026-07", fakeRepo.lastCashflowSummaryPeriod)

        // Tiến về lại tháng hiện tại → canGoNext = false
        vm.goNextMonth()
        assertEquals("2026-08", vm.state.value.periodKey)
        assertFalse(vm.state.value.canGoNext)
    }

    @Test
    fun selectIncomeCategory_doesNotAffectCashflowIncomeTrend_andMonthChangeFetchesAllThree() = runTest {
        val vm = ReportViewModel(
            reportRepository = fakeRepo,
            initialYearMonth = YearMonth.of(2026, 8),
            currentYearMonthProvider = { YearMonth.of(2026, 8) }
        )

        val salaryId = "22222222-0000-4000-8000-000000000001"
        vm.selectIncomeCategory(salaryId, "Lương")

        assertEquals(salaryId, vm.state.value.selectedIncomeCategoryId)
        assertEquals("Lương", vm.state.value.selectedIncomeCategoryLabel)
        assertEquals(18_000_000L, vm.state.value.incomeTrend?.selectedAmount)
        // Dòng tiền vẫn giữ thu nhập không lọc
        assertEquals(18_200_000L, vm.state.value.cashflowIncomeTrend?.selectedAmount)

        // Đổi tháng khi đang lọc chip → gọi đủ 3 lời gọi trend cho period mới
        fakeRepo.trendCalls.clear()
        vm.goPreviousMonth()
        assertEquals("2026-07", vm.state.value.periodKey)
        assertEquals(3, fakeRepo.trendCalls.size)
        assertTrue(fakeRepo.trendCalls.all { it.period == "2026-07" })
        assertTrue(fakeRepo.trendCalls.any { it.type == "INCOME" && it.categoryId == salaryId })
        assertTrue(fakeRepo.trendCalls.any { it.type == "INCOME" && it.categoryId == null })
        assertTrue(fakeRepo.trendCalls.any { it.type == "EXPENSE" && it.categoryId == null })
        assertEquals(18_000_000L, vm.state.value.incomeTrend?.selectedAmount)
        assertEquals(18_200_000L, vm.state.value.cashflowIncomeTrend?.selectedAmount)
        assertEquals(9_712_000L, vm.state.value.expenseTrend?.selectedAmount)
    }

    @Test
    fun selectDateAndClearDateFilter_filtersAndRestoresWholeMonth() = runTest {
        val vm = ReportViewModel(
            reportRepository = fakeRepo,
            initialYearMonth = YearMonth.of(2026, 8)
        )

        val picked = LocalDate.of(2026, 8, 22)
        vm.selectDate(picked)
        assertEquals(picked, vm.state.value.selectedDate)
        assertEquals(picked, fakeRepo.lastTransactionsDate)
        assertEquals(72_000L, vm.state.value.expenseTransactions?.filteredTotalAmount)
        assertEquals(285_000L, vm.state.value.expenseTransactions?.monthTotalAmount)

        vm.clearDateFilter()
        assertNull(vm.state.value.selectedDate)
        assertNull(fakeRepo.lastTransactionsDate)
        assertEquals(285_000L, vm.state.value.expenseTransactions?.filteredTotalAmount)
    }

    @Test
    fun networkFailure_exposesReadableErrorMessagesWithoutCrashing() = runTest {
        fakeRepo.shouldFail = true
        val vm = ReportViewModel(
            reportRepository = fakeRepo,
            initialYearMonth = YearMonth.of(2026, 8),
            currentYearMonthProvider = { YearMonth.of(2026, 8) }
        )

        val state = vm.state.value
        assertFalse(state.isTrendLoading)
        assertFalse(state.isExpenseTrendLoading)
        assertFalse(state.isCashflowIncomeLoading)
        assertFalse(state.isCashflowLoading)
        assertFalse(state.isTransactionsLoading)
        assertNotNull(state.trendError)
        assertNotNull(state.expenseTrendError)
        assertNotNull(state.cashflowIncomeError)
        assertNotNull(state.cashflowError)
        assertNotNull(state.transactionsError)
        assertTrue(state.trendError!!.contains("Không kết nối được"))
    }

    private data class TrendCall(
        val period: String,
        val months: Int,
        val type: String,
        val categoryId: String?
    )

    private class FakeReportRepository : ReportRepository() {
        var shouldFail = false
        val trendCalls = mutableListOf<TrendCall>()
        var lastTrendPeriod: String? = null
        var lastTrendCategoryId: String? = null
        var lastTransactionsPeriod: String? = null
        var lastTransactionsDate: LocalDate? = null
        var lastCashflowSummaryPeriod: String? = null

        override suspend fun getMonthlyTrend(
            period: String,
            months: Int,
            type: String,
            categoryId: String?
        ): ReportResult<MonthlyTrendData> {
            trendCalls.add(TrendCall(period, months, type, categoryId))
            lastTrendPeriod = period
            lastTrendCategoryId = categoryId
            if (shouldFail) {
                return ReportResult.Failure("Không kết nối được máy chủ.", "NETWORK_ERROR")
            }
            val selectedAmount = when {
                type == "EXPENSE" -> 9_712_000L
                categoryId != null -> 18_000_000L
                else -> 18_200_000L
            }
            val previousAmount = if (type == "EXPENSE") 10_127_000L else 15_000_000L
            val momPercent = if (type == "EXPENSE") -4.1 else 21.3
            return ReportResult.Success(
                MonthlyTrendData(
                    period = period,
                    type = type,
                    categoryId = categoryId,
                    currencyCode = "VND",
                    selectedAmount = selectedAmount,
                    previousAmount = previousAmount,
                    momChangeAmount = selectedAmount - previousAmount,
                    momChangePercent = momPercent,
                    points = listOf(
                        MonthlyBarPoint("2026-04", 2026, 4, 14_000_000L, 1, false),
                        MonthlyBarPoint("2026-05", 2026, 5, 15_000_000L, 1, false),
                        MonthlyBarPoint("2026-06", 2026, 6, 15_000_000L, 1, false),
                        MonthlyBarPoint("2026-07", 2026, 7, previousAmount, 1, false),
                        MonthlyBarPoint(period, 2026, 8, selectedAmount, 2, true)
                    ),
                    categoryBreakdown = listOf(
                        CategoryBreakdownItem(
                            categoryId = "22222222-0000-4000-8000-000000000004",
                            categoryName = "Đầu tư",
                            categoryIcon = "TrendingUp",
                            categoryColor = "#16A34A",
                            totalAmount = 200_000L,
                            txnCount = 1,
                            sharePercent = 1.1,
                            momChangePercent = null
                        )
                    )
                )
            )
        }

        override suspend fun getTransactions(
            period: String,
            date: LocalDate?,
            type: String?,
            categoryId: String?
        ): ReportResult<MonthlyTransactionsData> {
            lastTransactionsPeriod = period
            lastTransactionsDate = date
            if (shouldFail) {
                return ReportResult.Failure("Không kết nối được máy chủ.", "NETWORK_ERROR")
            }
            val day22 = DailyTransactionGroup(
                date = LocalDate.of(2026, 8, 22),
                totalAmount = 72_000L,
                netAmount = -72_000L,
                txnCount = 1,
                items = listOf(
                    ReportTransactionItem(
                        transactionId = "txn-1",
                        type = "EXPENSE",
                        amount = 72_000L,
                        signedAmount = -72_000L,
                        currencyCode = "VND",
                        occurredAt = Instant.parse("2026-08-22T02:12:00Z"),
                        occurredDate = LocalDate.of(2026, 8, 22),
                        categoryId = "11111111-0000-4000-8000-000000000001",
                        categoryName = "Ăn uống",
                        categoryIcon = "Restaurant",
                        categoryColor = "#EF4444",
                        walletId = "w-1",
                        walletName = "Ví Momo",
                        merchant = "Highlands Coffee",
                        note = null
                    )
                )
            )
            val filtered = if (date != null) 72_000L else 285_000L
            return ReportResult.Success(
                MonthlyTransactionsData(
                    period = period,
                    selectedDate = date,
                    currencyCode = "VND",
                    monthTotalAmount = 285_000L,
                    monthTxnCount = 3,
                    filteredTotalAmount = filtered,
                    filteredTxnCount = if (date != null) 1 else 3,
                    days = listOf(day22)
                )
            )
        }

        override suspend fun getCashflowSummary(period: String): ReportResult<CashflowSummaryData> {
            lastCashflowSummaryPeriod = period
            if (shouldFail) {
                return ReportResult.Failure("Không kết nối được máy chủ.", "NETWORK_ERROR")
            }
            return ReportResult.Success(
                CashflowSummaryData(
                    period = period,
                    currencyCode = "VND",
                    openingBalance = 11_510_000L,
                    totalIncome = 18_200_000L,
                    totalExpense = 9_712_000L,
                    closingBalance = 19_998_000L,
                    wallets = listOf(
                        WalletCashflowItem(
                            walletId = "w-1",
                            walletName = "Vietcombank",
                            walletKind = "BANK",
                            color = "#0E9F6E",
                            accountMask = null,
                            openingBalance = 10_000_000L,
                            totalIncome = 18_200_000L,
                            totalExpense = 10_300_000L,
                            netFlow = 7_900_000L,
                            closingBalance = 17_900_000L,
                            includeInTotal = true
                        )
                    )
                )
            )
        }
    }
}
