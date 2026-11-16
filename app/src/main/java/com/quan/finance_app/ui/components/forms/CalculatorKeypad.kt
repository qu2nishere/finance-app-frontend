package com.quan.finance_app.ui.components.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import com.quan.finance_app.ui.theme.FontCore

@Composable
fun CalculatorKeypad(
    onKeyPressed: (String) -> Unit,
    brandColor: Color,
    brandSoftColor: Color,
    modifier: Modifier = Modifier
) {
    val keys = listOf(
        listOf("C", "( )", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf(",", "0", "⌫", "=")
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        keys.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                row.forEachIndexed { colIndex, key ->
                    val isOperatorColumn = colIndex == 3
                    val isTopRow = rowIndex == 0 && !isOperatorColumn
                    val isEquals = key == "="
                    
                    val bgColor = when {
                        isEquals -> brandColor // Dynamic for =
                        isOperatorColumn -> brandSoftColor // Dynamic light color for operators
                        isTopRow -> MoneyFlowTheme.colors.surfacePage // Light gray for top row
                        else -> MoneyFlowTheme.colors.surfaceCard // White for numbers
                    }
                    
                    val textColor = when {
                        isEquals -> Color.White
                        isOperatorColumn -> brandColor // Dynamic text for operators
                        isTopRow -> brandColor
                        else -> MoneyFlowTheme.colors.textPrimary
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(bgColor)
                            .clickable { onKeyPressed(key) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            color = textColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontCore
                        )
                    }
                }
            }
        }
    }
}
