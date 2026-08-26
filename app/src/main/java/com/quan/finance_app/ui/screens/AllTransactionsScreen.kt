package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

data class LedgerEntry(
    val title: String,
    val meta: String,
    val amount: Long,
    val icon: ImageVector,
    val isIncome: Boolean = false
)

data class LedgerDay(
    val label: String,
    val total: Long,
    val entries: List<LedgerEntry>
)

private val ledgerDays = listOf(
    LedgerDay(
        "HÔM NAY · 22/08", -117_000L,
        listOf(
            LedgerEntry("Highlands Coffee", "Ăn uống · Ví Momo · 09:12", -72_000L, Icons.Outlined.Storefront),
            LedgerEntry("Grab · đi làm", "Di chuyển · Thẻ ●●42 · 08:05", -45_000L, Icons.Outlined.LocalShipping)
        )
    ),
    LedgerDay(
        "HÔM QUA · 21/08", -318_000L,
        listOf(
            LedgerEntry("VinMart", "Mua sắm · Tiền mặt · 19:44", -318_000L, Icons.Outlined.ShoppingCart)
        )
    ),
    LedgerDay(
        "THỨ TƯ · 20/08", 17_550_000L,
        listOf(
            LedgerEntry("Lương tháng 8", "Lương · Vietcombank ●●42", 18_000_000L, Icons.Outlined.Payments, isIncome = true),
            LedgerEntry("Tiền điện tháng 7", "Hoá đơn · Vietcombank ●●42", -450_000L, Icons.AutoMirrored.Outlined.ReceiptLong)
        )
    )
)

/**
 * 8c — Tất cả giao dịch, có tìm kiếm & lọc. Mở từ "Tất cả ›" ở trang chủ.
 */
@Composable
fun AllTransactionsScreen(
    onBack: () -> Unit = {},
    onOpenTransaction: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var categoryFilter by remember { mutableStateOf<String?>("Ăn uống") }

    val days = ledgerDays
        .map { day -> day.copy(entries = day.entries.filter { matchesQuery(it, query) }) }
        .filter { it.entries.isNotEmpty() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MfScreenHeader(
                title = "Tất cả giao dịch",
                onBack = onBack,
                actionIcon = Icons.Outlined.Tune
            )
        }

        // Ô tìm kiếm
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MoneyFlowTheme.colors.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Tìm theo tên, số tiền, ghi chú…",
                                fontFamily = FontCore,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textTertiary
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = FontCore,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            ),
                            cursorBrush = SolidColor(MoneyFlowTheme.colors.textPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Chip lọc
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MfPillChip(label = "Tháng 8", selected = true, onClick = { })
                if (categoryFilter != null) {
                    Surface(
                        modifier = Modifier.clickable { categoryFilter = null },
                        shape = RoundedCornerShape(999.dp),
                        color = Blue50,
                        border = BorderStroke(1.dp, Blue200)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = categoryFilter!!,
                                fontFamily = FontCore,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Blue600
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Bỏ lọc",
                                tint = Blue600,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
                MfPillChip(label = "Tất cả ví", selected = false, onClick = { })
                MfPillChip(label = "Chi & thu", selected = false, onClick = { })
            }
        }

        // Tổng quan
        item {
            MfCard {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SummaryCell("Tiền ra", "−9.712.000₫", Heat600, Modifier.weight(1f))
                    VerticalDivider(
                        modifier = Modifier.height(30.dp),
                        color = MoneyFlowTheme.colors.borderSubtle
                    )
                    SummaryCell("Tiền vào", "+18.200.000₫", Grow600, Modifier.weight(1f))
                    VerticalDivider(
                        modifier = Modifier.height(30.dp),
                        color = MoneyFlowTheme.colors.borderSubtle
                    )
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(
                            text = "42 giao dịch",
                            fontFamily = FontCore,
                            fontSize = 10.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                        Text(
                            text = "TB 313k/ngày",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        // Nhóm theo ngày
        days.forEach { day ->
            item {
                Spacer(modifier = Modifier.height(2.dp))
                MfSectionLabel(day.label, trailing = signedVnd(day.total))
            }
            item {
                MfCard {
                    day.entries.forEachIndexed { index, entry ->
                        MfRow(
                            title = entry.title,
                            icon = entry.icon,
                            iconTint = if (entry.isIncome) Grow600 else Heat600,
                            iconBackground = if (entry.isIncome) Grow50 else Heat50,
                            subtitle = entry.meta,
                            onClick = { onOpenTransaction(entry.title) },
                            trailing = {
                                Text(
                                    text = signedVnd(entry.amount),
                                    fontFamily = FontMono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (entry.isIncome) Grow600 else MoneyFlowTheme.colors.textPrimary
                                )
                            }
                        )
                        if (index != day.entries.lastIndex) {
                            HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                        }
                    }
                }
            }
        }

        if (days.isEmpty()) {
            item {
                Text(
                    text = "Không có giao dịch nào khớp “$query”.",
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            item {
                Text(
                    text = "Tải thêm 37 giao dịch",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = FontCore,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Blue600
                )
            }
        }
    }
}

@Composable
private fun SummaryCell(label: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(end = 12.dp)) {
        Text(
            text = label,
            fontFamily = FontCore,
            fontSize = 10.sp,
            color = MoneyFlowTheme.colors.textSecondary
        )
        Text(
            text = amount,
            fontFamily = FontMono,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = color
        )
    }
}

private fun matchesQuery(entry: LedgerEntry, query: String): Boolean {
    if (query.isBlank()) return true
    val q = query.trim()
    return entry.title.contains(q, ignoreCase = true) ||
        entry.meta.contains(q, ignoreCase = true) ||
        entry.amount.toString().contains(q)
}

/** -72000 -> "−72.000₫" · 18000000 -> "+18.000.000₫" */
internal fun signedVnd(amount: Long): String {
    val sign = if (amount < 0) "−" else "+"
    return sign + formatVnd(kotlin.math.abs(amount))
}
