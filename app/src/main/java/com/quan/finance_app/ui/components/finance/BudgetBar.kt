package com.quan.finance_app.ui.components.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun BudgetBar(
    spent: Double,
    limit: Double,
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = (spent / limit).toFloat().coerceIn(0f, 1f)
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MoneyFlowTheme.typography.bodySm, color = MoneyFlowTheme.colors.textPrimary)
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MoneyFlowTheme.typography.bodySm,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MoneyFlowTheme.colors.surfaceSunken, RoundedCornerShape(MoneyFlowTheme.radii.pill))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(MoneyFlowTheme.radii.pill))
            )
        }
    }
}
