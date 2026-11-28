package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.data.CategoryLimitItem
import com.quan.finance_app.data.CurrentBudgetData
import com.quan.finance_app.data.LimitStatus
import com.quan.finance_app.data.SavingsGoalItem
import com.quan.finance_app.ui.budget.AddCategoryLimitSheet
import com.quan.finance_app.ui.budget.BudgetUiState
import com.quan.finance_app.ui.budget.BudgetViewModel
import com.quan.finance_app.ui.budget.GoalsViewModel
import com.quan.finance_app.ui.components.core.MfInfoBanner
import com.quan.finance_app.ui.components.core.MfTabHeader
import com.quan.finance_app.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetRoute(
    onNavigateToSavingsGoals: () -> Unit = {},
    onCreateGoal: () -> Unit = {},
    onEditGoal: (String) -> Unit = {},
    budgetViewModel: BudgetViewModel = viewModel(),
    goalsViewModel: GoalsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by budgetViewModel.state.collectAsState()
    val goalsState by goalsViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        budgetViewModel.load()
        goalsViewModel.load()
    }

    val refresh = {
        budgetViewModel.load()
        goalsViewModel.load()
    }
    PullToRefreshBox(
        isRefreshing = state.loading,
        onRefresh = refresh,
        modifier = modifier.fillMaxSize()
    ) {
        BudgetScreen(
            state = state,
            activeGoals = goalsState.activeGoals.take(2),
            onRefresh = refresh,
            onOpenAddLimit = budgetViewModel::openCreateSheet,
            onEditLimit = budgetViewModel::openEditSheet,
            onNavigateToSavingsGoals = onNavigateToSavingsGoals,
            onCreateFirstGoal = onCreateGoal,
            onOpenGoal = onEditGoal
        )
    }

    if (state.sheetOpen) {
        AddCategoryLimitSheet(
            availableCategories = state.availableCategories,
            editingLimit = state.editingLimit,
            submitting = state.submitting,
            errorMessage = state.actionError,
            onDismiss = budgetViewModel::dismissSheet,
            onCreate = budgetViewModel::createLimit,
            onUpdate = budgetViewModel::updateLimit,
            onDelete = budgetViewModel::deleteLimit
        )
    }
}

