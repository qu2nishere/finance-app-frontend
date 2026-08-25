package com.quan.finance_app.ui.components.finance

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import java.text.NumberFormat
import java.util.Locale

enum class FlowDirection { IN, OUT, NEUTRAL }

@Composable
fun AmountText(
    value: Double,
    direction: FlowDirection,
    modifier: Modifier = Modifier,
    style: TextStyle = MoneyFlowTheme.typography.amountMd
) {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    val formattedValue = formatter.format(Math.abs(value)) + "₫"
    
    val (text, color) = when (direction) {
        FlowDirection.IN -> "+$formattedValue" to MoneyFlowTheme.colors.flowIn
        FlowDirection.OUT -> "−$formattedValue" to MoneyFlowTheme.colors.flowOut
        FlowDirection.NEUTRAL -> formattedValue to MoneyFlowTheme.colors.textPrimary
    }

    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}
