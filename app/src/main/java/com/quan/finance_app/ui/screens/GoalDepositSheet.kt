package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.data.SavingsGoalItem
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 8b — Nạp tiền vào mục tiêu. Bottom sheet phủ lên trang mục tiêu.
 */
@Composable
fun GoalDepositSheet(
    goal: SavingsGoalItem,
    submitting: Boolean = false,
    errorMessage: String? = null,
    onDismissRequest: () -> Unit = {},
    onConfirmDeposit: (amount: Long, note: String?) -> Unit = { _, _ -> }
) {
    val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0L)
    var amountDigits by remember(goal.id) {
        val defaultAmt = if (remaining in 1L..1_000_000L) remaining else 1_000_000L
        mutableStateOf(defaultAmt.toString())
    }
    var note by remember(goal.id) { mutableStateOf("") }

    val amount = amountDigits.toLongOrNull() ?: 0L
    val afterDeposit = goal.savedAmount + amount
    val quickOptions = buildList {
        add(500_000L to "500k")
        add(1_000_000L to "1tr")
        add(2_000_000L to "2tr")
        if (remaining > 0L) add(remaining to "Còn lại")
    }
    val canConfirm = !submitting && amount > 0L

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfaceOverlay)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { if (!submitting) onDismissRequest() }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { }
                ),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 28.dp)
            ) {
                // Tay nắm
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 44.dp, height = 4.dp)
                        .background(MoneyFlowTheme.colors.borderStrong, RoundedCornerShape(99.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Mục tiêu
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Grow50,
                    border = BorderStroke(1.dp, Grow100)
                ) {
                    MfRow(
                        title = goal.name,
                        icon = goalIconForName(goal.icon),
                        iconTint = Grow600,
                        iconBackground = Color.White,
                        titleWeight = FontWeight.Bold,
                        subtitle = "${formatVnd(goal.savedAmount)} / ${formatVnd(goal.targetAmount)} · còn ${shortVnd(remaining)}"
                    )
                }

                // Số tiền nạp
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "SỐ TIỀN NẠP",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.9.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountDigits,
                    onValueChange = { raw -> amountDigits = raw.filter { it.isDigit() }.take(12) },
                    placeholder = { Text("Nhập số tiền nạp") },
                    suffix = { Text("₫", fontFamily = FontMono, fontWeight = FontWeight.Bold, color = Grow600) },
                    supportingText = if (amount > 0L) {
                        { Text(formatVnd(amount), fontFamily = FontMono, fontWeight = FontWeight.Bold, color = Grow600) }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Chip số tiền nhanh
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickOptions.forEach { (value, label) ->
                        MfPillChip(
                            label = label,
                            selected = amount == value,
                            selectedColor = Grow500,
                            fillWhenSelected = false,
                            onClick = { amountDigits = value.toString() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it.take(255) },
                    label = { Text("Ghi chú (tuỳ chọn)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (!goal.walletName.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ví tham chiếu: ${goal.walletName} (chỉ ghi nhận tiến độ, không trừ ví)",
                        style = MoneyFlowTheme.typography.caption,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                val previewText = if (afterDeposit >= goal.targetAmount && goal.targetAmount > 0L) {
                    "Sau khi nạp: ${shortVnd(afterDeposit)} / ${shortVnd(goal.targetAmount)} — đạt mục tiêu!"
                } else {
                    val leftAfter = (goal.targetAmount - afterDeposit).coerceAtLeast(0L)
                    "Sau khi nạp: ${shortVnd(afterDeposit)} / ${shortVnd(goal.targetAmount)} · còn thiếu ${shortVnd(leftAfter)}"
                }
                MfInfoBanner(
                    text = previewText,
                    icon = Icons.Outlined.CheckCircle,
                    background = Grow50,
                    contentColor = Grow600
                )

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    MfInfoBanner(
                        text = errorMessage,
                        icon = Icons.Outlined.WarningAmber,
                        background = Alert50,
                        contentColor = Alert600,
                        borderColor = Alert100
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        enabled = !submitting,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MoneyFlowTheme.colors.textSecondary)
                    ) {
                        Text("Huỷ", fontFamily = FontCore, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onConfirmDeposit(amount, note.trim().ifBlank { null }) },
                        enabled = canConfirm,
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Grow500, contentColor = Color.White)
                    ) {
                        if (submitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Nạp tiền", fontFamily = FontCore, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/** 5500000 -> "5,5tr" · 500000 -> "500k" */
internal fun shortVnd(amount: Long): String = when {
    amount >= 1_000_000 -> {
        val millions = amount / 1_000_000.0
        val text = if (millions % 1.0 == 0.0) millions.toInt().toString()
        else String.format("%.1f", millions).replace('.', ',')
        "${text}tr"
    }
    amount >= 1_000 -> "${amount / 1_000}k"
    else -> amount.toString()
}
