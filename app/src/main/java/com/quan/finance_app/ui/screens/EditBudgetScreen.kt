package com.quan.finance_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.core.PrimaryButton
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hạn mức Ăn uống", style = MoneyFlowTheme.typography.title3) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MoneyFlowTheme.colors.surfacePage)
            )
        },
        containerColor = MoneyFlowTheme.colors.surfacePage
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier.size(120.dp),
                color = MoneyFlowTheme.colors.stateWarning,
                trackColor = MoneyFlowTheme.colors.surfaceSunken,
                strokeWidth = 12.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = "3,000,000",
                onValueChange = {},
                label = { Text("Số tiền hạn mức") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(text = "Lưu thay đổi", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}
