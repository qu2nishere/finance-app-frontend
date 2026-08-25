package com.quan.finance_app.ui.components.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun TransactionRow(
    title: String,
    date: String,
    amount: Double,
    direction: FlowDirection,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = MoneyFlowTheme.spacing.gutterScreen),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bgColor = if (direction == FlowDirection.IN) MoneyFlowTheme.colors.surfaceInSoft else MoneyFlowTheme.colors.surfaceOutSoft
        val iconColor = if (direction == FlowDirection.IN) MoneyFlowTheme.colors.flowIn else MoneyFlowTheme.colors.flowOut
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = bgColor, shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MoneyFlowTheme.typography.body, color = MoneyFlowTheme.colors.textPrimary)
            Text(text = date, style = MoneyFlowTheme.typography.caption, color = MoneyFlowTheme.colors.textSecondary)
        }
        
        AmountText(
            value = amount,
            direction = direction,
            style = MoneyFlowTheme.typography.amountSm
        )
    }
}
