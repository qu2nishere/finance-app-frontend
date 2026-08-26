package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 8a — Tự trích hằng tháng. Mở từ hàng "Tự trích vào mục tiêu" ở màn thu (4b, 7b).
 */
@Composable
fun AutoSaveSettingScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val monthlyIncome = 18_000_000L
    var percent by remember { mutableIntStateOf(20) }
    var fixedAmountEnabled by remember { mutableStateOf(false) }
    var withdrawOnIncome by remember { mutableStateOf(true) }
    var skipWhenLowBalance by remember { mutableStateOf(true) }

    val deducted = monthlyIncome * percent / 100

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            MfScreenHeader(
                title = "Tự trích hằng tháng",
                onBack = onBack,
                actionText = "Huỷ",
                actionColor = MoneyFlowTheme.colors.textSecondary,
                onAction = onBack
            )
        }

        // Mục tiêu đang nhận tiền trích
        item {
            MfCard {
                MfRow(
                    title = "Quỹ dự phòng",
                    icon = Icons.Outlined.Savings,
                    iconTint = Grow600,
                    iconBackground = Grow50,
                    iconSize = 38.dp,
                    titleWeight = FontWeight.Bold,
                    subtitle = "11.000.000₫ / 50.000.000₫"
                )
            }
        }

        // Ô nhập % + slider + chip nhanh
        item {
            MfCard(borderColor = Grow100) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MfSectionLabel("PHẦN TRĂM THU NHẬP TRÍCH RA")
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.5.dp, Grow500)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = percent.toString(),
                                    fontFamily = FontMono,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 30.sp,
                                    color = Grow600
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "%",
                                    fontFamily = FontCore,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Grow600,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            StepperButton(Icons.Outlined.KeyboardArrowUp) {
                                percent = (percent + 1).coerceAtMost(50)
                            }
                            StepperButton(Icons.Outlined.KeyboardArrowDown) {
                                percent = (percent - 1).coerceAtLeast(0)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Slider(
                        value = percent.toFloat(),
                        onValueChange = { percent = it.toInt() },
                        valueRange = 0f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = Grow500,
                            activeTrackColor = Grow500,
                            inactiveTrackColor = Ink100
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("0%", "25%", "50%").forEach {
                            Text(
                                text = it,
                                fontFamily = FontCore,
                                fontSize = 11.sp,
                                color = MoneyFlowTheme.colors.textTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5, 10, 20, 30).forEach { value ->
                            MfPillChip(
                                label = "$value%",
                                selected = percent == value,
                                selectedColor = Grow500,
                                fillWhenSelected = false,
                                onClick = { percent = value },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    MfInfoBanner(
                        text = "Thu ${formatVnd(monthlyIncome)} → trích ${formatVnd(deducted)} mỗi tháng",
                        icon = Icons.Outlined.Info,
                        background = Grow50,
                        contentColor = Grow600
                    )
                }
            }
        }

        // Hoặc số tiền cố định
        item { MfSectionLabel("HOẶC ĐẶT SỐ TIỀN CỐ ĐỊNH") }
        item {
            MfCard {
                MfToggleRow(
                    title = "Số tiền cố định mỗi tháng",
                    icon = Icons.Outlined.Payments,
                    iconTint = MoneyFlowTheme.colors.textTertiary,
                    iconBackground = Ink100,
                    checked = fixedAmountEnabled,
                    onCheckedChange = { fixedAmountEnabled = it },
                    accent = Grow500
                )
            }
        }

        // Thời điểm trích
        item { MfSectionLabel("THỜI ĐIỂM TRÍCH") }
        item {
            MfCard {
                MfRow(
                    title = "Ngày trích",
                    icon = Icons.Outlined.CalendarMonth,
                    trailing = {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            MfPillChip(
                                label = "Khi nhận thu",
                                selected = withdrawOnIncome,
                                onClick = { withdrawOnIncome = true }
                            )
                            MfPillChip(
                                label = "Ngày 25",
                                selected = !withdrawOnIncome,
                                onClick = { withdrawOnIncome = false }
                            )
                        }
                    }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Trừ từ ví",
                    icon = Icons.Outlined.AccountBalanceWallet,
                    onClick = { },
                    trailing = {
                        Text(
                            text = "Vietcombank ●●42",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Bỏ qua nếu số dư dưới",
                    subtitle = "2.000.000₫",
                    icon = Icons.Outlined.WarningAmber,
                    checked = skipWhenLowBalance,
                    onCheckedChange = { skipWhenLowBalance = it }
                )
            }
        }

        item {
            MfFooterActions(
                cancelText = "Huỷ",
                confirmText = "Lưu tự trích",
                confirmColor = Grow500,
                onCancel = onBack,
                onConfirm = onBack
            )
        }
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = 40.dp, height = 28.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(7.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/** 18000000 -> "18.000.000₫" */
internal fun formatVnd(amount: Long): String {
    val digits = amount.toString().reversed().chunked(3).joinToString(".").reversed()
    return "$digits₫"
}
