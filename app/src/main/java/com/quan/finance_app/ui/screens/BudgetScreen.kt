package com.quan.finance_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.core.MoneyFlowCard
import com.quan.finance_app.ui.components.finance.BudgetBar
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun BudgetScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MoneyFlowTheme.spacing.gutterScreen)
    ) {
        Text(
            text = "Hạn mức tháng này",
            style = MoneyFlowTheme.typography.title1,
            color = MoneyFlowTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))
        MoneyFlowCard {
            Column {
                BudgetBar(
                    spent = 4500000.0,
                    limit = 10000000.0,
                    title = "Tổng chi tiêu",
                    color = MoneyFlowTheme.colors.surfaceBrand
                )
                Spacer(modifier = Modifier.height(24.dp))
                BudgetBar(
                    spent = 2000000.0,
                    limit = 3000000.0,
                    title = "Ăn uống",
                    color = com.quan.finance_app.ui.theme.CatFood
                )
            }
        }
    }
}
