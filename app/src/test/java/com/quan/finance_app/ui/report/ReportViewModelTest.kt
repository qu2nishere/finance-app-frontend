package com.quan.finance_app.ui.report

import com.quan.finance_app.data.CategoryBreakdownItem
import com.quan.finance_app.data.DailyTransactionGroup
import com.quan.finance_app.data.MonthlyBarPoint
import com.quan.finance_app.data.MonthlyTransactionsData
import com.quan.finance_app.data.MonthlyTrendData
import com.quan.finance_app.data.ReportRepository
import com.quan.finance_app.data.ReportResult
import com.quan.finance_app.data.ReportTransactionItem
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
    fun init_loadsIncomeTrendAndExpenseTransactions() = runTest {
        val vm = ReportViewModel(
            reportRepository = fakeRepo,
            initialYearMonth = YearMonth.of(2026, 8)
        )

        val state = vm.state.value
        assertEquals("2026-08", state.periodKey)
        assertFalse(state.isTrendLoading)
        assertFalse(state.isTransactionsLoading)
        assertNull(state.trendError)
        assertNull(state.transactionsError)
        assertEquals(18_200_000L, state.incomeTrend?.selectedAmount)
        assertEquals(5, state.incomeTrend?.points?.size)
        assertEquals(285_000L, state.expenseTransactions?.monthTotalAmount)
        assertTrue(state.incomeFilterOptions.any { it.label == "Đầu tư" })
    }

    @Test
    fun goPreviousAndNextMonth_updatesPeriodResetsDateAndReloads() = runTest {
        val vm = ReportViewModel(
            reportRepository = fakeRepo,
            initialYearMonth = YearMonth.of(2026, 8)
        )

        vm.selectDate(LocalDate.of(2026, 8, 22))
        assertEquals(LocalDate.of(2026, 8, 22), vm.state.value.selectedDate)

        vm.goPreviousMonth()
        assertEquals("2026-07", vm.state.value.periodKey)
        assertNull(vm.state.value.selectedDate)
        assertEquals("2026-07", fakeRepo.lastTrendPeriod)
        assertEquals("2026-07", fakeRepo.lastTransactionsPeriod)

        vm.goNextMonth()
        assertEquals("2026-08", vm.state.value.periodKey)
    }

    @Test
    fun selectIncomeCategory_callsTrendApiWithCategoryId() = runTest {
        val vm = ReportViewModel(
            reportRepository = fakeRepo,
            initialYearMonth = YearMonth.of(2026, 8)
        )

        val salaryId = "22222222-0000-4000-8000-000000000001"
        vm.selectIncomeCategory(salaryId, "Lương")

        assertEquals(salaryId, vm.state.value.selectedIncomeCategoryId)
        assertEquals("Lương", vm.state.value.selectedIncomeCategoryLabel)
        assertEquals(salaryId, fakeRepo.lastTrendCategoryId)
        assertEquals(18_000_000L, vm.state.value.incomeTrend?.selectedAmount)
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
            initialYearMonth = YearMonth.of(2026, 8)
        )

        val state = vm.state.value
        assertFalse(state.isTrendLoading)
        assertFalse(state.isTransactionsLoading)
        assertNotNull(state.trendError)
        assertNotNull(state.transactionsError)
        assertTrue(state.trendError!!.contains("Không kết nối được"))
    }

    private class FakeReportRepository : ReportRepository() {
        var shouldFail = false
        var lastTrendPeriod: String? = null
        var lastTrendCategoryId: String? = null
        var lastTransactionsPeriod: String? = null
        var lastTransactionsDate: LocalDate? = null

        override suspend fun getMonthlyTrend(
            period: String,
            months: Int,
            type: String,
            categoryId: String?
        ): ReportResult<MonthlyTrendData> {
            lastTrendPeriod = period
            lastTrendCategoryId = categoryId
            if (shouldFail) {
                return ReportResult.Failure("Không kết nối được máy chủ.", "NETWORK_ERROR")
            }
            val selectedAmount = if (categoryId != null) 18_000_000L else 18_200_000L
            return ReportResult.Success(
                MonthlyTrendData(
                    period = period,
                    type = type,
                    categoryId = categoryId,
                    currencyCode = "VND",
                    selectedAmount = selectedAmount,
                    previousAmount = 15_000_000L,
                    momChangeAmount = selectedAmount - 15_000_000L,
                    momChangePercent = 21.3,
                    points = listOf(
                        MonthlyBarPoint("2026-04", 2026, 4, 14_000_000L, 1, false),
                        MonthlyBarPoint("2026-05", 2026, 5, 15_000_000L, 1, false),
                        MonthlyBarPoint("2026-06", 2026, 6, 15_000_000L, 1, false),
                        MonthlyBarPoint("2026-07", 2026, 7, 15_000_000L, 1, false),
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
    }
}
