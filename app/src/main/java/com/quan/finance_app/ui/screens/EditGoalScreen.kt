package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.data.SavingsGoalItem
import com.quan.finance_app.ui.budget.GoalsViewModel
import com.quan.finance_app.ui.budget.WalletOption
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val DATE_DISPLAY_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private val GOAL_COLOR_OPTIONS = listOf(
    "#0FA162" to Grow500,
    "#F2571F" to Heat500,
    "#EFA707" to Sun500,
    "#2B6BF0" to Blue500
)

private val QUICK_GOAL_AMOUNTS = listOf(
    5_000_000L to "5tr",
    10_000_000L to "10tr",
    20_000_000L to "20tr",
    50_000_000L to "50tr"
)

@Composable
fun EditGoalRoute(
    goalId: String?,
    onBack: () -> Unit,
    onOpenDeposit: (String) -> Unit,
    goalsViewModel: GoalsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by goalsViewModel.state.collectAsState()
    val existingGoal = remember(goalId, state.activeGoals, state.completedGoals) {
        goalsViewModel.findGoalById(goalId)
    }

    LaunchedEffect(Unit) {
        goalsViewModel.clearActionError()
        if (state.wallets.isEmpty()) {
            goalsViewModel.load()
        }
    }

    LaunchedEffect(state.actionCompleted) {
        if (state.actionCompleted) {
            goalsViewModel.consumeActionCompleted()
            onBack()
        }
    }

    EditGoalScreen(
        goal = existingGoal,
        wallets = state.wallets,
        submitting = state.submitting,
        errorMessage = state.actionError,
        onBack = onBack,
        onSave = { name, targetAmount, colorHex, targetDate, wallet, priority, note ->
            if (existingGoal == null) {
                goalsViewModel.createGoal(
                    name = name,
                    targetAmount = targetAmount,
                    color = colorHex,
                    targetDate = targetDate,
                    wallet = wallet,
                    priority = priority,
                    note = note
                )
            } else {
                goalsViewModel.updateGoal(
                    goalId = existingGoal.id,
                    name = name,
                    targetAmount = targetAmount,
                    icon = existingGoal.icon,
                    color = colorHex,
                    targetDate = targetDate,
                    wallet = wallet,
                    priority = priority,
                    note = note
                )
            }
        },
        onDelete = {
            existingGoal?.let { goalsViewModel.deleteGoal(it.id) }
        },
        onDeposit = {
            existingGoal?.let { onOpenDeposit(it.id) }
        },
        modifier = modifier
    )
}

