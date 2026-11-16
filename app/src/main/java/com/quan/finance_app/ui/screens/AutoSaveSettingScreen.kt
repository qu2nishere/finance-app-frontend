package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/** Một nơi có thể nhận tiền trích: mục tiêu tiết kiệm hoặc ví riêng. */
private class AutoSaveTarget(
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    selected: Boolean,
    percent: Int
) {
    var selected by mutableStateOf(selected)
    var percent by mutableIntStateOf(percent)
}

/**
 * 8a — Tự trích hằng tháng. Mở từ hàng "Tự trích vào mục tiêu" ở màn thu (4b, 7b).
 * Bản turn 10: chia % cho nhiều mục tiêu, mọi số đều sửa được.
 */
@Composable
fun AutoSaveSettingScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val monthlyIncome = 18_000_000L

    val targets = remember {
        mutableStateListOf(
            AutoSaveTarget("Quỹ dự phòng", "11.000.000₫ / 50.000.000₫", Icons.Outlined.VerifiedUser, true, 15),
            AutoSaveTarget("Du lịch Đà Lạt", "4.500.000₫ / 10.000.000₫", Icons.Outlined.CardGiftcard, true, 5),
            AutoSaveTarget("Đổi điện thoại", "3.000.000₫ / 12.000.000₫", Icons.Outlined.Smartphone, false, 0),
            AutoSaveTarget("Ví tiết kiệm riêng", "Không gắn mục tiêu nào", Icons.Outlined.AccountBalanceWallet, false, 0)
        )
    }

    var fixedAmountEnabled by remember { mutableStateOf(false) }
    var withdrawOnIncome by remember { mutableStateOf(true) }
    var skipWhenLowBalance by remember { mutableStateOf(true) }
    var minBalance by remember { mutableStateOf("2.000.000") }

    val selected = targets.filter { it.selected }
    val total = selected.sumOf { it.percent }
    val deducted = monthlyIncome * total / 100

    // Sửa tổng % -> chia lại đều cho các mục tiêu đang chọn
    fun setTotal(newTotal: Int) {
        val t = newTotal.coerceIn(0, 50)
        val sel = targets.filter { it.selected }
        if (sel.isEmpty()) return
        val base = t / sel.size
        var extra = t - base * sel.size
        sel.forEach { g ->
            g.percent = base + if (extra > 0) 1 else 0
            if (extra > 0) extra--
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = MoneyFlowTheme.colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tự trích hằng tháng",
                    modifier = Modifier.weight(1f),
                    fontFamily = FontCore,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                Text(
                    text = "Huỷ",
                    fontFamily = FontCore,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.clickable(onClick = onBack)
                )
            }
        }

        item {
            MfInfoBanner(
                text = "Mở từ “Tự trích vào mục tiêu” khi ghi khoản thu",
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                background = Grow50,
                contentColor = Grow600,
                borderColor = Grow100
            )
        }

        // Trích vào đâu
        item { MfSectionLabel("TRÍCH VÀO ĐÂU") }
        item {
            MfCard(borderColor = Grow100) {
                targets.forEachIndexed { index, target ->
                    AutoSaveTargetRow(
                        target = target,
                        onToggle = {
                            target.selected = !target.selected
                            target.percent = if (target.selected) 5 else 0
                        },
                        onBumpPercent = {
                            target.percent = if (target.percent >= 50) 5 else target.percent + 5
                        }
                    )
                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 13.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = Grow600,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tạo mục tiêu mới để trích vào",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Grow600
                    )
                }
            }
        }

        item {
            MfInfoBanner(
                text = if (selected.isEmpty()) "Chưa chọn nơi nhận — bật ít nhất một mục tiêu để trích."
                else "Tổng $total% chia cho ${selected.size} mục tiêu — sửa % ở từng dòng hoặc đổi tổng bên dưới.",
                icon = Icons.Outlined.Info,
                background = Grow50,
                contentColor = Grow600,
                borderColor = Grow100
            )
        }

        // Tổng %
        item {
            MfCard(borderColor = Grow100) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MfSectionLabel("TỔNG % THU NHẬP TRÍCH RA")
                    Spacer(modifier = Modifier.height(10.dp))

                    MfAmountStepperField(
                        value = total.toString(),
                        onValueChange = { setTotal(it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0) },
                        suffix = "%",
                        accent = Grow600,
                        onIncrement = { setTotal(total + 1) },
                        onDecrement = { setTotal(total - 1) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Slider(
                        value = total.toFloat(),
                        onValueChange = { setTotal(it.toInt()) },
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
                                selected = total == value,
                                selectedColor = Grow500,
                                fillWhenSelected = false,
                                onClick = { setTotal(value) },
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

        // Số tiền cố định
        item { MfSectionLabel("HOẶC ĐẶT SỐ TIỀN CỐ ĐỊNH") }
        item {
            MfCard {
                MfRow(
                    title = "Số tiền cố định mỗi tháng",
                    icon = Icons.Outlined.Payments,
                    iconTint = MoneyFlowTheme.colors.textTertiary,
                    iconBackground = Ink100,
                    iconSize = 30.dp,
                    titleColor = MoneyFlowTheme.colors.textSecondary,
                    trailing = {
                        Text(
                            text = if (fixedAmountEnabled) "Bật" else "Tắt",
                            fontFamily = FontCore,
                            fontSize = 12.sp,
                            color = MoneyFlowTheme.colors.textTertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = fixedAmountEnabled,
                            onCheckedChange = { fixedAmountEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Grow500,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Ink200,
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
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
                    iconSize = 30.dp,
                    trailing = {
                        MfSegmentedPills(
                            options = listOf("Khi nhận thu", "Ngày 25"),
                            selectedIndex = if (withdrawOnIncome) 0 else 1,
                            onSelect = { withdrawOnIncome = it == 0 }
                        )
                    }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Trừ từ ví",
                    icon = Icons.Outlined.AccountBalanceWallet,
                    iconSize = 30.dp,
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
                MfRow(
                    title = "Bỏ qua nếu số dư dưới",
                    icon = Icons.Outlined.WarningAmber,
                    iconSize = 30.dp,
                    trailing = {
                        MfEditableChip(
                            value = "$minBalance₫",
                            accent = Blue600,
                            onClick = {
                                // wireframe: bấm để sửa — ở đây xoay vòng vài mức thường dùng
                                minBalance = when (minBalance) {
                                    "1.000.000" -> "2.000.000"
                                    "2.000.000" -> "3.000.000"
                                    "3.000.000" -> "5.000.000"
                                    else -> "1.000.000"
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = skipWhenLowBalance,
                            onCheckedChange = { skipWhenLowBalance = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Blue500,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Ink200,
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
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
private fun AutoSaveTargetRow(
    target: AutoSaveTarget,
    onToggle: () -> Unit,
    onBumpPercent: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (target.selected) Grow50 else Color.White)
            .clickable(onClick = onToggle)
            .padding(horizontal = 13.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MfIconTile(
            icon = target.icon,
            tint = if (target.selected) Grow600 else MoneyFlowTheme.colors.textTertiary,
            background = if (target.selected) Color.White else Ink100,
            size = 32.dp
        )
        Spacer(modifier = Modifier.width(11.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = target.name,
                fontFamily = FontCore,
                fontWeight = if (target.selected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 13.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = target.subtitle,
                fontFamily = if (target.subtitle.contains('₫')) FontMono else FontCore,
                fontSize = 11.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (target.selected) {
            MfEditableChip(
                value = "${target.percent}%",
                accent = Grow600,
                onClick = onBumpPercent
            )
        } else {
            Text(
                text = "Chọn",
                fontFamily = FontCore,
                fontSize = 12.sp,
                color = MoneyFlowTheme.colors.textTertiary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        MfCheckSquare(checked = target.selected, accent = Grow500, onClick = onToggle)
    }
}

/** 18000000 -> "18.000.000₫" */
internal fun formatVnd(amount: Long): String {
    val digits = amount.toString().reversed().chunked(3).joinToString(".").reversed()
    return "$digits₫"
}
