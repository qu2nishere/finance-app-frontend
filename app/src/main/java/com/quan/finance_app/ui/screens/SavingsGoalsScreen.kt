package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.data.SavingsGoalItem
import com.quan.finance_app.ui.budget.GoalsUiState
import com.quan.finance_app.ui.budget.GoalsViewModel
import com.quan.finance_app.ui.components.core.MfInfoBanner
import com.quan.finance_app.ui.theme.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

private val MONTH_YEAR_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/yyyy")

internal fun parseGoalAccent(hex: String?, fallback: Color = Grow500): Color {
    if (hex.isNullOrBlank()) return fallback
    return runCatching {
        Color(android.graphics.Color.parseColor(hex.trim()))
    }.getOrDefault(fallback)
}

internal fun goalIconForName(iconName: String?): ImageVector = when (iconName) {
    "Shield" -> Icons.Outlined.Shield
    "PhoneIphone" -> Icons.Outlined.PhoneIphone
    "LaptopMac" -> Icons.Outlined.LaptopMac
    "Flight" -> Icons.Outlined.Flight
    "Home" -> Icons.Outlined.Home
    "DirectionsCar" -> Icons.Outlined.DirectionsCar
    else -> Icons.Outlined.CardGiftcard
}

@Composable
fun SavingsGoalsRoute(
    onBack: () -> Unit,
    onAddGoal: () -> Unit,
    onEditGoal: (String) -> Unit,
    onDeposit: (String) -> Unit,
    goalsViewModel: GoalsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by goalsViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        goalsViewModel.load()
    }

    SavingsGoalsScreen(
        state = state,
        onBack = onBack,
        onAddGoal = onAddGoal,
        onEditGoal = onEditGoal,
        onDeposit = onDeposit,
        onRefresh = goalsViewModel::load,
        modifier = modifier
    )
}

@Composable
fun SavingsGoalsScreen(
    state: GoalsUiState = GoalsUiState(),
    onBack: () -> Unit = {},
    onAddGoal: () -> Unit = {},
    onEditGoal: (String) -> Unit = {},
    onDeposit: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Ink50),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Top Header with Back button and "+ Thêm"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mục tiêu tiết kiệm",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontCore,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Surface(
                    modifier = Modifier.clickable(onClick = onAddGoal),
                    shape = RoundedCornerShape(20.dp),
                    color = Blue50
                ) {
                    Text(
                        text = "+ Thêm",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Blue600,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Error state
        if (state.errorMessage != null && state.allGoals.isEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

        // 2. Overview Card (Tổng đã tích luỹ)
        item {
            TotalAccumulatedCard(
                totalSaved = state.totalSaved,
                totalTarget = state.totalTarget,
                activeCount = state.activeGoals.size,
                completedCount = state.completedGoals.size
            )
        }

        if (state.loading && state.allGoals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Grow500, strokeWidth = 2.5.dp)
                }
            }
        }

        // 3. Section "Đang chạy (n)"
        item {
            Text(
                text = "Đang chạy (${state.activeGoals.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontCore,
                color = MoneyFlowTheme.colors.textPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 4. Active Goals List or empty card
        if (state.activeGoals.isEmpty() && !state.loading) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAddGoal),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Ink100)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Chưa có mục tiêu đang chạy",
                            style = MoneyFlowTheme.typography.title3,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                        Text(
                            text = "Bấm \"+ Thêm\" để tạo mục tiêu tiết kiệm mới.",
                            style = MoneyFlowTheme.typography.bodySm,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                }
            }
        } else {
            items(state.activeGoals, key = { it.id }) { goal ->
                val progress = if (goal.targetAmount > 0L) {
                    (goal.savedAmount.toFloat() / goal.targetAmount.toFloat()).coerceIn(0f, 1f)
                } else 0f
                val accent = parseGoalAccent(goal.color, Grow500)
                ActiveGoalCard(
                    icon = goalIconForName(goal.icon),
                    iconBg = accent.copy(alpha = 0.12f),
                    iconTint = accent,
                    title = goal.name,
                    spentAmount = shortVnd(goal.savedAmount),
                    targetAmount = shortVnd(goal.targetAmount),
                    subInfo = buildGoalSubInfo(goal),
                    progress = progress,
                    progressColor = accent,
                    onClick = { onEditGoal(goal.id) },
                    onDeposit = { onDeposit(goal.id) }
                )
            }
        }

        // 5. Section "Đã hoàn thành (n)"
        if (state.completedGoals.isNotEmpty()) {
            item {
                Text(
                    text = "Đã hoàn thành (${state.completedGoals.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontCore,
                    color = MoneyFlowTheme.colors.textPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(state.completedGoals, key = { it.id }) { goal ->
                val completedMonth = goal.completedAt
                    ?.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                    ?.format(MONTH_YEAR_FMT)
                val info = buildString {
                    append("Hoàn thành")
                    if (completedMonth != null) append(" $completedMonth")
                    append(" · ${shortVnd(goal.savedAmount)}")
                }
                CompletedGoalCard(
                    title = goal.name,
                    info = info,
                    onClick = { onEditGoal(goal.id) }
                )
            }
        }
    }
}

