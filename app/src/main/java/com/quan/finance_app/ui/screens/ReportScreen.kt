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
import com.quan.finance_app.data.CategoryBreakdownItem
import com.quan.finance_app.data.MonthlyBarPoint
import com.quan.finance_app.data.MonthlyTransactionsData
import com.quan.finance_app.data.MonthlyTrendData
import com.quan.finance_app.data.ReportTransactionItem
import com.quan.finance_app.ui.components.core.MfTabHeader
import com.quan.finance_app.ui.report.IncomeFilterOption
import com.quan.finance_app.ui.report.ReportUiState
import com.quan.finance_app.ui.report.ReportViewModel
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

data class DualBarData(
    val label: String,
    val inFraction: Float,
    val outFraction: Float,
    val isCurrent: Boolean
)

@Composable
fun ReportRoute(
    reportViewModel: ReportViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by reportViewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        reportViewModel.refresh()
    }
    ReportScreen(
        state = state,
        onSelectTab = reportViewModel::selectTab,
        onPreviousMonth = reportViewModel::goPreviousMonth,
        onNextMonth = reportViewModel::goNextMonth,
        onSelectIncomeCategory = reportViewModel::selectIncomeCategory,
        onPickDate = reportViewModel::selectDate,
        onClearDate = reportViewModel::clearDateFilter,
        onRefresh = reportViewModel::refresh,
        modifier = modifier
    )
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
                    ExpenseOverviewCard()
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
                    CashflowOverviewCard()
                }

                item {
                    CashflowPeriodSummaryCard()
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
                    WalletBreakdownCard()
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
fun ExpenseOverviewCard() {
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
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Tổng chi tháng này",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "9.712.000",
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

                Row(
                    modifier = Modifier
                        .background(Grow50, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "▼",
                        fontSize = 9.sp,
                        color = Grow500,
                        fontFamily = FontCore
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4,1%",
                        fontFamily = FontMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Grow500
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5-Month Expense Bar Chart with perfectly horizontal aligned baseline
            FiveMonthBarChart(
                data = listOf(
                    Triple("T4", 0.48f, false),
                    Triple("T5", 0.36f, false),
                    Triple("T6", 0.68f, false),
                    Triple("T7", 0.82f, false),
                    Triple("T8", 0.74f, true)
                ),
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
                        append("313.000₫")
                    }
                    withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                        append(" · Dự báo cuối tháng: ")
                    }
                    withStyle(SpanStyle(color = Ink900, fontFamily = FontCore, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                        append("10,4tr")
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
                            "Tổng thu tháng này"
                        } else {
                            "Thu nhập: $selectedCategoryLabel"
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
    selectedAmount: Long
) {
    val (bgColor, fgColor, arrow, label) = when {
        momChangePercent == null && selectedAmount > 0L ->
            MomBadgeStyle(Grow50, Grow500, null, "Mới")
        momChangePercent == null || momChangePercent == 0.0 ->
            MomBadgeStyle(Ink100, MoneyFlowTheme.colors.textSecondary, null, "0%")
        momChangePercent > 0.0 ->
            MomBadgeStyle(Grow50, Grow500, "▲", "${formatPercentVi(momChangePercent)}%")
        else ->
            MomBadgeStyle(Heat50, Heat500, "▼", "${formatPercentVi(kotlin.math.abs(momChangePercent))}%")
    }

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
                            append(" · Mới trong tháng này")
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

private fun buildFiveMonthBarTriples(
    points: List<MonthlyBarPoint>?,
    year: Int,
    month: Int
): List<Triple<String, Float, Boolean>> {
    if (points.isNullOrEmpty()) {
        val baseYm = YearMonth.of(year, month)
        return (4 downTo 0).map { offset ->
            val ym = baseYm.minusMonths(offset.toLong())
            Triple("T${ym.monthValue}", 0f, offset == 0)
        }
    }
    val maxAmount = points.maxOfOrNull { it.totalAmount } ?: 0L
    return points.map { p ->
        // Chi doi sang Float o buoc ve cuoi cung; tranh chia 0 khi ca 5 thang deu bang 0
        val fraction = if (maxAmount > 0L) {
            (p.totalAmount.toFloat() / maxAmount.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        Triple("T${p.month}", fraction, p.isSelected)
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
fun CashflowOverviewCard() {
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
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Dòng tiền ròng tháng 8",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "+8.488.000",
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

                Row(
                    modifier = Modifier
                        .background(Grow50, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("▲", fontSize = 9.sp, color = Grow500, fontFamily = FontCore)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "14%",
                        fontFamily = FontMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Grow500
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dual Bar Chart (Vào vs Ra) with perfectly aligned bottom baseline
            DualMonthlyBarChart(
                data = listOf(
                    DualBarData("T4", 0.50f, 0.65f, false),
                    DualBarData("T5", 0.60f, 0.45f, false),
                    DualBarData("T6", 0.55f, 0.75f, false),
                    DualBarData("T7", 0.70f, 0.85f, false),
                    DualBarData("T8", 0.88f, 0.82f, true)
                )
            )

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
fun CashflowPeriodSummaryCard() {
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
            CashflowSummaryRow(
                icon = Icons.Outlined.AccountBalanceWallet,
                iconBg = Ink100,
                iconTint = Ink600,
                title = "Số dư đầu kỳ",
                amount = "11.510.000₫",
                amountColor = MoneyFlowTheme.colors.textPrimary
            )

            HorizontalDivider(color = Ink100)

            CashflowSummaryRow(
                icon = Icons.AutoMirrored.Outlined.CallMade,
                iconBg = Grow50,
                iconTint = Grow500,
                title = "Tiền vào",
                amount = "+18.200.000₫",
                amountColor = Grow500
            )

            HorizontalDivider(color = Ink100)

            CashflowSummaryRow(
                icon = Icons.AutoMirrored.Outlined.ArrowForward,
                iconBg = Blue50,
                iconTint = Blue500,
                title = "Tiền ra",
                amount = "-9.712.000₫",
                amountColor = Heat500
            )

            HorizontalDivider(color = Ink100)

            CashflowSummaryRow(
                icon = Icons.Outlined.CheckCircleOutline,
                iconBg = Grow50,
                iconTint = Grow500,
                title = "Số dư cuối kỳ",
                amount = "19.998.000₫",
                amountColor = MoneyFlowTheme.colors.textPrimary
            )
        }
    }
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
fun WalletBreakdownCard() {
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
            // 1. Vietcombank
            WalletProgressItem(
                title = "Vietcombank ••42",
                amount = "+7.900.000₫",
                isPositive = true,
                progress = 0.75f,
                progressColor = Grow500
            )

            // 2. Ví Momo
            WalletProgressItem(
                title = "Ví Momo",
                amount = "-1.240.000₫",
                isPositive = false,
                progress = 0.25f,
                progressColor = Heat500
            )

            // 3. Tiền mặt
            WalletProgressItem(
                title = "Tiền mặt",
                amount = "-320.000₫",
                isPositive = false,
                progress = 0.10f,
                progressColor = Heat200
            )
        }
    }
}

@Composable
fun WalletProgressItem(
    title: String,
    amount: String,
    isPositive: Boolean,
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
                    color = if (isPositive) Grow500 else Heat500
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
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(progressColor, RoundedCornerShape(4.dp))
            )
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
