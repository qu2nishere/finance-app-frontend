package com.quan.finance_app.ui.components.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun CustomKeypad(
    onKeyPressed: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keys = listOf(
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "-"),
        listOf("C", "0", ".", "+")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MoneyFlowTheme.colors.surfaceCard)
            .padding(MoneyFlowTheme.spacing.gutterScreen),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    KeypadButton(
                        text = key,
                        onClick = { onKeyPressed(key) },
                        modifier = Modifier.weight(1f),
                        isOperator = key in listOf("÷", "×", "-", "+", "C")
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(3f))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(MoneyFlowTheme.radii.control))
                    .background(MoneyFlowTheme.colors.surfaceBrand)
                    .clickable { onConfirm() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false
) {
    val bgColor = if (isOperator) MoneyFlowTheme.colors.surfacePage else MoneyFlowTheme.colors.surfaceCard
    val textColor = if (isOperator) MoneyFlowTheme.colors.textBrand else MoneyFlowTheme.colors.textPrimary
    
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(MoneyFlowTheme.radii.control))
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MoneyFlowTheme.typography.title2,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
