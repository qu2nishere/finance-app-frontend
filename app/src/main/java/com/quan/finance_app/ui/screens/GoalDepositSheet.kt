package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 8b — Nạp tiền vào mục tiêu. Bottom sheet phủ lên trang mục tiêu (5b, 5c).
 */
@Composable
fun GoalDepositSheet(
    goalName: String = "Du lịch Đà Lạt",
    currentAmount: Long = 4_500_000L,
    targetAmount: Long = 10_000_000L,
    onDismissRequest: () -> Unit = {}
) {
    var amount by remember { mutableLongStateOf(1_400_000L) }
    var repeatMonthly by remember { mutableStateOf(true) }

    val remaining = (targetAmount - currentAmount).coerceAtLeast(0L)
    val afterDeposit = currentAmount + amount
    val quickOptions = listOf(500_000L to "500k", 1_000_000L to "1tr", 1_400_000L to "1,4tr", remaining to "Còn lại")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfaceOverlay)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismissRequest
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
                        title = goalName,
                        icon = Icons.Outlined.CardGiftcard,
                        iconTint = Grow600,
                        iconBackground = Color.White,
                        titleWeight = FontWeight.Bold,
                        subtitle = "${formatVnd(currentAmount)} / ${formatVnd(targetAmount)} · còn ${shortVnd(remaining)}"
                    )
                }

                // Số tiền nạp
                Spacer(modifier = Modifier.height(20.dp))
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatVnd(amount),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = FontMono,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    color = Grow600
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 150.dp, height = 2.dp)
                        .background(Grow500, RoundedCornerShape(2.dp))
                )

                // Chip số tiền nhanh
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickOptions.forEach { (value, label) ->
                        MfPillChip(
                            label = label,
                            selected = amount == value,
                            selectedColor = Grow500,
                            fillWhenSelected = false,
                            onClick = { amount = value },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Nguồn tiền + lặp lại
                Spacer(modifier = Modifier.height(16.dp))
                MfCard {
                    MfRow(
                        title = "Trừ từ · Vietcombank ●●42",
                        icon = Icons.Outlined.AccountBalanceWallet,
                        iconTint = MoneyFlowTheme.colors.textTertiary,
                        iconBackground = Ink100,
                        titleColor = MoneyFlowTheme.colors.textSecondary,
                        showChevron = true,
                        onClick = { }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                MfCard {
                    MfToggleRow(
                        title = "Lặp lại mỗi tháng",
                        checked = repeatMonthly,
                        onCheckedChange = { repeatMonthly = it },
                        accent = Grow500
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                MfInfoBanner(
                    text = "Sau khi nạp: ${shortVnd(afterDeposit)} / ${shortVnd(targetAmount)} — về đích sớm 1 tháng",
                    icon = Icons.Outlined.CheckCircle,
                    background = Grow50,
                    contentColor = Grow600
                )

                Spacer(modifier = Modifier.height(18.dp))
                MfFooterActions(
                    cancelText = "Huỷ",
                    confirmText = "Nạp tiền",
                    confirmColor = Grow500,
                    onCancel = onDismissRequest,
                    onConfirm = onDismissRequest
                )
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
