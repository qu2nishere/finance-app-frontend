package com.quan.finance_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.core.MoneyFlowCard
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    var darkMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MoneyFlowTheme.spacing.gutterScreen)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = MoneyFlowTheme.colors.textSecondary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Nguyễn Minh Quân", style = MoneyFlowTheme.typography.title2, color = MoneyFlowTheme.colors.textPrimary)
                Text(text = "quan@example.com", style = MoneyFlowTheme.typography.body, color = MoneyFlowTheme.colors.textSecondary)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        MoneyFlowCard {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Giao diện tối", style = MoneyFlowTheme.typography.body)
                    Switch(checked = darkMode, onCheckedChange = { darkMode = it })
                }
                Divider(color = MoneyFlowTheme.colors.borderSubtle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Đăng xuất", style = MoneyFlowTheme.typography.body, color = MoneyFlowTheme.colors.stateDanger)
                }
            }
        }
    }
}
