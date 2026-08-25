package com.quan.finance_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.finance.BarChart
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun StatsCashflowScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            // Reusing BarChart for now, ideally this would be a grouped bar chart
            val data = listOf("T2" to 20f, "T3" to 50f, "T4" to 30f, "T5" to 80f, "T6" to 40f, "T7" to 60f, "CN" to 10f)
            BarChart(data = data, color = MoneyFlowTheme.colors.textBrand)
        }
        item {
            Text(
                text = "Phân tích theo ví",
                style = MoneyFlowTheme.typography.title3,
                modifier = Modifier.padding(16.dp)
            )
        }
        // Wallets list would go here
    }
}
