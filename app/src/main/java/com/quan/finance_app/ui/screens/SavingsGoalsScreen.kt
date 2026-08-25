package com.quan.finance_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.core.MoneyFlowCard
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mục tiêu tiết kiệm", style = MoneyFlowTheme.typography.title3) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                actions = { IconButton(onClick = { }) { Icon(Icons.Default.Add, contentDescription = "Add") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MoneyFlowTheme.colors.surfacePage)
            )
        },
        containerColor = MoneyFlowTheme.colors.surfacePage
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            item {
                MoneyFlowCard(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            progress = { 0.45f },
                            modifier = Modifier.size(100.dp),
                            color = MoneyFlowTheme.colors.surfaceBrand,
                            trackColor = MoneyFlowTheme.colors.surfaceSunken,
                            strokeWidth = 10.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Mua MacBook", style = MoneyFlowTheme.typography.title2)
                        Text("15,000,000đ / 30,000,000đ", style = MoneyFlowTheme.typography.body)
                    }
                }
            }
        }
    }
}
