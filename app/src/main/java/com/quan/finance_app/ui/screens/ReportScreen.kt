package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.CallMade
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.data.CashflowSummaryData
import com.quan.finance_app.data.CategoryBreakdownItem
import com.quan.finance_app.data.MonthlyBarPoint
import com.quan.finance_app.data.MonthlyTransactionsData
import com.quan.finance_app.data.MonthlyTrendData
import com.quan.finance_app.data.ReportTransactionItem
import com.quan.finance_app.data.WalletCashflowItem
import com.quan.finance_app.ui.components.core.MfTabHeader
import com.quan.finance_app.ui.report.DualBarData
import com.quan.finance_app.ui.report.IncomeFilterOption
import com.quan.finance_app.ui.report.ReportUiState
import com.quan.finance_app.ui.report.ReportViewModel
import com.quan.finance_app.ui.report.averagePerDay
import com.quan.finance_app.ui.report.buildFiveMonthBarTriples
import com.quan.finance_app.ui.report.buildFiveMonthDualBars
import com.quan.finance_app.ui.report.buildWalletBars
import com.quan.finance_app.ui.report.forecastMonthEnd
import com.quan.finance_app.ui.report.netMomPercent
import com.quan.finance_app.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val REPORT_DAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")
private val REPORT_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val REPORT_ZONE: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportRoute(
    reportViewModel: ReportViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by reportViewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        reportViewModel.refresh()
    }
    PullToRefreshBox(
        isRefreshing = state.isTransactionsLoading || state.isTrendLoading || state.isCashflowLoading,
        onRefresh = reportViewModel::refresh,
        modifier = modifier.fillMaxSize()
    ) {
        ReportScreen(
            state = state,
            onSelectTab = reportViewModel::selectTab,
            onPreviousMonth = reportViewModel::goPreviousMonth,
            onNextMonth = reportViewModel::goNextMonth,
            onSelectIncomeCategory = reportViewModel::selectIncomeCategory,
            onPickDate = reportViewModel::selectDate,
            onClearDate = reportViewModel::clearDateFilter,
            onRefresh = reportViewModel::refresh
        )
    }
}

