package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.data.remote.TransactionDto
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.components.forms.DateTimePickerSheet
import com.quan.finance_app.ui.theme.*
import com.quan.finance_app.ui.transaction.AllTransactionsViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Currency
import java.util.Locale

internal fun transactionKind(type: String) = when (type) {
    "INCOME" -> "Thu nhập"
    "EXPENSE" -> "Chi tiêu"
    "TRANSFER_IN" -> "Chuyển đến"
    "TRANSFER_OUT" -> "Chuyển đi"
    "ADJUSTMENT" -> "Điều chỉnh"
    else -> type
}

internal fun TransactionDto.displayTitle() =
    listOf(merchant, note, categoryName).firstOrNull { !it.isNullOrBlank() } ?: "Giao dịch"

internal fun ledgerAmount(minor: Long, code: String): String {
    val currency = Currency.getInstance(code.trim())
    val places = currency.defaultFractionDigits.coerceAtLeast(0)
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))
    formatter.currency = currency
    formatter.minimumFractionDigits = places
    formatter.maximumFractionDigits = places
    return formatter.format(java.math.BigDecimal.valueOf(minor, places))
}

internal fun signedVnd(amount: Long): String =
    (if (amount < 0) "−" else "+") + formatVnd(kotlin.math.abs(amount))

@Composable
fun AllTransactionsScreen(
    allTransactionsViewModel: AllTransactionsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenTransaction: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by allTransactionsViewModel.state.collectAsState()
    val filter = state.filter
    val change = allTransactionsViewModel::setFilter
    var dateEdge by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(allTransactionsViewModel) { allTransactionsViewModel.open() }
    val days = remember(state.transactions) { state.transactions.groupBy { it.occurredDate } }
    LazyColumn(modifier.fillMaxSize().background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { MfScreenHeader("Tất cả giao dịch", onBack) }
        item {
            OutlinedTextField(filter.q, { change(filter.copy(q = it)) }, Modifier.fillMaxWidth(),
                label = { Text("Tìm theo tên, số tiền, ghi chú") }, singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done))
        }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterMenu("Loại", filter.type, listOf("INCOME" to "Thu nhập", "EXPENSE" to "Chi tiêu")) {
                    change(filter.copy(type = it))
                }
                FilterMenu("Ví", filter.walletId, state.wallets.map { it.id to it.name }) {
                    change(filter.copy(walletId = it))
                }
                FilterMenu("Danh mục", filter.categoryId, state.categories.map { it.id to it.name }) {
                    change(filter.copy(categoryId = it))
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton({ dateEdge = "from" }) { Text(filter.from ?: "Từ ngày") }
                TextButton({ dateEdge = "to" }) { Text(filter.to ?: "Đến ngày") }
                if (filter.from != null || filter.to != null) TextButton({
                    change(filter.copy(from = null, to = null))
                }) { Text("Bỏ lọc ngày") }
            }
        }
        if (state.optionsError) item {
            TextButton(allTransactionsViewModel::loadOptions) { Text("Chưa tải được ví/danh mục · Thử lại") }
        }
        if (state.totals.isNotEmpty() || state.transactions.isNotEmpty()) item {
            MfCard {
                Column(Modifier.padding(12.dp)) {
                    Text("${state.count} giao dịch")
                    state.totals.forEach {
                        Text("Thu ${ledgerAmount(it.totalIncome, it.currencyCode)} · Chi ${ledgerAmount(it.totalExpense, it.currencyCode)}")
                    }
                }
            }
        }
        days.forEach { (date, transactions) ->
            item(key = "day-$date") { MfSectionLabel(date) }
            items(transactions, key = { it.id }) { txn ->
                MfCard {
                    MfRow(title = txn.displayTitle(), icon = getCategoryIcon(txn.categoryName),
                        subtitle = listOfNotNull(transactionKind(txn.type), txn.categoryName, txn.walletName).joinToString(" · "),
                        onClick = { onOpenTransaction(txn.id) },
                        trailing = { Text(ledgerAmount(txn.signedAmount, txn.currencyCode)) })
                }
            }
        }
        item {
            when {
                state.error != null -> TextButton(allTransactionsViewModel::next) { Text("${state.error} Thử lại") }
                state.loading -> CircularProgressIndicator()
                state.nextPage != null -> LaunchedEffect(state.nextPage) { allTransactionsViewModel.next() }
                state.transactions.isEmpty() -> Text("Không có giao dịch phù hợp.")
            }
        }
    }
    dateEdge?.let { edge ->
        val selected = if (edge == "from") filter.from else filter.to
        DateTimePickerSheet(onDismissRequest = { dateEdge = null }, onDateSelected = {
            val date = it.toString()
            change(if (edge == "from") filter.copy(from = date, to = filter.to?.coerceAtLeast(date))
                else filter.copy(to = date, from = filter.from?.coerceAtMost(date)))
        }, brandColor = Blue600, brandSoftColor = Blue50,
            initialDate = selected?.let(LocalDate::parse) ?: LocalDate.now())
    }
}

@Composable
private fun FilterMenu(label: String, selected: String?, values: List<Pair<String, String>>,
                       select: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton({ expanded = true }) { Text(values.find { it.first == selected }?.second ?: label) }
        DropdownMenu(expanded, { expanded = false }) {
            DropdownMenuItem({ Text("Tất cả · $label") }, { select(null); expanded = false })
            values.forEach { (id, name) ->
                DropdownMenuItem({ Text(name) }, { select(id); expanded = false })
            }
        }
    }
}
