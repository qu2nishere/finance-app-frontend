package com.quan.finance_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.finance.BarChart
import com.quan.finance_app.ui.components.finance.FlowDirection
import com.quan.finance_app.ui.components.finance.TransactionRow
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun ReportScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Chi tiêu", "Thu nhập", "Dòng tiền")

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MoneyFlowTheme.colors.surfacePage,
            contentColor = MoneyFlowTheme.colors.textBrand
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MoneyFlowTheme.typography.body) }
                )
            }
        }

        when (selectedTab) {
            0 -> StatsExpenseContent()
            1 -> StatsIncomeScreen()
            2 -> StatsCashflowScreen()
        }
    }
}

@Composable
fun StatsExpenseContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            val data = listOf("T2" to 20f, "T3" to 50f, "T4" to 30f, "T5" to 80f, "T6" to 40f, "T7" to 60f, "CN" to 10f)
            BarChart(data = data, color = MoneyFlowTheme.colors.flowOut)
        }
        item {
            Text(
                text = "Danh mục chi tiêu",
                style = MoneyFlowTheme.typography.title3,
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            TransactionRow(title = "Ăn uống", date = "60%", amount = 4500000.0, direction = FlowDirection.OUT)
            TransactionRow(title = "Di chuyển", date = "20%", amount = 1500000.0, direction = FlowDirection.OUT)
            TransactionRow(title = "Mua sắm", date = "20%", amount = 1500000.0, direction = FlowDirection.OUT)
        }
    }
}