@Composable
fun ReportScreen(
    state: ReportUiState = ReportUiState(),
    onSelectTab: (Int) -> Unit = {},
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    onSelectIncomeCategory: (String?, String) -> Unit = { _, _ -> },
    onPickDate: (LocalDate) -> Unit = {},
    onClearDate: () -> Unit = {},
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val selectedTab = state.selectedTab
    val currentMonth = state.month
    val currentYear = state.year
    val tabs = listOf("Chi tiêu", "Thu nhập", "Dòng tiền")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Ink50),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Month Selector Header
        item {
            MonthSelectorHeader(
                month = currentMonth,
                year = currentYear,
                canGoNext = state.canGoNext,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )
        }

        // 2. Custom Tabs (Chi tiêu: Cam, Thu nhập: Xanh lá, Dòng tiền: Xanh lam)
        item {
            ReportTabs(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = onSelectTab
            )
        }

        // 3. Tab Contents
        when (selectedTab) {
            // TAB 1: CHI TIÊU
            0 -> {
                item {
                    ExpenseOverviewCard(
                        trend = state.expenseTrend,
                        year = currentYear,
                        month = currentMonth,
                        isLoading = state.isExpenseTrendLoading,
                        errorMessage = state.expenseTrendError,
                        onRetry = onRefresh
                    )
                }

                // Danh sách toàn bộ giao dịch đã thực hiện trong tháng kèm bộ lọc ngày
                item {
                    Text(
                        text = "Giao dịch trong Tháng $currentMonth, $currentYear",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoneyFlowTheme.colors.textPrimary,
                        fontFamily = FontCore,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    MonthTransactionsLedgerCard(
                        month = currentMonth,
                        year = currentYear,
                        selectedDate = state.selectedDate,
                        transactionsData = state.expenseTransactions,
                        isLoading = state.isTransactionsLoading,
                        errorMessage = state.transactionsError,
                        onPickDate = onPickDate,
                        onClearDate = onClearDate,
                        onRetry = onRefresh
                    )
                }

                item {
                    InsightBanner(
                        icon = Icons.Outlined.Info,
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Ink700, fontFamily = FontCore)) {
                                append("Chỗ tiêu nhiều nhất tháng này là ")
                            }
                            withStyle(
                                SpanStyle(
                                    color = Ink900,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontCore
                                )
                            ) {
                                append("cà phê (18 lần, 1,1tr)")
                            }
                            withStyle(SpanStyle(color = Ink700, fontFamily = FontCore)) {
                                append(" – đặt ngân sách?")
                            }
                        }
                    )
                }
            }

            // TAB 2: THU NHẬP
            1 -> {
                item {
                    IncomeOverviewCard(
                        year = currentYear,
                        month = currentMonth,
                        selectedCategoryId = state.selectedIncomeCategoryId,
                        selectedCategoryLabel = state.selectedIncomeCategoryLabel,
                        filterOptions = state.incomeFilterOptions,
                        trendData = state.incomeTrend,
                        isLoading = state.isTrendLoading,
                        errorMessage = state.trendError,
                        onSelectCategory = onSelectIncomeCategory,
                        onRetry = onRefresh
                    )
                }

                item {
                    Text(
                        text = "Nguồn thu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoneyFlowTheme.colors.textPrimary,
                        fontFamily = FontCore,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    IncomeSourceCard(
                        month = currentMonth,
                        year = currentYear,
                        breakdown = state.categoryBreakdown,
                        isLoading = state.isTrendLoading
                    )
                }

                item {
                    SavingsRateCard()
                }
            }

            // TAB 3: DÒNG TIỀN
            2 -> {
                item {
                    CashflowOverviewCard(
                        incomeTrend = state.cashflowIncomeTrend,
                        expenseTrend = state.expenseTrend,
                        year = currentYear,
                        month = currentMonth,
                        isLoading = state.isCashflowIncomeLoading || state.isExpenseTrendLoading,
                        errorMessage = state.cashflowIncomeError ?: state.expenseTrendError,
                        onRetry = onRefresh
                    )
                }

                item {
                    CashflowPeriodSummaryCard(
                        summary = state.cashflowSummary,
                        isLoading = state.isCashflowLoading,
                        errorMessage = state.cashflowError,
                        onRetry = onRefresh
                    )
                }

                item {
                    Text(
                        text = "Theo ví",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoneyFlowTheme.colors.textPrimary,
                        fontFamily = FontCore,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    WalletBreakdownCard(
                        wallets = state.cashflowSummary?.wallets ?: emptyList(),
                        isLoading = state.isCashflowLoading && state.cashflowSummary == null
                    )
                }

                item {
                    InsightBanner(
                        icon = Icons.Outlined.FlashOn,
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Ink700, fontFamily = FontCore)) {
                                append("Giữ nhịp này, cuối năm bạn dư ")
                            }
                            withStyle(
                                SpanStyle(
                                    color = Ink900,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontCore
                                )
                            ) {
                                append("~68tr")
                            }
                            withStyle(SpanStyle(color = Ink700, fontFamily = FontCore)) {
                                append(". Chuyển ")
                            }
                            withStyle(
                                SpanStyle(
                                    color = Ink900,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontCore
                                )
                            ) {
                                append("5tr/tháng")
                            }
                            withStyle(SpanStyle(color = Ink700, fontFamily = FontCore)) {
                                append(" vào quỹ dự phòng?")
                            }
                        }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 1. Header & Navigation Components
// -------------------------------------------------------------------------

@Composable
fun MonthSelectorHeader(
    month: Int,
    year: Int,
    canGoNext: Boolean = true,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    MfTabHeader(title = "Thống kê") {
        MonthStepper(
            month = month,
            year = year,
            canGoNext = canGoNext,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth
        )
    }
}

@Composable
fun MonthStepper(
    month: Int,
    year: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
    canGoNext: Boolean = true,
    fillWidth: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (fillWidth) Arrangement.SpaceBetween else Arrangement.Start
    ) {
        IconButton(onClick = onPreviousMonth, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "Tháng trước",
                tint = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = "Tháng $month, $year",
            fontFamily = FontCore,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MoneyFlowTheme.colors.textPrimary
        )
        IconButton(
            onClick = onNextMonth,
            enabled = canGoNext,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Tháng sau",
                tint = if (canGoNext) {
                    MoneyFlowTheme.colors.textSecondary
                } else {
                    MoneyFlowTheme.colors.textSecondary.copy(alpha = 0.3f)
                },
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// -------------------------------------------------------------------------
// 2. Tabs
// -------------------------------------------------------------------------

@Composable
fun ReportTabs(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            // Chi tiêu: Cam (Heat500), Thu nhập: Xanh lá (Grow500), Dòng tiền: Xanh lam (Blue500)
            val activeColor = when (index) {
                0 -> Heat500
                1 -> Grow500
                else -> Blue500
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(index) }
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = title,
                    fontFamily = FontCore,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 15.sp,
                    color = if (isSelected) activeColor else MoneyFlowTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .height(3.dp)
                            .background(activeColor, RoundedCornerShape(2.dp))
                    )
                } else {
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 2. Spending (Chi tiêu) Components
// -------------------------------------------------------------------------

@Composable
fun ExpenseOverviewCard(
    trend: MonthlyTrendData?,
    year: Int,
    month: Int,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    val selectedAmount = trend?.selectedAmount ?: 0L
    val momChangePercent = trend?.momChangePercent
    val today = remember { LocalDate.now(REPORT_ZONE) }
    val selectedYm = remember(year, month) { YearMonth.of(year, month) }
    val avgPerDay = remember(selectedAmount, selectedYm, today) {
        averagePerDay(selectedAmount, selectedYm, today)
    }
    val forecast = remember(selectedAmount, selectedYm, today) {
        forecastMonthEnd(selectedAmount, selectedYm, today)
    }
    val barData = remember(trend, year, month) {
        buildFiveMonthBarTriples(trend?.points, year, month)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            if (errorMessage != null) {
                ReportInlineError(message = errorMessage, onRetry = onRetry)
                Spacer(modifier = Modifier.height(14.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tổng chi tháng $month",
                            fontFamily = FontCore,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                        if (isLoading && trend == null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Đang tải…",
                                fontFamily = FontCore,
                                fontSize = 12.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatPlainVndNumber(selectedAmount),
                            fontFamily = FontMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Heat500,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "₫",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Heat500,
                            modifier = Modifier.padding(bottom = 3.dp, start = 1.dp)
                        )
                    }
                }

                MomComparisonBadge(
                    momChangePercent = momChangePercent,
                    selectedAmount = selectedAmount,
                    higherIsBetter = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5-Month Expense Bar Chart with perfectly horizontal aligned baseline
            FiveMonthBarChart(
                data = barData,
                activeColor = Heat500,
                inactiveColor = Heat100
            )

            Spacer(modifier = Modifier.height(20.dp))
            DashedDivider(color = Ink200)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                        append("Trung bình 1 ngày: ")
                    }
                    withStyle(SpanStyle(color = Ink900, fontFamily = FontMono, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                        append(formatVnd(avgPerDay))
                    }
                    if (forecast != null) {
                        withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                            append(" · Dự báo cuối tháng: ")
                        }
                        withStyle(SpanStyle(color = Ink900, fontFamily = FontMono, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                            append(formatVnd(forecast))
                        }
                    }
                }
            )
        }
    }
}

// -------------------------------------------------------------------------
// 2b. Danh sách toàn bộ giao dịch đã thực hiện trong tháng & Lọc theo ngày
// -------------------------------------------------------------------------

@Composable
fun MonthTransactionsLedgerCard(
    month: Int,
    year: Int,
    selectedDate: LocalDate?,
    transactionsData: MonthlyTransactionsData?,
    isLoading: Boolean,
    errorMessage: String?,
    onPickDate: (LocalDate) -> Unit,
    onClearDate: () -> Unit,
    onRetry: () -> Unit
) {
    val today = remember { LocalDate.now(REPORT_ZONE) }
    val yesterday = remember(today) { today.minusDays(1) }

    val monthGroups = transactionsData?.days ?: emptyList()
    val totalMonthSpent = transactionsData?.monthTotalAmount ?: 0L
    val totalTxnCount = transactionsData?.monthTxnCount ?: 0

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Heat50, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Receipt,
                                contentDescription = "Giao dịch",
                                tint = Heat500,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Giao dịch Tháng $month/$year",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tổng chi: ${formatVnd(totalMonthSpent)} · $totalTxnCount giao dịch",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }

                if (selectedDate != null) {
                    Text(
                        text = "Xem tất cả",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Blue500,
                        modifier = Modifier
                            .clickable(onClick = onClearDate)
                            .padding(4.dp)
                    )
                }
            }

            // Bộ lọc theo ngày (date picker) trong tháng đã chọn, kèm nút xem cả tháng
            var showDatePicker by remember { mutableStateOf(false) }
            val pickedLabel = if (selectedDate == null) {
                "Chọn ngày"
            } else {
                "Ngày ${selectedDate.format(REPORT_DAY_FORMATTER)}"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedDate == null) Ink100 else Heat500,
                    border = if (selectedDate == null) BorderStroke(1.dp, Ink200) else null,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val fg = if (selectedDate == null) MoneyFlowTheme.colors.textSecondary else Color.White
                        Icon(
                            Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = fg,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pickedLabel,
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = fg
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedDate == null) Heat500 else Heat50,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onClearDate)
                ) {
                    Text(
                        text = "Xem cả tháng",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = if (selectedDate == null) Color.White else Heat700,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }
            if (showDatePicker) {
                com.quan.finance_app.ui.components.forms.DateTimePickerSheet(
                    onDismissRequest = { showDatePicker = false },
                    onDateSelected = { date ->
                        showDatePicker = false
                        onPickDate(date)
                    },
                    brandColor = Heat500,
                    brandSoftColor = Heat50,
                    initialDate = selectedDate ?: LocalDate.of(year, month, 1)
                )
            }

            HorizontalDivider(color = Ink100)

            when {
                isLoading && transactionsData == null -> {
                    Text(
                        text = "Đang tải danh sách giao dịch…",
                        fontFamily = FontCore,
                        fontSize = 13.sp,
                        color = MoneyFlowTheme.colors.textSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                errorMessage != null -> {
                    ReportInlineError(
                        message = errorMessage,
                        onRetry = onRetry
                    )
                }

                selectedDate == null -> {
                    if (monthGroups.isEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Chưa có giao dịch chi tiêu nào trong tháng $month/$year.",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                            Text(
                                text = "Bấm nút + để ghi khoản chi đầu tiên của bạn.",
                                fontFamily = FontCore,
                                fontSize = 12.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            monthGroups.forEachIndexed { groupIndex, dayGroup ->
                                val dayLabel = formatDayHeaderLabel(dayGroup.date, today, yesterday)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Ink50, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = dayLabel,
                                                fontFamily = FontCore,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MoneyFlowTheme.colors.textPrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${dayGroup.txnCount} GD)",
                                                fontFamily = FontCore,
                                                fontSize = 11.sp,
                                                color = MoneyFlowTheme.colors.textSecondary
                                            )
                                        }
                                        Text(
                                            text = "−${formatVnd(dayGroup.totalAmount)}",
                                            fontFamily = FontMono,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Heat500
                                        )
                                    }

                                    dayGroup.items.forEach { txn ->
                                        TransactionLedgerItemRow(txn = txn)
                                    }
                                }

                                if (groupIndex < monthGroups.size - 1) {
                                    DashedDivider(color = Ink100)
                                }
                            }
                        }
                    }
                }

                else -> {
                    val selectedGroup = monthGroups.firstOrNull { it.date == selectedDate }
                    val dayLabel = formatDayHeaderLabel(selectedDate, today, yesterday)
                    if (selectedGroup == null || selectedGroup.items.isEmpty()) {
                        Text(
                            text = "Không có giao dịch nào trong ngày ${selectedDate.format(REPORT_DAY_FORMATTER)}.",
                            fontFamily = FontCore,
                            fontSize = 13.sp,
                            color = MoneyFlowTheme.colors.textSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Heat50, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Ngày: $dayLabel",
                                        fontFamily = FontCore,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Heat700
                                    )
                                    Text(
                                        text = "${selectedGroup.txnCount} giao dịch đã thực hiện",
                                        fontFamily = FontCore,
                                        fontSize = 11.sp,
                                        color = Heat600
                                    )
                                }
                                Text(
                                    text = "−${formatVnd(selectedGroup.totalAmount)}",
                                    fontFamily = FontMono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Heat500
                                )
                            }

                            selectedGroup.items.forEach { txn ->
                                TransactionLedgerItemRow(txn = txn)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionLedgerItemRow(txn: ReportTransactionItem) {
    val isIncome = txn.type == "INCOME"
    val title = txn.merchant?.takeIf { it.isNotBlank() }
        ?: txn.note?.takeIf { it.isNotBlank() }
        ?: txn.categoryName
    val timeText = remember(txn.occurredAt) {
        txn.occurredAt.atZone(REPORT_ZONE).format(REPORT_TIME_FORMATTER)
    }
    val icon = remember(txn.categoryName) {
        getCategoryIcon(txn.categoryName)
    }
    val tint = if (isIncome) Grow500 else Heat500
    val tileBg = if (isIncome) Grow50 else Heat50
    val signPrefix = if (isIncome) "+" else "−"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(tileBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontFamily = FontCore,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                Text(
                    text = "${txn.categoryName} · $timeText · ${txn.walletName}",
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }
        }

        Text(
            text = "$signPrefix${formatVnd(txn.amount)}",
            fontFamily = FontMono,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = tint
        )
    }
}

