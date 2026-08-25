package com.quan.finance_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.finance.BarChart
import com.quan.finance_app.ui.components.finance.FlowDirection
import com.quan.finance_app.ui.components.finance.TransactionRow
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun StatsIncomeScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            val data = listOf("T2" to 0f, "T3" to 0f, "T4" to 15f, "T5" to 0f, "T6" to 0f, "T7" to 0f, "CN" to 0f)
            BarChart(data = data, color = MoneyFlowTheme.colors.flowIn)
        }
        item {
            Text(
                text = "Tỷ lệ tiết kiệm",
                style = MoneyFlowTheme.typography.title3,
                modifier = Modifier.padding(16.dp)
            )
            // Mocking savings rate card visually with a TransactionRow
            TransactionRow(title = "Tháng này", date = "Tốt hơn tháng trước", amount = 3500000.0, direction = FlowDirection.IN)
        }
        item {
            Text(
                text = "Danh mục thu nhập",
                style = MoneyFlowTheme.typography.title3,
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            TransactionRow(title = "Lương", date = "100%", amount = 15000000.0, direction = FlowDirection.IN)
        }
    }
}