private fun buildGoalSubInfo(goal: SavingsGoalItem): String {
    val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0L)
    val deadline = goal.targetDate ?: return if (goal.walletName != null) {
        "Ví ${goal.walletName}"
    } else {
        "Còn thiếu ${shortVnd(remaining)}"
    }
    val today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))
    val monthsLeft = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), deadline.withDayOfMonth(1))
        .coerceAtLeast(1L)
    val perMonth = (remaining + monthsLeft - 1) / monthsLeft
    return "Cần ${shortVnd(perMonth)}/tháng · hạn ${deadline.format(MONTH_YEAR_FMT)}"
}

// -------------------------------------------------------------------------
// Sub-components
// -------------------------------------------------------------------------

@Composable
fun TotalAccumulatedCard(
    totalSaved: Long,
    totalTarget: Long,
    activeCount: Int,
    completedCount: Int
) {
    val progress = if (totalTarget > 0L) (totalSaved.toFloat() / totalTarget.toFloat()).coerceIn(0f, 1f) else 0f
    val percentText = "${(progress * 100f).roundToInt()}%"
    val formattedSaved = formatVnd(totalSaved).removeSuffix("₫")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(76.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = Grow500,
                        trackColor = Grow50,
                        strokeWidth = 7.dp
                    )
                    Text(
                        text = percentText,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column {
                    Text(
                        text = "Tổng đã tích luỹ",
                        fontFamily = FontCore,
                        fontSize = 13.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formattedSaved,
                            fontFamily = FontMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Grow500,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "₫",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Grow500,
                            modifier = Modifier.padding(bottom = 2.dp, start = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "trên tổng mục tiêu ${shortVnd(totalTarget)}",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "$activeCount đang chạy · $completedCount đã hoàn thành",
                fontFamily = FontCore,
                fontSize = 13.sp,
                color = Ink600
            )
        }
    }
}

@Composable
fun ActiveGoalCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    spentAmount: String,
    targetAmount: String,
    subInfo: String,
    subInfoColor: Color = MoneyFlowTheme.colors.textSecondary,
    progress: Float,
    progressColor: Color,
    onClick: () -> Unit,
    onDeposit: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = progressColor,
                    trackColor = Ink100,
                    strokeWidth = 5.dp
                )
                Text(
                    text = "${(progress * 100f).roundToInt()}%",
                    fontFamily = FontCore,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(iconBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$spentAmount / $targetAmount",
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subInfo,
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    color = subInfoColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable(onClick = onDeposit),
                shape = RoundedCornerShape(10.dp),
                color = Grow50
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nạp thêm",
                        tint = Grow600,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedGoalCard(
    title: String,
    info: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Grow50, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Đã hoàn thành",
                        tint = Grow500,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = info,
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Xem chi tiết",
                tint = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
