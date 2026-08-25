package com.quan.finance_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.core.PrimaryButton
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mục tiêu mới", style = MoneyFlowTheme.typography.title3) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MoneyFlowTheme.colors.surfacePage)
            )
        },
        containerColor = MoneyFlowTheme.colors.surfacePage
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(value = "Mua MacBook", onValueChange = {}, label = { Text("Tên mục tiêu") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = "30,000,000", onValueChange = {}, label = { Text("Số tiền mục tiêu") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = "31/12/2026", onValueChange = {}, label = { Text("Ngày hoàn thành (Tuỳ chọn)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(text = "Lưu mục tiêu", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}
