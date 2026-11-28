package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import com.quan.finance_app.ui.transaction.TransactionDetailViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TransactionDetailScreen(
    id: String,
    onBack: () -> Unit,
    detailViewModel: TransactionDetailViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by detailViewModel.state.collectAsState()
    LaunchedEffect(id) { detailViewModel.load(id) }
    Column(modifier.fillMaxSize().background(MoneyFlowTheme.colors.surfacePage)
        .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MfScreenHeader("Chi tiết giao dịch", onBack)
        val txn = state.transaction
        when {
            state.id != id -> CircularProgressIndicator()
            txn != null -> {
                MfCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(txn.displayTitle(), style = MaterialTheme.typography.titleLarge)
                        Text(ledgerAmount(txn.signedAmount, txn.currencyCode),
                            style = MaterialTheme.typography.headlineMedium)
                        Text(transactionKind(txn.type))
                    }
                }
                val timestamp = Instant.parse(txn.occurredAt).atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                DetailField("Ngày giờ", timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                DetailField("Ví", txn.walletName ?: "Ví không còn hiển thị")
                DetailField("Danh mục", txn.categoryName ?: "Chưa phân loại")
                DetailField("Ghi chú", txn.note?.takeIf { it.isNotBlank() } ?: "Chưa có ghi chú")
            }
            state.message != null -> {
                Text(state.message.orEmpty())
                if (!state.missing) OutlinedButton({ detailViewModel.load(id) }) { Text("Thử lại") }
            }
            else -> CircularProgressIndicator()
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
