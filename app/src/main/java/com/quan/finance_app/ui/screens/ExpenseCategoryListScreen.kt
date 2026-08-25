package com.quan.finance_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.finance.FlowDirection
import com.quan.finance_app.ui.components.finance.TransactionRow
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCategoryListScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh mục chi tiêu", style = MoneyFlowTheme.typography.title3) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.Add, contentDescription = "Add") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(
                selectedTabIndex = 0,
                containerColor = MoneyFlowTheme.colors.surfacePage,
                contentColor = MoneyFlowTheme.colors.textBrand
            ) {
                Tab(selected = true, onClick = {}, text = { Text("Đã đặt hạn mức") })
                Tab(selected = false, onClick = {}, text = { Text("Chưa đặt") })
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    TransactionRow(title = "Ăn uống", date = "Hạn mức: 3,000,000đ", amount = 0.0, direction = FlowDirection.NEUTRAL)
                    TransactionRow(title = "Di chuyển", date = "Hạn mức: 1,500,000đ", amount = 0.0, direction = FlowDirection.NEUTRAL)
                }
            }
        }
    }
}