@Composable
fun BudgetScreen(
    state: BudgetUiState = BudgetUiState(),
    activeGoals: List<SavingsGoalItem> = emptyList(),
    onRefresh: () -> Unit = {},
    onOpenAddLimit: () -> Unit = {},
    onEditLimit: (CategoryLimitItem) -> Unit = {},
    onNavigateToSavingsGoals: () -> Unit = {},
    onCreateFirstGoal: () -> Unit = {},
    onOpenGoal: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val budget = state.budget
    val limits = budget?.limits.orEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Ink50),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Top Header: Title "Ngân sách"
        item {
            BudgetHeader()
        }

        // 2. Main Monthly Budget Overview Card
        item {
            when {
                state.loading && budget == null -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Ink100)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Blue500, strokeWidth = 2.5.dp)
                        }
                    }
                }
                state.errorMessage != null && budget == null -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Alert100)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MfInfoBanner(
                                text = state.errorMessage,
                                icon = Icons.Outlined.WarningAmber,
                                background = Alert50,
                                contentColor = Alert600,
                                borderColor = Alert100
                            )
                            Button(
                                onClick = onRefresh,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Blue500)
                            ) {
                                Text("Thử lại", fontFamily = FontCore, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                budget != null -> {
                    TotalMonthlyBudgetCard(budget = budget)
                }
            }
        }

        // 3. Section "Theo danh mục"
        item {
            Text(
                text = "Theo danh mục",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontCore,
                color = MoneyFlowTheme.colors.textPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 4. Category Budget Cards or Empty State
        if (limits.isEmpty() && !state.loading) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Ink100)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Chưa có hạn mức danh mục",
                            style = MoneyFlowTheme.typography.title3,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                        Text(
                            text = "Đặt hạn mức cho từng danh mục chi tiêu để theo dõi sát mức chi tháng này.",
                            style = MoneyFlowTheme.typography.bodySm,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                }
            }
        } else {
            items(limits, key = { it.id }) { limit ->
                val progress = if (limit.limitAmount > 0L) {
                    (limit.spentAmount.toFloat() / limit.limitAmount.toFloat()).coerceIn(0f, 1f)
                } else 0f

                val (iconBg, iconTint, progressColor, borderColor) = when (limit.status) {
                    LimitStatus.EXCEEDED -> QuadColor(Alert50, Alert500, Alert500, Alert300)
                    LimitStatus.WARNING -> QuadColor(Sun50, Sun600, Sun500, Sun300)
                    LimitStatus.OK -> QuadColor(Blue50, Blue600, Heat500, Ink100)
                }

                val isOverBudget = limit.status == LimitStatus.EXCEEDED
                val overAmount = if (isOverBudget) formatVnd(limit.spentAmount - limit.limitAmount) else ""

                CategoryBudgetCard(
                    icon = getCategoryIcon(limit.categoryName),
                    iconBg = iconBg,
                    iconTint = iconTint,
                    title = limit.categoryName,
                    spentAmount = shortVnd(limit.spentAmount),
                    limitAmount = shortVnd(limit.limitAmount),
                    progress = progress,
                    progressColor = progressColor,
                    borderColor = borderColor,
                    isOverBudget = isOverBudget,
                    overAmount = overAmount,
                    onClick = { onEditLimit(limit) }
                )
            }
        }

        // Button: Đặt ngân sách cho danh mục khác
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable(onClick = onOpenAddLimit),
                shape = RoundedCornerShape(16.dp),
                color = Blue50,
                border = BorderStroke(1.dp, Blue100)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "+ Đặt ngân sách cho danh mục khác",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Blue600
                    )
                }
            }
        }

        // 5. Section "Mục tiêu tiết kiệm" with navigation button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mục tiêu tiết kiệm",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontCore,
                    color = MoneyFlowTheme.colors.textPrimary
                )

                Text(
                    text = "Tất cả >",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontCore,
                    color = Blue600,
                    modifier = Modifier.clickable { onNavigateToSavingsGoals() }
                )
            }
        }

        // 6. Savings Goals Cards (D9: up to 2 ACTIVE goals, or empty state "+ Tạo mục tiêu đầu tiên")
        item {
            if (activeGoals.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCreateFirstGoal),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Grow100)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Chưa có mục tiêu tiết kiệm",
                            style = MoneyFlowTheme.typography.title3,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                        Text(
                            text = "Đặt mục tiêu tích luỹ cho chuyến đi, quỹ dự phòng hoặc món đồ bạn muốn mua.",
                            style = MoneyFlowTheme.typography.bodySm,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Grow50,
                            border = BorderStroke(1.dp, Grow100)
                        ) {
                            Text(
                                text = "+ Tạo mục tiêu đầu tiên",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Grow600,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    activeGoals.forEachIndexed { idx, goal ->
                        val progress = if (goal.targetAmount > 0L) {
                            (goal.savedAmount.toFloat() / goal.targetAmount.toFloat()).coerceIn(0f, 1f)
                        } else 0f
                        val accent = parseGoalAccent(goal.color, if (idx == 0) Grow500 else Blue500)
                        val deadlineSuffix = goal.targetDate?.let { " · T${it.monthValue}" } ?: ""
                        SavingsGoalCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenGoal(goal.id) },
                            icon = if (idx == 0) Icons.Outlined.CardGiftcard else Icons.Outlined.Shield,
                            iconBg = accent.copy(alpha = 0.12f),
                            iconTint = accent,
                            title = goal.name,
                            progress = progress,
                            progressColor = accent,
                            infoText = "${shortVnd(goal.savedAmount)}/${shortVnd(goal.targetAmount)}$deadlineSuffix"
                        )
                    }
                    if (activeGoals.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private data class QuadColor(
    val iconBg: Color,
    val iconTint: Color,
    val progressColor: Color,
    val borderColor: Color
)

// -------------------------------------------------------------------------
// Sub-components
// -------------------------------------------------------------------------

@Composable
fun BudgetHeader() {
    MfTabHeader(title = "Ngân sách")
}

@Composable
fun TotalMonthlyBudgetCard(budget: CurrentBudgetData) {
    val monthNumber = budget.periodKey.substringAfter("-", "").toIntOrNull()
    val monthTitle = if (monthNumber != null) "Ngân sách tháng $monthNumber" else "Ngân sách tháng này"
    val totalLimit = budget.totalLimit
    val totalSpent = budget.totalSpent
    val isOver = totalLimit > 0L && totalSpent > totalLimit
    val progress = if (totalLimit > 0L) (totalSpent.toFloat() / totalLimit.toFloat()).coerceIn(0f, 1f) else 0f
    val usedPercent = if (totalLimit > 0L) ((totalSpent * 100.0) / totalLimit).roundToInt() else 0
    val barColor = when {
        isOver -> Alert500
        usedPercent >= 80 -> Sun500
        else -> Heat500
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, if (isOver) Alert300 else Ink100)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthTitle,
                    fontFamily = FontCore,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                Text(
                    text = "còn ${budget.daysLeft} ngày",
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = shortVnd(totalSpent),
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = if (isOver) Alert500 else Heat500,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = " / ${shortVnd(totalLimit)}",
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Ink100, RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress)
                        .fillMaxHeight()
                        .background(barColor, RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (totalLimit <= 0L) {
                Text(
                    text = "Chưa có hạn mức nào trong kỳ này — bấm nút bên dưới để đặt hạn mức.",
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    color = Ink600
                )
            } else if (isOver) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                            append("Đã dùng $usedPercent% · vượt ")
                        }
                        withStyle(
                            SpanStyle(
                                color = Alert500,
                                fontFamily = FontMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        ) {
                            append(formatVnd(totalSpent - totalLimit))
                        }
                    }
                )
            } else {
                val remaining = (totalLimit - totalSpent).coerceAtLeast(0L)
                val perDay = if (budget.daysLeft > 0) remaining / budget.daysLeft else 0L
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                            append("Đã dùng $usedPercent% · còn ")
                        }
                        withStyle(
                            SpanStyle(
                                color = Grow500,
                                fontFamily = FontMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        ) {
                            append(formatVnd(remaining))
                        }
                        if (budget.daysLeft > 0) {
                            withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                                append(" (~${shortVnd(perDay)}/ngày)")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryBudgetCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    spentAmount: String,
    limitAmount: String,
    progress: Float,
    progressColor: Color,
    borderColor: Color,
    isOverBudget: Boolean,
    overAmount: String = "",
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(if (isOverBudget) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
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
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Text(
                    text = "$spentAmount / $limitAmount",
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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

            if (isOverBudget) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = "Cảnh báo vượt hạn mức",
                        tint = Alert500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Vượt $overAmount",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Alert500
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsGoalCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    progress: Float,
    progressColor: Color,
    infoText: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontFamily = FontCore,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = infoText,
                fontFamily = FontMono,
                fontSize = 12.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }
    }
}