@Composable
fun ExpenseCategoryCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CategoryProgressItem(
                title = "Ăn uống",
                amount = "3.302.000₫",
                percent = "34%",
                progress = 0.68f,
                progressColor = Heat500,
                trendText = "12% vs T7",
                trendUp = true
            )

            CategoryProgressItem(
                title = "Di chuyển",
                amount = "2.040.000₫",
                percent = "21%",
                progress = 0.42f,
                progressColor = Heat500,
                trendText = "6% vs T7",
                trendUp = false
            )

            CategoryProgressItem(
                title = "Mua sắm",
                amount = "1.560.000₫",
                percent = "16%",
                progress = 0.32f,
                progressColor = Heat300
            )

            CategoryProgressItem(
                title = "Hoá đơn & nhà ở",
                amount = "1.410.000₫",
                percent = "15%",
                progress = 0.30f,
                progressColor = Heat200
            )
        }
    }
}

// -------------------------------------------------------------------------
// 3. Income (Thu nhập) Components
// -------------------------------------------------------------------------

@Composable
fun IncomeOverviewCard(
    year: Int,
    month: Int,
    selectedCategoryId: String?,
    selectedCategoryLabel: String,
    filterOptions: List<IncomeFilterOption>,
    trendData: MonthlyTrendData?,
    isLoading: Boolean,
    errorMessage: String?,
    onSelectCategory: (String?, String) -> Unit,
    onRetry: () -> Unit
) {
    val selectedAmount = trendData?.selectedAmount ?: 0L
    val previousAmount = trendData?.previousAmount ?: 0L
    val momChangeAmount = trendData?.momChangeAmount ?: 0L
    val momChangePercent = trendData?.momChangePercent

    val barData = remember(trendData, year, month) {
        buildFiveMonthBarTriples(trendData?.points, year, month)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 1. Tên loại giao dịch (Transaction Type Name) Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Grow50, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Grow500, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Loại giao dịch: Thu nhập (Tiền vào)",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Grow600
                    )
                }

                Text(
                    text = if (isLoading) "Đang cập nhật…" else "Dòng tiền vào",
                    fontFamily = FontCore,
                    fontSize = 12.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bộ chọn Tên loại giao dịch (Income types)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { option ->
                    val isSelected = selectedCategoryId == option.categoryId
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Grow500 else Ink100,
                        border = if (isSelected) null else BorderStroke(1.dp, Ink200),
                        modifier = Modifier.clickable {
                            onSelectCategory(option.categoryId, option.label)
                        }
                    ) {
                        Text(
                            text = option.label,
                            fontFamily = FontCore,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else MoneyFlowTheme.colors.textSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (errorMessage != null) {
                ReportInlineError(message = errorMessage, onRetry = onRetry)
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Phần hiển thị số tiền thu theo loại giao dịch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedCategoryId == null) {
                            "Tổng thu tháng $month"
                        } else {
                            "Thu nhập tháng $month: $selectedCategoryLabel"
                        },
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatPlainVndNumber(selectedAmount),
                            fontFamily = FontMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Grow500,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "₫",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Grow500,
                            modifier = Modifier.padding(bottom = 3.dp, start = 1.dp)
                        )
                    }
                }

                MomComparisonBadge(
                    momChangePercent = momChangePercent,
                    selectedAmount = selectedAmount
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5-Month Income Bar Chart with perfectly horizontal aligned baseline
            FiveMonthBarChart(
                data = barData,
                activeColor = Grow500,
                inactiveColor = Grow100
            )

            Spacer(modifier = Modifier.height(20.dp))
            DashedDivider(color = Ink200)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                        append("Tháng trước: ")
                    }
                    withStyle(SpanStyle(color = Ink900, fontFamily = FontMono, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                        append(formatVnd(previousAmount))
                    }
                    withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                        append(" · Chênh lệch: ")
                    }
                    val diffPrefix = when {
                        momChangeAmount > 0L -> "+"
                        momChangeAmount < 0L -> "−"
                        else -> ""
                    }
                    withStyle(SpanStyle(color = Ink900, fontFamily = FontMono, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                        append("$diffPrefix${formatVnd(kotlin.math.abs(momChangeAmount))}")
                    }
                }
            )
        }
    }
}

