package com.quan.finance_app.ui.components.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.theme.GradientHero
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun BalanceCard(
    balance: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MoneyFlowTheme.spacing.gutterScreen)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(MoneyFlowTheme.radii.xl),
                ambientColor = MoneyFlowTheme.colors.surfaceBrand,
                spotColor = MoneyFlowTheme.colors.surfaceBrand
            )
            .background(
                brush = Brush.linearGradient(colors = GradientHero),
                shape = RoundedCornerShape(MoneyFlowTheme.radii.xl)
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "TỔNG SỐ DƯ",
                style = MoneyFlowTheme.typography.micro,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            AmountText(
                value = balance,
                direction = FlowDirection.NEUTRAL,
                style = MoneyFlowTheme.typography.amountHero.copy(color = Color.White)
            )
        }
    }
}
