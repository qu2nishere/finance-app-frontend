package com.quan.finance_app.ui.components.core

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun MoneyFlowCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 4.dp, 
                shape = RoundedCornerShape(MoneyFlowTheme.radii.card), 
                ambientColor = MoneyFlowTheme.colors.surfaceBrand,
                spotColor = MoneyFlowTheme.colors.surfaceBrand
            )
            .border(
                width = 1.dp,
                color = MoneyFlowTheme.colors.borderSubtle,
                shape = RoundedCornerShape(MoneyFlowTheme.radii.card)
            ),
        shape = RoundedCornerShape(MoneyFlowTheme.radii.card),
        color = MoneyFlowTheme.colors.surfaceCard,
        content = {
            Box(modifier = Modifier.padding(MoneyFlowTheme.spacing.gutterCard)) {
                content()
            }
        }
    )
}
