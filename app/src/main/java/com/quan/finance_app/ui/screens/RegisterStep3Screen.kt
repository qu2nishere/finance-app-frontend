package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

private class SuggestedLimit(
    val name: String,
    val icon: ImageVector,
    amount: Long,
    enabled: Boolean
) {
    var amount by mutableLongStateOf(amount)
    var enabled by mutableStateOf(enabled)
}

/**
 * 10c — Đăng ký bước 3/3: thu nhập & hạn mức đầu tiên.
 */
@Composable
fun RegisterStep3Screen(
    onBack: () -> Unit = {},
    onFinish: () -> Unit = {},
    onSkip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var income by remember { mutableLongStateOf(18_000_000L) }
    var savePercent by remember { mutableIntStateOf(20) }

    val limits = remember {
        mutableStateListOf(
            SuggestedLimit("Ăn uống", Icons.Outlined.Storefront, 3_500_000L, true),
            SuggestedLimit("Di chuyển", Icons.Outlined.LocalShipping, 2_500_000L, true),
            SuggestedLimit("Mua sắm", Icons.Outlined.ShoppingBag, 2_000_000L, true),
            SuggestedLimit("Hoá đơn", Icons.AutoMirrored.Outlined.ReceiptLong, 4_000_000L, false)
        )
    }

    val totalLimit = limits.filter { it.enabled }.sumOf { it.amount }
    val spendPct = if (income > 0) (totalLimit * 100 / income).toInt().coerceIn(0, 100) else 0
    val savePct = 100 - spendPct
    val saveAmount = income - totalLimit

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 28.dp)
    ) {
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
                text = "Tạo tài khoản",
                modifier = Modifier.weight(1f),
                fontFamily = FontCore,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Text(
                text = "Bỏ qua",
                fontFamily = FontCore,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.clickable(onClick = onSkip)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        RegisterProgress(step = 3)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bước 3/3 · Hạn mức đầu tiên",
            fontFamily = FontCore,
            fontSize = 11.sp,
            color = MoneyFlowTheme.colors.textTertiary
        )

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Thu nhập & hạn mức",
            fontFamily = FontCore,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MoneyFlowTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Nhập thu nhập tháng để app gợi ý hạn mức. Sửa số nào cũng được.",
            fontFamily = FontCore,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = MoneyFlowTheme.colors.textSecondary
        )

        // Thu nhập
        Spacer(modifier = Modifier.height(16.dp))
        MfCard(borderColor = Grow100) {
            Column(modifier = Modifier.padding(16.dp)) {
                MfSectionLabel("THU NHẬP MỖI THÁNG")
                Spacer(modifier = Modifier.height(10.dp))
                MfAmountStepperField(
                    value = groupDigits(income),
                    onValueChange = { raw ->
                        income = raw.filter { it.isDigit() }.take(12).toLongOrNull() ?: 0L
                    },
                    suffix = "₫",
                    accent = Grow600,
                    onIncrement = { income += 1_000_000L },
                    onDecrement = { income = (income - 1_000_000L).coerceAtLeast(0L) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(10_000_000L to "10tr", 15_000_000L to "15tr", 18_000_000L to "18tr", 25_000_000L to "25tr")
                        .forEach { (value, label) ->
                            MfPillChip(
                                label = label,
                                selected = income == value,
                                selectedColor = Grow500,
                                fillWhenSelected = false,
                                onClick = { income = value },
                                modifier = Modifier.weight(1f)
                            )
                        }
                }
            }
        }

        // Tổng hạn mức chi
        Spacer(modifier = Modifier.height(14.dp))
        MfCard(borderColor = Blue200) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tổng hạn mức chi",
                        modifier = Modifier.weight(1f),
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                    MfEditableChip(
                        value = formatVnd(totalLimit),
                        accent = Heat600,
                        onClick = { }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                MfSplitBar(
                    leftFraction = spendPct / 100f,
                    leftColor = Heat500,
                    rightColor = Grow500
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "Chi $spendPct%",
                        modifier = Modifier.weight(1f),
                        fontFamily = FontCore,
                        fontSize = 11.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Text(
                        text = "Tiết kiệm $savePct% · ${shortVnd(saveAmount.coerceAtLeast(0L))}",
                        fontFamily = FontCore,
                        fontSize = 11.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }
        }

        // Hạn mức theo danh mục
        Spacer(modifier = Modifier.height(18.dp))
        MfSectionLabel("HẠN MỨC THEO DANH MỤC")
        Spacer(modifier = Modifier.height(8.dp))
        MfCard {
            limits.forEachIndexed { index, limit ->
                MfRow(
                    title = limit.name,
                    icon = limit.icon,
                    iconTint = if (limit.enabled) Heat600 else MoneyFlowTheme.colors.textTertiary,
                    iconBackground = if (limit.enabled) Heat50 else Ink100,
                    subtitle = "Gợi ý từ mức chi phổ biến",
                    trailing = {
                        if (limit.enabled) {
                            MfEditableChip(
                                value = formatVnd(limit.amount),
                                accent = Heat600,
                                onClick = { limit.amount += 500_000L }
                            )
                        } else {
                            Text(
                                text = "Bỏ qua",
                                fontFamily = FontCore,
                                fontSize = 12.sp,
                                color = MoneyFlowTheme.colors.textTertiary,
                                modifier = Modifier.clickable { limit.enabled = true }
                            )
                        }
                    }
                )
                if (index != limits.lastIndex) {
                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                }
            }
        }

        // Tự trích tiết kiệm
        Spacer(modifier = Modifier.height(14.dp))
        MfCard {
            MfRow(
                title = "Tự trích tiết kiệm mỗi tháng",
                icon = Icons.Outlined.Percent,
                iconTint = Grow600,
                iconBackground = Grow50,
                trailing = {
                    MfEditableChip(
                        value = "$savePercent%",
                        accent = Grow600,
                        onClick = { savePercent = if (savePercent >= 50) 5 else savePercent + 5 }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onBack),
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
            ) {
                Text(
                    text = "Quay lại",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = FontCore,
                    fontSize = 15.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }
            Surface(
                modifier = Modifier
                    .weight(2f)
                    .clickable(onClick = onFinish),
                shape = RoundedCornerShape(12.dp),
                color = Blue500
            ) {
                Text(
                    text = "Bắt đầu dùng app",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = FontCore,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}

/** 18000000 -> "18.000.000" (không kèm ₫, dùng cho ô nhập). */
internal fun groupDigits(amount: Long): String =
    amount.toString().reversed().chunked(3).joinToString(".").reversed()