@Composable
fun EditGoalScreen(
    goal: SavingsGoalItem? = null,
    wallets: List<WalletOption> = emptyList(),
    submitting: Boolean = false,
    errorMessage: String? = null,
    onBack: () -> Unit = {},
    onSave: (
        name: String,
        targetAmount: Long,
        colorHex: String,
        targetDate: LocalDate?,
        wallet: WalletOption?,
        priority: Int,
        note: String?
    ) -> Unit = { _, _, _, _, _, _, _ -> },
    onDelete: () -> Unit = {},
    onDeposit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isEditMode = goal != null
    val today = remember { LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")) }

    var goalName by remember(goal?.id) { mutableStateOf(goal?.name ?: "") }
    var targetDigits by remember(goal?.id) { mutableStateOf(goal?.targetAmount?.toString() ?: "") }
    var selectedColorIndex by remember(goal?.id) {
        val idx = GOAL_COLOR_OPTIONS.indexOfFirst { it.first.equals(goal?.color, ignoreCase = true) }
        mutableIntStateOf(if (idx >= 0) idx else 0)
    }
    var targetDate by remember(goal?.id) { mutableStateOf(goal?.targetDate) }
    var selectedWallet by remember(goal?.id, wallets) {
        val matched = wallets.firstOrNull { it.id == goal?.walletId }
            ?: if (goal?.walletId != null && goal.walletName != null) WalletOption(goal.walletId, goal.walletName) else null
        mutableStateOf(matched)
    }
    var priority by remember(goal?.id) { mutableIntStateOf(goal?.priority ?: 1) }
    var note by remember(goal?.id) { mutableStateOf(goal?.note ?: "") }

    val parsedTarget = targetDigits.toLongOrNull() ?: 0L
    val savedAmount = goal?.savedAmount ?: 0L
    val previewProgress = if (parsedTarget > 0L) (savedAmount.toFloat() / parsedTarget.toFloat()).coerceIn(0f, 1f) else 0f
    val accentColor = GOAL_COLOR_OPTIONS[selectedColorIndex].second
    val accentHex = GOAL_COLOR_OPTIONS[selectedColorIndex].first
    val canSave = !submitting && goalName.trim().isNotEmpty() && parsedTarget > 0L

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Ink50),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    text = if (isEditMode) "Sửa mục tiêu" else "Tạo mục tiêu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontCore,
                    color = MoneyFlowTheme.colors.textPrimary
                )
            }
        }

        // 2. Goal Preview Header Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { previewProgress },
                            modifier = Modifier.fillMaxSize(),
                            color = accentColor,
                            trackColor = Ink100,
                            strokeWidth = 6.dp
                        )
                        Text(
                            text = "${(previewProgress * 100f).roundToInt()}%",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = goalName.ifBlank { "Tên mục tiêu mới" },
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatVnd(savedAmount),
                                fontFamily = FontMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = accentColor
                            )
                            Text(
                                text = " / ${formatVnd(parsedTarget)}",
                                fontFamily = FontMono,
                                fontSize = 13.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                        if (targetDate != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hạn hoàn thành: ${targetDate!!.format(DATE_DISPLAY_FMT)}",
                                fontFamily = FontCore,
                                fontSize = 11.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // 3. Form Fields Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Ink100)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Tên mục tiêu
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Tên mục tiêu",
                            style = MoneyFlowTheme.typography.bodySm,
                            fontWeight = FontWeight.SemiBold,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                        OutlinedTextField(
                            value = goalName,
                            onValueChange = { goalName = it.take(100) },
                            placeholder = { Text("VD: Du lịch Đà Lạt") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Số tiền mục tiêu
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Số tiền mục tiêu (VND)",
                            style = MoneyFlowTheme.typography.bodySm,
                            fontWeight = FontWeight.SemiBold,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                        OutlinedTextField(
                            value = targetDigits,
                            onValueChange = { raw -> targetDigits = raw.filter { it.isDigit() }.take(12) },
                            placeholder = { Text("VD: 10000000") },
                            suffix = { Text("₫", fontFamily = FontMono, fontWeight = FontWeight.Bold) },
                            supportingText = if (parsedTarget > 0L) {
                                { Text(formatVnd(parsedTarget), fontFamily = FontMono, color = accentColor) }
                            } else null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QUICK_GOAL_AMOUNTS.forEach { (value, label) ->
                                MfPillChip(
                                    label = label,
                                    selected = parsedTarget == value,
                                    selectedColor = accentColor,
                                    onClick = { targetDigits = value.toString() },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Ink100)

                    // Màu nhãn
                    FormRow(
                        icon = Icons.Outlined.CardGiftcard,
                        label = "Màu nhãn",
                        trailingContent = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GOAL_COLOR_OPTIONS.forEachIndexed { index, (_, color) ->
                                    val isSelected = selectedColorIndex == index
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .background(color, CircleShape)
                                            .then(
                                                if (isSelected) Modifier
                                                    .border(2.dp, Color.White, CircleShape)
                                                    .border(3.5.dp, color, CircleShape)
                                                else Modifier
                                            )
                                            .clickable { selectedColorIndex = index }
                                    )
                                }
                            }
                        }
                    )

                    HorizontalDivider(color = Ink100)

                    // Hạn hoàn thành
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FormRow(
                            icon = Icons.Outlined.CalendarToday,
                            label = "Ngày hoàn thành",
                            trailingContent = {
                                Text(
                                    text = targetDate?.format(DATE_DISPLAY_FMT) ?: "Không giới hạn",
                                    fontFamily = FontMono,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = MoneyFlowTheme.colors.textPrimary
                                )
                            }
                        )
                        val datePresets = listOf(
                            null to "Không hạn",
                            today.plusMonths(3) to "+3 tháng",
                            today.plusMonths(6) to "+6 tháng",
                            today.plusYears(1) to "+1 năm"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            datePresets.forEach { (presetDate, label) ->
                                MfPillChip(
                                    label = label,
                                    selected = targetDate == presetDate,
                                    selectedColor = Blue500,
                                    onClick = { targetDate = presetDate },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Ink100)

                    // Ví tích luỹ (từ getWallets)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FormRow(
                            icon = Icons.Outlined.AccountBalanceWallet,
                            label = "Ví tích luỹ",
                            trailingContent = {
                                Text(
                                    text = selectedWallet?.name ?: "Chưa chọn",
                                    fontFamily = FontCore,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = MoneyFlowTheme.colors.textPrimary
                                )
                            }
                        )
                        if (wallets.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                wallets.take(3).forEach { w ->
                                    MfPillChip(
                                        label = w.name,
                                        selected = selectedWallet?.id == w.id,
                                        selectedColor = Blue500,
                                        onClick = {
                                            selectedWallet = if (selectedWallet?.id == w.id) null else w
                                        }
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Ink100)

                    // Thứ tự ưu tiên
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Blue50, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FormatListNumbered,
                                    contentDescription = "Thứ tự ưu tiên",
                                    tint = Blue500,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Thứ tự ưu tiên",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MfStepperButton(increment = false, onClick = { if (priority > 0) priority-- })
                            Text(
                                text = "#$priority",
                                fontFamily = FontMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                            MfStepperButton(increment = true, onClick = { if (priority < 10) priority++ })
                        }
                    }

                    HorizontalDivider(color = Ink100)

                    // Ghi chú
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it.take(500) },
                        label = { Text("Ghi chú (tuỳ chọn)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            item {
                MfInfoBanner(
                    text = errorMessage,
                    icon = Icons.Outlined.WarningAmber,
                    background = Alert50,
                    contentColor = Alert600,
                    borderColor = Alert100
                )
            }
        }

        // 4. Edit-only Action Buttons: Nạp thêm + Xoá mục tiêu
        if (isEditMode) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable(enabled = !submitting, onClick = onDeposit),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Grow100)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Nạp thêm",
                                    tint = Grow500,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Nạp thêm tiền vào mục tiêu",
                                    fontFamily = FontCore,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Grow500
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                contentDescription = "Tiếp tục",
                                tint = Grow500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable(enabled = !submitting, onClick = onDelete),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Alert100)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Xoá",
                                tint = Alert500,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Xoá mục tiêu (giữ lại tiền đã nạp)",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Alert500
                            )
                        }
                    }
                }
            }
        }

        // 5. Bottom Action Buttons: Huỷ & Lưu mục tiêu
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    enabled = !submitting,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Ink200),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Ink600
                    )
                ) {
                    Text(
                        text = "Huỷ",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = {
                        onSave(
                            goalName.trim(),
                            parsedTarget,
                            accentHex,
                            targetDate,
                            selectedWallet,
                            priority,
                            note.trim().ifBlank { null }
                        )
                    },
                    enabled = canSave,
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue500,
                        contentColor = Color.White
                    )
                ) {
                    if (submitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Lưu mục tiêu",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FormRow(
    icon: ImageVector,
    label: String,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Blue50, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Blue500,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontFamily = FontCore,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }

        trailingContent()
    }
}