@Composable
private fun MomComparisonBadge(
    momChangePercent: Double?,
    selectedAmount: Long,
    higherIsBetter: Boolean = true,
    showNewWhenNull: Boolean = true
) {
    val style = when {
        momChangePercent == null && selectedAmount != 0L && showNewWhenNull -> {
            if (higherIsBetter) {
                MomBadgeStyle(Grow50, Grow500, null, "Mới")
            } else {
                MomBadgeStyle(Heat50, Heat500, null, "Mới")
            }
        }
        momChangePercent == null -> return
        momChangePercent == 0.0 ->
            MomBadgeStyle(Ink100, MoneyFlowTheme.colors.textSecondary, null, "0%")
        momChangePercent > 0.0 -> {
            val isGood = higherIsBetter
            MomBadgeStyle(
                bg = if (isGood) Grow50 else Heat50,
                fg = if (isGood) Grow500 else Heat500,
                arrow = "▲",
                label = "${formatPercentVi(momChangePercent)}%"
            )
        }
        else -> {
            val isGood = !higherIsBetter
            MomBadgeStyle(
                bg = if (isGood) Grow50 else Heat50,
                fg = if (isGood) Grow500 else Heat500,
                arrow = "▼",
                label = "${formatPercentVi(kotlin.math.abs(momChangePercent))}%"
            )
        }
    }
    val (bgColor, fgColor, arrow, label) = style

    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (arrow != null) {
            Text(arrow, fontSize = 9.sp, color = fgColor, fontFamily = FontCore)
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = label,
            fontFamily = FontMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = fgColor
        )
    }
}

