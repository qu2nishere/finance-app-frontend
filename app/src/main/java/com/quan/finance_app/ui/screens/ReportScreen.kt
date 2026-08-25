package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.CallMade
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Info
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
import com.quan.finance_app.ui.theme.*

data class DualBarData(
    val label: String,
    val inFraction: Float,
    val outFraction: Float,
    val isCurrent: Boolean
)

@Composable
fun ReportScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentMonth by remember { mutableIntStateOf(8) }
    var currentYear by remember { mutableIntStateOf(2026) }
    val tabs = listOf("Chi tiêu", "Thu nhập", "Dòng tiền")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Month Selector Header
        item {
            MonthSelectorHeader(
                month = currentMonth,
                year = currentYear,
                onPreviousMonth = {
                    if (currentMonth == 1) {
                        currentMonth = 12
                        currentYear -= 1
                    } else {
                        currentMonth -= 1
                    }
                },
                onNextMonth = {
                    if (currentMonth == 12) {
                        currentMonth = 1
                        currentYear += 1
                    } else {
                        currentMonth += 1
                    }
                }
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

                item {
                    Text(
                        text = "Theo danh mục",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoneyFlowTheme.colors.textPrimary,
                        fontFamily = FontCore,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    ExpenseCategoryCard()
                }

                item {
                    InsightBanner(
                        icon = Icons.Outlined.Info,
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color(0xFF4A3B18), fontFamily = FontCore)) {
                                append("Chỗ tiêu nhiều nhất tháng này là ")
                            }
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF2C220E),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontCore
                                )
                            ) {
                                append("cà phê (18 lần, 1,1tr)")
                            }
                            withStyle(SpanStyle(color = Color(0xFF4A3B18), fontFamily = FontCore)) {
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
                            withStyle(SpanStyle(color = Color(0xFF4A3B18), fontFamily = FontCore)) {
                                append("Giữ nhịp này, cuối năm bạn dư ")
                            }
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF2C220E),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontCore
                                )
                            ) {
                                append("~68tr")
                            }
                            withStyle(SpanStyle(color = Color(0xFF4A3B18), fontFamily = FontCore)) {
                                append(". Chuyển ")
                            }
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF2C220E),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontCore
                                )
                            ) {
                                append("5tr/tháng")
                            }
                            withStyle(SpanStyle(color = Color(0xFF4A3B18), fontFamily = FontCore)) {
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
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "Tháng trước",
                tint = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "Tháng $month, $year",
            fontFamily = FontCore,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MoneyFlowTheme.colors.textPrimary
        )

        IconButton(
            onClick = onNextMonth,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Tháng sau",
                tint = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ReportTabs(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(index) }
                    .padding(vertical = 6.dp, horizontal = 12.dp)
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
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
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
                        .background(Color(0xFFE8F8F0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "▼",
                        fontSize = 9.sp,
                        color = Color(0xFF0FA162),
                        fontFamily = FontCore
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4,1%",
                        fontFamily = FontMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF0FA162)
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
                inactiveColor = Color(0xFFFFE0D2)
            )

            Spacer(modifier = Modifier.height(20.dp))
            DashedDivider(color = Color(0xFFDCE2EC))
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

@Composable
fun ExpenseCategoryCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
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
                progressColor = Color(0xFFFF9E6E)
            )

            CategoryProgressItem(
                title = "Hoá đơn & nhà ở",
                amount = "1.410.000₫",
                percent = "15%",
                progress = 0.30f,
                progressColor = Color(0xFFFFC3A3)
            )
        }
    }
}

// -------------------------------------------------------------------------
// 3. Income (Thu nhập) Components
// -------------------------------------------------------------------------

@Composable
fun IncomeOverviewCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
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
                        text = "Tổng thu tháng này",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "18.200.000",
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
                        .background(Color(0xFFE8F8F0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("▲", fontSize = 9.sp, color = Grow500, fontFamily = FontCore)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "5,2%",
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
                inactiveColor = Color(0xFFC9F5DD)
            )

            Spacer(modifier = Modifier.height(20.dp))
            DashedDivider(color = Color(0xFFDCE2EC))
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
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CategoryProgressItem(
                title = "Lương",
                amount = "18.000.000₫",
                percent = "98,9%",
                progress = 0.989f,
                progressColor = Grow500,
                trendText = "đều đặn 6 tháng",
                trendUp = null
            )

            CategoryProgressItem(
                title = "Thưởng",
                amount = "200.000₫",
                percent = "1,1%",
                progress = 0.12f,
                progressColor = Color(0xFF92E8BC)
            )

            CategoryProgressItem(
                title = "Freelance",
                amount = "0₫",
                percent = "0%",
                progress = 0.0f,
                progressColor = Color(0xFFDCE2EC),
                trendText = "chưa có tháng này",
                trendUp = null
            )
        }
    }
}

@Composable
fun SavingsRateCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
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
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
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
                        .background(Color(0xFFE8F8F0), RoundedCornerShape(12.dp))
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
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CashflowSummaryRow(
                icon = Icons.Outlined.AccountBalanceWallet,
                iconBg = Color(0xFFF0F4F8),
                iconTint = Ink600,
                title = "Số dư đầu kỳ",
                amount = "11.510.000₫",
                amountColor = MoneyFlowTheme.colors.textPrimary
            )

            HorizontalDivider(color = Color(0xFFF4F6F9))

            CashflowSummaryRow(
                icon = Icons.AutoMirrored.Outlined.CallMade,
                iconBg = Color(0xFFE8F8F0),
                iconTint = Grow500,
                title = "Tiền vào",
                amount = "+18.200.000₫",
                amountColor = Grow500
            )

            HorizontalDivider(color = Color(0xFFF4F6F9))

            CashflowSummaryRow(
                icon = Icons.AutoMirrored.Outlined.ArrowForward,
                iconBg = Color(0xFFEEF4FF),
                iconTint = Blue500,
                title = "Tiền ra",
                amount = "-9.712.000₫",
                amountColor = Heat500
            )

            HorizontalDivider(color = Color(0xFFF4F6F9))

            CashflowSummaryRow(
                icon = Icons.Outlined.CheckCircleOutline,
                iconBg = Color(0xFFE8F8F0),
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
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
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
                progressColor = Color(0xFFFFC3A3)
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
                .background(Color(0xFFF0F2F6), RoundedCornerShape(4.dp))
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
                                color = if (item.isCurrent) Grow500 else Color(0xFFC9F5DD),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    // Cột Ra (Orange)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(if (maxFraction > 0f) item.outFraction / maxFraction else 0f)
                            .background(
                                color = if (item.isCurrent) Heat500 else Color(0xFFFFE0D2),
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
                .background(Color(0xFFF0F2F6), RoundedCornerShape(4.dp))
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
    color: Color = Color(0xFFDCE2EC),
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
        color = Color(0xFFFFF8E6),
        border = BorderStroke(1.dp, Color(0xFFFFEFC2))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Gợi ý thông minh",
                tint = Color(0xFFC48304),
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
