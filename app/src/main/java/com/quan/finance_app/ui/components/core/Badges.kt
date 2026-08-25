package com.quan.finance_app.ui.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun CategoryBadge(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(MoneyFlowTheme.radii.sm) // 10px radius for category glyphs
            )
            .padding(horizontal = MoneyFlowTheme.spacing.space3, vertical = MoneyFlowTheme.spacing.space1)
    ) {
        Text(
            text = text,
            style = MoneyFlowTheme.typography.caption,
            color = contentColor
        )
    }
}