private data class MomBadgeStyle(
    val bg: Color,
    val fg: Color,
    val arrow: String?,
    val label: String
)

@Composable
fun IncomeSourceCard(
    month: Int,
    year: Int,
    breakdown: List<CategoryBreakdownItem>,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (breakdown.isEmpty()) {
                Text(
                    text = if (isLoading) {
                        "Đang tải cơ cấu nguồn thu…"
                    } else {
                        "Chưa có khoản thu nào trong tháng $month/$year."
                    },
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            } else {
                breakdown.forEachIndexed { index, item ->
                    val share = item.sharePercent ?: 0.0
                    val mom = item.momChangePercent
                    val trendText = buildString {
                        append("${item.txnCount} giao dịch")
                        if (mom != null) {
                            append(" · ${formatPercentVi(kotlin.math.abs(mom))}% so với tháng trước")
                        } else if (item.totalAmount > 0L) {
                            append(" · Mới trong tháng $month")
                        }
                    }
                    val trendUp = when {
                        mom == null || mom == 0.0 -> null
                        else -> mom > 0.0
                    }
                    CategoryProgressItem(
                        title = item.categoryName,
                        amount = formatVnd(item.totalAmount),
                        percent = "${formatPercentVi(share)}%",
                        progress = (share / 100.0).toFloat().coerceIn(0f, 1f),
                        progressColor = if (index == 0) Grow500 else Grow200,
                        trendText = trendText,
                        trendUp = trendUp
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportInlineError(
    message: String,
    onRetry: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Heat50, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            fontFamily = FontCore,
            fontSize = 12.sp,
            color = Heat700,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Thử lại",
            fontFamily = FontCore,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Heat600,
            modifier = Modifier
                .clickable(onClick = onRetry)
                .padding(4.dp)
        )
    }
}

private fun formatDayHeaderLabel(
    date: LocalDate,
    today: LocalDate,
    yesterday: LocalDate
): String {
    val ddMm = date.format(REPORT_DAY_FORMATTER)
    return when (date) {
        today -> "Hôm nay ($ddMm)"
        yesterday -> "Hôm qua ($ddMm)"
        else -> ddMm
    }
}

private fun formatPlainVndNumber(amount: Long): String {
    return kotlin.math.abs(amount).toString().reversed().chunked(3).joinToString(".").reversed()
}

private fun formatPercentVi(value: Double): String {
    val rounded = kotlin.math.round(value * 10.0) / 10.0
    return if (rounded % 1.0 == 0.0) {
        rounded.toLong().toString()
    } else {
        String.format(Locale.US, "%.1f", rounded).replace('.', ',')
    }
}

@Composable
fun SavingsRateCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tỷ lệ tiết kiệm",
                    fontFamily = FontCore,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                Text(
                    text = "46,6%",
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Grow500
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment progress bar: Đã chi (Orange 53.4%) | Còn lại (Green 46.6%)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.534f)
                        .background(Heat500)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.466f)
                        .background(Grow500)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Heat500, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Đã chi 9,7tr",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Grow500, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Còn lại 8,5tr",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 4. Cash Flow (Dòng tiền) Components
// -------------------------------------------------------------------------

@Composable
fun CashflowOverviewCard(
    incomeTrend: MonthlyTrendData?,
    expenseTrend: MonthlyTrendData?,
    year: Int,
    month: Int,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    val incomeAmount = incomeTrend?.selectedAmount ?: 0L
    val expenseAmount = expenseTrend?.selectedAmount ?: 0L
    val netAmount = incomeAmount - expenseAmount
    val netPrefix = when {
        netAmount > 0L -> "+"
        netAmount < 0L -> "−"
        else -> ""
    }
    val netColor = when {
        netAmount > 0L -> Grow500
        netAmount < 0L -> Heat500
        else -> MoneyFlowTheme.colors.textPrimary
    }
    val momPercent = remember(incomeTrend, expenseTrend) {
        netMomPercent(incomeTrend, expenseTrend)
    }
    val dualBars = remember(incomeTrend, expenseTrend, year, month) {
        buildFiveMonthDualBars(
            incomePoints = incomeTrend?.points,
            expensePoints = expenseTrend?.points,
            year = year,
            month = month
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            if (errorMessage != null) {
                ReportInlineError(message = errorMessage, onRetry = onRetry)
                Spacer(modifier = Modifier.height(14.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Dòng tiền ròng tháng $month",
                            fontFamily = FontCore,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                        if (isLoading && incomeTrend == null && expenseTrend == null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Đang tải…",
                                fontFamily = FontCore,
                                fontSize = 12.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$netPrefix${formatPlainVndNumber(netAmount)}",
                            fontFamily = FontMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = netColor,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "₫",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = netColor,
                            modifier = Modifier.padding(bottom = 3.dp, start = 1.dp)
                        )
                    }
                }

                MomComparisonBadge(
                    momChangePercent = momPercent,
                    selectedAmount = netAmount,
                    higherIsBetter = true,
                    showNewWhenNull = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dual Bar Chart (Vào vs Ra) with perfectly aligned bottom baseline
            DualMonthlyBarChart(data = dualBars)

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Grow500, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Vào",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Heat500, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ra",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun CashflowPeriodSummaryCard(
    summary: CashflowSummaryData?,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    val opening = summary?.openingBalance ?: 0L
    val totalIncome = summary?.totalIncome ?: 0L
    val totalExpense = summary?.totalExpense ?: 0L
    val closing = summary?.closingBalance ?: 0L

    val openingText = formatBalanceVnd(opening)
    val incomeText = if (totalIncome > 0L) "+${formatPlainVndNumber(totalIncome)}₫" else "0₫"
    val expenseText = if (totalExpense > 0L) "−${formatPlainVndNumber(totalExpense)}₫" else "0₫"
    val closingText = formatBalanceVnd(closing)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (errorMessage != null) {
                ReportInlineError(message = errorMessage, onRetry = onRetry)
            } else if (isLoading && summary == null) {
                Text(
                    text = "Đang tải tóm tắt kỳ…",
                    fontFamily = FontCore,
                    fontSize = 12.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }

            CashflowSummaryRow(
                icon = Icons.Outlined.AccountBalanceWallet,
                iconBg = Ink100,
                iconTint = Ink600,
                title = "Số dư đầu kỳ",
                amount = openingText,
                amountColor = MoneyFlowTheme.colors.textPrimary
            )

            HorizontalDivider(color = Ink100)

            CashflowSummaryRow(
                icon = Icons.AutoMirrored.Outlined.CallMade,
                iconBg = Grow50,
                iconTint = Grow500,
                title = "Tiền vào",
                amount = incomeText,
                amountColor = Grow500
            )

            HorizontalDivider(color = Ink100)

            CashflowSummaryRow(
                icon = Icons.AutoMirrored.Outlined.ArrowForward,
                iconBg = Blue50,
                iconTint = Blue500,
                title = "Tiền ra",
                amount = expenseText,
                amountColor = Heat500
            )

            HorizontalDivider(color = Ink100)

            CashflowSummaryRow(
                icon = Icons.Outlined.CheckCircleOutline,
                iconBg = Grow50,
                iconTint = Grow500,
                title = "Số dư cuối kỳ",
                amount = closingText,
                amountColor = MoneyFlowTheme.colors.textPrimary
            )
        }
    }
}

private fun formatBalanceVnd(amount: Long): String {
    val prefix = if (amount < 0L) "−" else ""
    return "$prefix${formatPlainVndNumber(amount)}₫"
}

@Composable
fun CashflowSummaryRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    amount: String,
    amountColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontFamily = FontCore,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
        }

        Text(
            text = amount,
            fontFamily = FontMono,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = amountColor
        )
    }
}

@Composable
fun WalletBreakdownCard(
    wallets: List<WalletCashflowItem>,
    isLoading: Boolean
) {
    val bars = remember(wallets) { buildWalletBars(wallets) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when {
                isLoading -> {
                    Text(
                        text = "Đang tải dữ liệu ví…",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
                bars.isEmpty() -> {
                    Text(
                        text = "Chưa có ví nào",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
                else -> {
                    bars.forEach { bar ->
                        val amountText = when {
                            bar.netFlow > 0L -> "+${formatPlainVndNumber(bar.netFlow)}₫"
                            bar.netFlow < 0L -> "−${formatPlainVndNumber(bar.netFlow)}₫"
                            else -> "0₫"
                        }
                        val amountColor = when {
                            bar.isZero -> MoneyFlowTheme.colors.textSecondary
                            bar.isPositive -> Grow500
                            else -> Heat500
                        }
                        val progressColor = when {
                            bar.isZero -> Ink200
                            bar.isPositive -> Grow500
                            else -> Heat500
                        }
                        WalletProgressItem(
                            title = bar.title,
                            amount = amountText,
                            amountColor = amountColor,
                            progress = bar.progress,
                            progressColor = progressColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WalletProgressItem(
    title: String,
    amount: String,
    amountColor: Color,
    progress: Float,
    progressColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = FontCore,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = amount,
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = amountColor
                )
                Text(
                    text = " · ",
                    fontFamily = FontCore,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                Text(
                    text = "ròng",
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Ink100, RoundedCornerShape(4.dp))
        ) {
            if (progress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(progressColor, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// 5. Shared Design Components (Fixed Baseline Alignment)
// -------------------------------------------------------------------------

@Composable
fun FiveMonthBarChart(
    data: List<Triple<String, Float, Boolean>>,
    activeColor: Color,
    inactiveColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row chứa toàn bộ các cột - căn đáy chuẩn 100% bằng verticalAlignment = Alignment.Bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (_, fraction, isCurrent) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction.coerceIn(0.08f, 1f))
                        .background(
                            color = if (isCurrent) activeColor else inactiveColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row chứa toàn bộ nhãn tháng - nằm trên một hàng ngang cố định
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            data.forEach { (label, _, isCurrent) ->
                Text(
                    text = label,
                    fontFamily = FontCore,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp,
                    color = if (isCurrent) Ink900 else Ink500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DualMonthlyBarChart(
    data: List<DualBarData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row chứa các cặp cột - căn đáy chuẩn 100% bằng verticalAlignment = Alignment.Bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { item ->
                val maxFraction = maxOf(item.inFraction, item.outFraction).coerceIn(0.08f, 1f)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(maxFraction),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Cột Vào (Green)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(if (maxFraction > 0f) item.inFraction / maxFraction else 0f)
                            .background(
                                color = if (item.isCurrent) Grow500 else Grow100,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    // Cột Ra (Orange)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(if (maxFraction > 0f) item.outFraction / maxFraction else 0f)
                            .background(
                                color = if (item.isCurrent) Heat500 else Heat100,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row chứa toàn bộ nhãn tháng - nằm trên một hàng ngang cố định
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            data.forEach { item ->
                Text(
                    text = item.label,
                    fontFamily = FontCore,
                    fontWeight = if (item.isCurrent) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp,
                    color = if (item.isCurrent) Ink900 else Ink500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CategoryProgressItem(
    title: String,
    amount: String,
    percent: String,
    progress: Float,
    progressColor: Color,
    trendText: String? = null,
    trendUp: Boolean? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = FontCore,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = amount,
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                Text(
                    text = " · ",
                    fontFamily = FontCore,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                Text(
                    text = percent,
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Ink100, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(progressColor, RoundedCornerShape(4.dp))
            )
        }

        if (trendText != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (trendUp != null) {
                    Text(
                        text = if (trendUp) "▲ " else "▼ ",
                        fontSize = 9.sp,
                        color = MoneyFlowTheme.colors.textSecondary,
                        fontFamily = FontCore
                    )
                }
                Text(
                    text = trendText,
                    fontSize = 11.sp,
                    color = MoneyFlowTheme.colors.textSecondary,
                    fontFamily = FontCore
                )
            }
        }
    }
}

@Composable
fun DashedDivider(
    color: Color = Ink200,
    thickness: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        val pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            0f
        )
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = thickness.toPx(),
            pathEffect = pathEffect
        )
    }
}

@Composable
fun InsightBanner(
    icon: ImageVector,
    text: AnnotatedString,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Sun50,
        border = BorderStroke(1.dp, Sun100)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Gợi ý thông minh",
                tint = Sun600,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MoneyFlowTheme.typography.bodySm.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}
