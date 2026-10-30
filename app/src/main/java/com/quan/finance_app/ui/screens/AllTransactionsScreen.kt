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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.data.remote.TransactionDto
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*
import com.quan.finance_app.ui.transaction.AllTransactionsViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class LedgerEntry(
    val id: String = "",
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

@Composable
fun AllTransactionsScreen(
    allTransactionsViewModel: AllTransactionsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenTransaction: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by allTransactionsViewModel.state.collectAsState()
    var query by remember { mutableStateOf("") }
    var flowFilter by remember { mutableStateOf<String?>("ALL") } // "ALL", "EXPENSE", "INCOME"

    val today = remember { LocalDate.now() }
    val yesterday = remember { LocalDate.now().minusDays(1) }

    // Map and group transactions by date
    val groupedDays = remember(uiState.transactions, query, flowFilter) {
        val filtered = uiState.transactions.filter { txn ->
            val matchFlow = when (flowFilter) {
                "EXPENSE" -> txn.type == "EXPENSE"
                "INCOME" -> txn.type == "INCOME"
                else -> true
            }
            val matchQuery = query.isBlank() ||
                    (txn.note?.contains(query, ignoreCase = true) == true) ||
                    (txn.categoryName?.contains(query, ignoreCase = true) == true) ||
                    (txn.merchant?.contains(query, ignoreCase = true) == true) ||
                    txn.amount.toString().contains(query)

            matchFlow && matchQuery
        }

        filtered
            .groupBy { it.occurredDate }
            .map { (dateStr, txns) ->
                val date = runCatching { LocalDate.parse(dateStr) }.getOrNull()
                val label = when (date) {
                    today -> "HÔM NAY · ${date.format(DateTimeFormatter.ofPattern("dd/MM"))}"
                    yesterday -> "HÔM QUA · ${date.format(DateTimeFormatter.ofPattern("dd/MM"))}"
                    null -> dateStr
                    else -> "NGÀY · ${date.format(DateTimeFormatter.ofPattern("dd/MM"))}"
                }
                val totalSigned = txns.sumOf { it.signedAmount }
                val entries = txns.map { txn ->
                    val isIncome = txn.type == "INCOME"
                    LedgerEntry(
                        id = txn.id,
                        title = txn.note?.ifBlank { null } ?: txn.categoryName ?: "Giao dịch",
                        meta = (txn.categoryName ?: "Chưa phân loại") + (txn.walletName?.let { " · $it" } ?: ""),
                        amount = txn.signedAmount,
                        icon = getCategoryIcon(txn.categoryName),
                        isIncome = isIncome
                    )
                }
                LedgerDay(label = label, total = totalSigned, entries = entries)
            }
    }

    val totalOut = remember(uiState.transactions) {
        uiState.transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    }
    val totalIn = remember(uiState.transactions) {
        uiState.transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
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
                MfPillChip(
                    label = "Tất cả",
                    selected = flowFilter == "ALL",
                    onClick = { flowFilter = "ALL" }
                )
                MfPillChip(
                    label = "Chỉ chi tiêu",
                    selected = flowFilter == "EXPENSE",
                    onClick = { flowFilter = "EXPENSE" }
                )
                MfPillChip(
                    label = "Chỉ thu nhập",
                    selected = flowFilter == "INCOME",
                    onClick = { flowFilter = "INCOME" }
                )
            }
        }

        // Tổng quan
        item {
            MfCard {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SummaryCell("Tiền ra", "−" + formatVnd(totalOut), Heat600, Modifier.weight(1f))
                    VerticalDivider(
                        modifier = Modifier.height(30.dp),
                        color = MoneyFlowTheme.colors.borderSubtle
                    )
                    SummaryCell("Tiền vào", "+" + formatVnd(totalIn), Grow600, Modifier.weight(1f))
                    VerticalDivider(
                        modifier = Modifier.height(30.dp),
                        color = MoneyFlowTheme.colors.borderSubtle
                    )
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(
                            text = "${uiState.transactions.size} giao dịch",
                            fontFamily = FontCore,
                            fontSize = 10.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            // Skeleton Loading Placeholders
            items(3) {
                AllTransactionsDaySkeleton()
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else if (groupedDays.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (query.isNotBlank()) "Không có giao dịch nào khớp “$query”." else "Chưa có giao dịch nào.",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Nhóm theo ngày
            groupedDays.forEach { day ->
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

internal fun signedVnd(amount: Long): String {
    val sign = if (amount < 0) "−" else "+"
    return sign + formatVnd(kotlin.math.abs(amount))
}
