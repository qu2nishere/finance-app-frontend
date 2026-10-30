package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

private data class RecurringItem(
    val name: String,
    val schedule: String,
    val amount: Long,
    val icon: ImageVector,
    val isIncome: Boolean = false,
    val isDueSoon: Boolean = false
)

private val upcomingItems = listOf(
    RecurringItem("Tiền nhà", "Ngày 25 · 3 ngày nữa", -2_000_000L, Icons.AutoMirrored.Outlined.ReceiptLong, isDueSoon = true),
    RecurringItem("Gói data 4G", "Ngày 28 · còn 6 ngày", -120_000L, Icons.Outlined.Smartphone),
    RecurringItem("Lương", "Ngày 20 · đã nhận", 18_000_000L, Icons.Outlined.Payments, isIncome = true)
)

private val allItems = listOf(
    RecurringItem("Internet", "Ngày 5", -250_000L, Icons.Outlined.Home),
    RecurringItem("Bảo hiểm sức khoẻ", "Ngày 10", -600_000L, Icons.Outlined.FavoriteBorder),
    RecurringItem("Netflix", "Ngày 15", -200_000L, Icons.Outlined.LocalFireDepartment)
)

/**
 * 8f — Giao dịch định kỳ. Mở từ Cài đặt (3e).
 */
@Composable
fun RecurringTransactionsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Bật/tắt từng mục — lưu theo tên để state sống qua recomposition
    val enabled = remember {
        mutableStateMapOf<String, Boolean>().apply {
            (upcomingItems + allItems).forEach { put(it.name, true) }
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
            MfScreenHeader(
                title = "Giao dịch định kỳ",
                onBack = onBack,
                actionText = "Thêm"
            )
        }

        // Tổng quan
        item {
            MfCard {
                Row(modifier = Modifier.padding(14.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cố định mỗi tháng",
                            fontFamily = FontCore,
                            fontSize = 10.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                        Text(
                            text = "−3.170.000₫",
                            fontFamily = FontMono,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Heat600
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier.height(32.dp),
                        color = MoneyFlowTheme.colors.borderSubtle
                    )
                    Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
                        Text(
                            text = "Thu cố định",
                            fontFamily = FontCore,
                            fontSize = 10.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                        Text(
                            text = "+18.000.000₫",
                            fontFamily = FontMono,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Grow600
                        )
                    }
                }
            }
        }

        item { MfSectionLabel("SẮP TỚI") }
        item {
            MfCard {
                upcomingItems.forEachIndexed { index, item ->
                    RecurringRow(item, enabled[item.name] == true) { enabled[item.name] = it }
                    if (index != upcomingItems.lastIndex) {
                        HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                    }
                }
            }
        }

        item { MfSectionLabel("TẤT CẢ · ${upcomingItems.size + allItems.size} MỤC") }
        item {
            MfCard {
                allItems.forEachIndexed { index, item ->
                    RecurringRow(item, enabled[item.name] == true) { enabled[item.name] = it }
                    if (index != allItems.lastIndex) {
                        HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                    }
                }
            }
        }

        item {
            MfInfoBanner(
                text = "Tiền nhà sẽ trừ trong 3 ngày. Số dư ví Vietcombank sau khi trừ: 7,2tr",
                icon = Icons.Outlined.Info,
                background = Sun50,
                contentColor = Sun600,
                borderColor = Sun100
            )
        }

        item {
            MfDashedAction(
                text = "Thêm giao dịch định kỳ",
                color = Blue600,
                borderColor = Blue200,
                onClick = { }
            )
        }
    }
}

@Composable
private fun RecurringRow(
    item: RecurringItem,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        MfIconTile(
            icon = item.icon,
            tint = if (item.isIncome) Grow600 else Heat600,
            background = if (item.isIncome) Grow50 else Heat50
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontFamily = FontCore,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.schedule,
                fontFamily = FontCore,
                fontSize = 11.sp,
                fontWeight = if (item.isDueSoon) FontWeight.SemiBold else FontWeight.Normal,
                color = if (item.isDueSoon) Sun600 else MoneyFlowTheme.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        MfAmountTrailing(
            amount = signedVnd(item.amount),
            caption = "mỗi tháng",
            amountColor = if (item.isIncome) Grow600 else MoneyFlowTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Blue500,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Ink200,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
