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
import com.quan.finance_app.ui.components.core.MfTabHeader
import com.quan.finance_app.ui.theme.*

data class DualBarData(
    val label: String,
    val inFraction: Float,
    val outFraction: Float,
    val isCurrent: Boolean
)

data class DailyExpenseItem(
    val title: String,
    val category: String,
    val amount: Long,
    val time: String,
    val wallet: String,
    val icon: ImageVector
)

data class DayExpenseGroup(
    val dateKey: String,
    val dateLabel: String,
    val totalAmount: Long,
    val items: List<DailyExpenseItem>
)

@Composable
fun ReportScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentMonth by remember { mutableIntStateOf(8) }
    var currentYear by remember { mutableIntStateOf(2026) }
    var selectedExpenseDayFilter by remember { mutableStateOf("ALL") }
    val goPreviousMonth = {
        selectedExpenseDayFilter = "ALL"
        if (currentMonth == 1) { currentMonth = 12; currentYear -= 1 } else currentMonth -= 1
    }
    val goNextMonth = {
        selectedExpenseDayFilter = "ALL"
        if (currentMonth == 12) { currentMonth = 1; currentYear += 1 } else currentMonth += 1
    }
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
                onPreviousMonth = { goPreviousMonth() },
                onNextMonth = { goNextMonth() },
                showMonthPicker = selectedTab != 0
            )
        }

        // 2. Custom Tabs (Chi tiêu: Cam, Thu nhập: Xanh lá, Dòng tiền: Xanh lam)
        item {
            ReportTabs(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
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
                        selectedDayKey = selectedExpenseDayFilter,
                        onSelectDayKey = { selectedExpenseDayFilter = it },
                        onPreviousMonth = { goPreviousMonth() },
                        onNextMonth = { goNextMonth() },
                        onPickDate = { d ->
                            currentMonth = d.monthValue
                            currentYear = d.year
                            selectedExpenseDayFilter = "%02d/%02d".format(d.dayOfMonth, d.monthValue)
                        }
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
                    IncomeOverviewCard()
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
                    IncomeSourceCard()
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
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    showMonthPicker: Boolean = true
) {
    MfTabHeader(title = "Thống kê") {
        if (showMonthPicker) {
            MonthStepper(month, year, onPreviousMonth, onNextMonth)
        }
    }
}

@Composable
fun MonthStepper(
    month: Int,
    year: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
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
        IconButton(onClick = onNextMonth, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Tháng sau",
                tint = MoneyFlowTheme.colors.textSecondary,
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

private fun getMonthTransactionsData(month: Int, year: Int): List<DayExpenseGroup> {
    val mStr = month.toString().padStart(2, '0')
    return when (month) {
        8 -> listOf(
            DayExpenseGroup(
                dateKey = "22/$mStr",
                dateLabel = "Hôm nay (22/$mStr)",
                totalAmount = 285000L,
                items = listOf(
                    DailyExpenseItem("Highlands Coffee", "Ăn uống", 72000L, "09:12", "Ví Momo", Icons.Outlined.Restaurant),
                    DailyExpenseItem("Cơm trưa văn phòng", "Ăn uống", 45000L, "12:30", "Tiền mặt", Icons.Outlined.Restaurant),
                    DailyExpenseItem("Siêu thị WinMart", "Mua sắm", 168000L, "18:45", "Vietcombank", Icons.Outlined.ShoppingBag)
                )
            ),
            DayExpenseGroup(
                dateKey = "21/$mStr",
                dateLabel = "Hôm qua (21/$mStr)",
                totalAmount = 310000L,
                items = listOf(
                    DailyExpenseItem("Grab Bike đi làm", "Di chuyển", 38000L, "08:15", "Ví Momo", Icons.Outlined.DirectionsCar),
                    DailyExpenseItem("Bún chả Hà Nội", "Ăn uống", 55000L, "12:15", "Tiền mặt", Icons.Outlined.Restaurant),
                    DailyExpenseItem("Nhà sách Fahasa", "Mua sắm", 185000L, "19:20", "Vietcombank", Icons.Outlined.ShoppingBag),
                    DailyExpenseItem("Trà sữa Gongcha", "Ăn uống", 32000L, "21:00", "Ví Momo", Icons.Outlined.Restaurant)
                )
            ),
            DayExpenseGroup(
                dateKey = "20/$mStr",
                dateLabel = "20/$mStr",
                totalAmount = 145000L,
                items = listOf(
                    DailyExpenseItem("Phở bò gia truyền", "Ăn uống", 50000L, "07:45", "Tiền mặt", Icons.Outlined.Restaurant),
                    DailyExpenseItem("Xăng xe máy", "Di chuyển", 95000L, "17:30", "Tiền mặt", Icons.Outlined.DirectionsCar)
                )
            ),
            DayExpenseGroup(
                dateKey = "19/$mStr",
                dateLabel = "19/$mStr",
                totalAmount = 520000L,
                items = listOf(
                    DailyExpenseItem("Tiền điện tháng 7", "Hoá đơn & nhà ở", 420000L, "10:00", "Vietcombank", Icons.Outlined.Receipt),
                    DailyExpenseItem("Bánh mì & Cà phê", "Ăn uống", 40000L, "08:00", "Ví Momo", Icons.Outlined.Restaurant),
                    DailyExpenseItem("Gửi xe tháng", "Di chuyển", 60000L, "18:00", "Tiền mặt", Icons.Outlined.DirectionsCar)
                )
            ),
            DayExpenseGroup(
                dateKey = "18/$mStr",
                dateLabel = "18/$mStr",
                totalAmount = 190000L,
                items = listOf(
                    DailyExpenseItem("Pizza Hut tối", "Ăn uống", 190000L, "19:30", "Ví Momo", Icons.Outlined.Restaurant)
                )
            ),
            DayExpenseGroup(
                dateKey = "15/$mStr",
                dateLabel = "15/$mStr",
                totalAmount = 1410000L,
                items = listOf(
                    DailyExpenseItem("Tiền nước sinh hoạt", "Hoá đơn & nhà ở", 125000L, "09:00", "Vietcombank", Icons.Outlined.Receipt),
                    DailyExpenseItem("Mua giày thể thao", "Mua sắm", 850000L, "15:30", "Vietcombank", Icons.Outlined.ShoppingBag),
                    DailyExpenseItem("Lẩu Kichi Kichi", "Ăn uống", 435000L, "20:00", "Ví Momo", Icons.Outlined.Restaurant)
                )
            ),
            DayExpenseGroup(
                dateKey = "12/$mStr",
                dateLabel = "12/$mStr",
                totalAmount = 185000L,
                items = listOf(
                    DailyExpenseItem("Đổ xăng xe máy", "Di chuyển", 120000L, "08:30", "Tiền mặt", Icons.Outlined.DirectionsCar),
                    DailyExpenseItem("Cà phê Trung Nguyên", "Ăn uống", 65000L, "14:00", "Ví Momo", Icons.Outlined.Restaurant)
                )
            ),
            DayExpenseGroup(
                dateKey = "08/$mStr",
                dateLabel = "08/$mStr",
                totalAmount = 675000L,
                items = listOf(
                    DailyExpenseItem("Gói cước 4G Viettel", "Hoá đơn & nhà ở", 150000L, "11:00", "Ví Momo", Icons.Outlined.Receipt),
                    DailyExpenseItem("Mua sắm Uniqlo", "Mua sắm", 525000L, "16:30", "Vietcombank", Icons.Outlined.ShoppingBag)
                )
            ),
            DayExpenseGroup(
                dateKey = "05/$mStr",
                dateLabel = "05/$mStr",
                totalAmount = 455000L,
                items = listOf(
                    DailyExpenseItem("Tiền internet FPT", "Hoá đơn & nhà ở", 275000L, "10:30", "Vietcombank", Icons.Outlined.Receipt),
                    DailyExpenseItem("Cơm trưa nhóm", "Ăn uống", 180000L, "12:45", "Ví Momo", Icons.Outlined.Restaurant)
                )
            ),
            DayExpenseGroup(
                dateKey = "01/$mStr",
                dateLabel = "01/$mStr",
                totalAmount = 3780000L,
                items = listOf(
                    DailyExpenseItem("Tiền thuê nhà tháng 8", "Hoá đơn & nhà ở", 3500000L, "09:00", "Vietcombank", Icons.Outlined.Receipt),
                    DailyExpenseItem("Grab Car đi sân bay", "Di chuyển", 280000L, "06:30", "Ví Momo", Icons.Outlined.DirectionsCar)
                )
            )
        )
        7 -> listOf(
            DayExpenseGroup(
                dateKey = "30/$mStr",
                dateLabel = "30/$mStr",
                totalAmount = 780000L,
                items = listOf(
                    DailyExpenseItem("Ăn tối nhà hàng", "Ăn uống", 650000L, "19:30", "Ví Momo", Icons.Outlined.Restaurant),
                    DailyExpenseItem("Grab Car", "Di chuyển", 130000L, "21:30", "Ví Momo", Icons.Outlined.DirectionsCar)
                )
            ),
            DayExpenseGroup(
                dateKey = "20/$mStr",
                dateLabel = "20/$mStr",
                totalAmount = 2150000L,
                items = listOf(
                    DailyExpenseItem("Bảo dưỡng xe máy", "Di chuyển", 1850000L, "10:00", "Vietcombank", Icons.Outlined.DirectionsCar),
                    DailyExpenseItem("Cơm trưa", "Ăn uống", 300000L, "12:00", "Tiền mặt", Icons.Outlined.Restaurant)
                )
            ),
            DayExpenseGroup(
                dateKey = "15/$mStr",
                dateLabel = "15/$mStr",
                totalAmount = 1420000L,
                items = listOf(
                    DailyExpenseItem("Tiền điện nước T6", "Hoá đơn & nhà ở", 520000L, "09:00", "Vietcombank", Icons.Outlined.Receipt),
                    DailyExpenseItem("Mua sắm đồ gia dụng", "Mua sắm", 900000L, "16:00", "Vietcombank", Icons.Outlined.ShoppingBag)
                )
            ),
            DayExpenseGroup(
                dateKey = "01/$mStr",
                dateLabel = "01/$mStr",
                totalAmount = 3500000L,
                items = listOf(
                    DailyExpenseItem("Tiền thuê nhà tháng 7", "Hoá đơn & nhà ở", 3500000L, "08:30", "Vietcombank", Icons.Outlined.Receipt)
                )
            )
        )
        else -> listOf(
            DayExpenseGroup(
                dateKey = "25/$mStr",
                dateLabel = "25/$mStr",
                totalAmount = 450000L,
                items = listOf(
                    DailyExpenseItem("Siêu thị Co.opmart", "Mua sắm", 320000L, "18:00", "Vietcombank", Icons.Outlined.ShoppingBag),
                    DailyExpenseItem("Ăn tối gia đình", "Ăn uống", 130000L, "19:30", "Tiền mặt", Icons.Outlined.Restaurant)
                )
            ),
            DayExpenseGroup(
                dateKey = "15/$mStr",
                dateLabel = "15/$mStr",
                totalAmount = 850000L,
                items = listOf(
                    DailyExpenseItem("Tiền điện nước", "Hoá đơn & nhà ở", 550000L, "09:00", "Vietcombank", Icons.Outlined.Receipt),
                    DailyExpenseItem("Xăng xe", "Di chuyển", 300000L, "16:00", "Tiền mặt", Icons.Outlined.DirectionsCar)
                )
            ),
            DayExpenseGroup(
                dateKey = "01/$mStr",
                dateLabel = "01/$mStr",
                totalAmount = 3500000L,
                items = listOf(
                    DailyExpenseItem("Tiền thuê nhà", "Hoá đơn & nhà ở", 3500000L, "08:00", "Vietcombank", Icons.Outlined.Receipt)
                )
            )
        )
    }
}

@Composable
fun MonthTransactionsLedgerCard(
    month: Int,
    year: Int,
    selectedDayKey: String,
    onSelectDayKey: (String) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onPickDate: (java.time.LocalDate) -> Unit
) {
    val monthGroups = remember(month, year) {
        getMonthTransactionsData(month, year)
    }

    val totalMonthSpent = remember(monthGroups) {
        monthGroups.sumOf { it.totalAmount }
    }

    val totalTxnCount = remember(monthGroups) {
        monthGroups.sumOf { it.items.size }
    }

    val filterChips = remember(monthGroups) {
        listOf("ALL" to "Tất cả các ngày") + monthGroups.map { it.dateKey to it.dateLabel }
    }

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

                if (selectedDayKey != "ALL") {
                    Text(
                        text = "Xem tất cả",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Blue500,
                        modifier = Modifier
                            .clickable { onSelectDayKey("ALL") }
                            .padding(4.dp)
                    )
                }
            }

            // Lớp lọc 1: theo tháng - tràn hết một hàng
            MonthStepper(
                month = month,
                year = year,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Ink50, RoundedCornerShape(12.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                fillWidth = true
            )

            // Lớp lọc 2: theo ngày (date picker) trong tháng đã chọn, kèm nút xem cả tháng
            var showDatePicker by remember { mutableStateOf(false) }
            val pickedLabel = if (selectedDayKey == "ALL") "Chọn ngày" else "Ngày $selectedDayKey"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedDayKey == "ALL") Ink100 else Heat500,
                    border = if (selectedDayKey == "ALL") BorderStroke(1.dp, Ink200) else null,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val fg = if (selectedDayKey == "ALL") MoneyFlowTheme.colors.textSecondary else Color.White
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
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
                    color = if (selectedDayKey == "ALL") Heat500 else Heat50,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectDayKey("ALL") }
                ) {
                    Text(
                        text = "Xem cả tháng",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = if (selectedDayKey == "ALL") Color.White else Heat700,
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
                    initialDate = java.time.LocalDate.of(year, month, 1)
                )
            }

            HorizontalDivider(color = Ink100)

            // Danh sách các giao dịch đã thực hiện trong tháng
            if (selectedDayKey == "ALL") {
                // Hiển thị toàn bộ giao dịch nhóm theo từng ngày
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    monthGroups.forEachIndexed { groupIndex, dayGroup ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Tiêu đề ngày
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
                                        text = dayGroup.dateLabel,
                                        fontFamily = FontCore,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MoneyFlowTheme.colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(${dayGroup.items.size} GD)",
                                        fontFamily = FontCore,
                                        fontSize = 11.sp,
                                        color = MoneyFlowTheme.colors.textSecondary
                                    )
                                }
                                Text(
                                    text = "-${formatVnd(dayGroup.totalAmount)}",
                                    fontFamily = FontMono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Heat500
                                )
                            }

                            // Các giao dịch trong ngày
                            dayGroup.items.forEach { txn ->
                                TransactionLedgerItemRow(txn = txn)
                            }
                        }

                        if (groupIndex < monthGroups.size - 1) {
                            DashedDivider(color = Ink100)
                        }
                    }
                }
            } else {
                // Hiển thị riêng ngày được chọn
                val selectedGroup = monthGroups.find { it.dateKey == selectedDayKey }
                if (selectedGroup == null) {
                    Text(
                        text = "Không có giao dịch nào trong ngày $selectedDayKey",
                        fontFamily = FontCore,
                        fontSize = 13.sp,
                        color = MoneyFlowTheme.colors.textSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Banner ngày chọn
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
                                    text = "Ngày: ${selectedGroup.dateLabel}",
                                    fontFamily = FontCore,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Heat700
                                )
                                Text(
                                    text = "${selectedGroup.items.size} giao dịch đã thực hiện",
                                    fontFamily = FontCore,
                                    fontSize = 11.sp,
                                    color = Heat600
                                )
                            }
                            Text(
                                text = "-${formatVnd(selectedGroup.totalAmount)}",
                                fontFamily = FontMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Heat500
                            )
                        }

                        // Danh sách giao dịch
                        selectedGroup.items.forEach { txn ->
                            TransactionLedgerItemRow(txn = txn)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionLedgerItemRow(txn: DailyExpenseItem) {
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
                    .background(Heat50, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = txn.icon,
                    contentDescription = txn.title,
                    tint = Heat500,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = txn.title,
                    fontFamily = FontCore,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                Text(
                    text = "${txn.category} · ${txn.time} · ${txn.wallet}",
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }
        }

        Text(
            text = "-${formatVnd(txn.amount)}",
            fontFamily = FontMono,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Heat500
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
fun IncomeOverviewCard() {
    var selectedIncomeType by remember { mutableStateOf("Tất cả nguồn thu") }
    val incomeTypes = listOf("Tất cả nguồn thu", "Lương", "Thưởng", "Freelance")

    val (currentAmount, currentChange) = when (selectedIncomeType) {
        "Lương" -> Pair("18.000.000", "98,9%")
        "Thưởng" -> Pair("200.000", "1,1%")
        "Freelance" -> Pair("0", "0%")
        else -> Pair("18.200.000", "5,2%")
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
                    text = "Dòng tiền vào",
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
                incomeTypes.forEach { typeName ->
                    val isSelected = selectedIncomeType == typeName
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Grow500 else Ink100,
                        border = if (isSelected) null else BorderStroke(1.dp, Ink200),
                        modifier = Modifier.clickable { selectedIncomeType = typeName }
                    ) {
                        Text(
                            text = typeName,
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

            // Phần hiển thị số tiền thu theo loại giao dịch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = if (selectedIncomeType == "Tất cả nguồn thu") "Tổng thu tháng này" else "Thu nhập: $selectedIncomeType",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = currentAmount,
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
                        text = currentChange,
                        fontFamily = FontMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Grow500
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5-Month Income Bar Chart with perfectly horizontal aligned baseline
            FiveMonthBarChart(
                data = listOf(
                    Triple("T4", 0.50f, false),
                    Triple("T5", 0.60f, false),
                    Triple("T6", 0.55f, false),
                    Triple("T7", 0.70f, false),
                    Triple("T8", 0.85f, true)
                ),
                activeColor = Grow500,
                inactiveColor = Grow100
            )

            Spacer(modifier = Modifier.height(20.dp))
            DashedDivider(color = Ink200)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                        append("Thu cố định ")
                    }
                    withStyle(SpanStyle(color = Ink900, fontFamily = FontMono, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                        append("18,0tr")
                    }
                    withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                        append(" · biến động ")
                    }
                    withStyle(SpanStyle(color = Ink900, fontFamily = FontMono, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                        append("200k")
                    }
                }
            )
        }
    }
}

@Composable
fun IncomeSourceCard() {
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
                title = "Lương (Thu định kỳ)",
                amount = "18.000.000₫",
                percent = "98,9%",
                progress = 0.989f,
                progressColor = Grow500,
                trendText = "đều đặn 6 tháng · Loại: Thu nhập cố định",
                trendUp = null
            )

            CategoryProgressItem(
                title = "Thưởng (Thu bổ sung)",
                amount = "200.000₫",
                percent = "1,1%",
                progress = 0.12f,
                progressColor = Grow200,
                trendText = "Loại: Thu nhập biến động"
            )

            CategoryProgressItem(
                title = "Freelance (Thu ngoài)",
                amount = "0₫",
                percent = "0%",
                progress = 0.0f,
                progressColor = Ink200,
                trendText = "chưa có tháng này · Loại: Dự án tự do",
                trendUp = null
            )
        }
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
