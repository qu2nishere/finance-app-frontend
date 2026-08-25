package com.quan.finance_app.ui.components.core

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(MoneyFlowTheme.spacing.tapMin),
        shape = RoundedCornerShape(MoneyFlowTheme.radii.control),
        colors = ButtonDefaults.buttonColors(
            containerColor = MoneyFlowTheme.colors.surfaceBrand,
            contentColor = MoneyFlowTheme.colors.textInverse,
            disabledContainerColor = MoneyFlowTheme.colors.surfaceBrand.copy(alpha = 0.45f),
            disabledContentColor = MoneyFlowTheme.colors.textInverse.copy(alpha = 0.45f)
        )
    ) {
        Text(text = text, style = MoneyFlowTheme.typography.body.copy(fontWeight = FontWeight.SemiBold))
    }
}
