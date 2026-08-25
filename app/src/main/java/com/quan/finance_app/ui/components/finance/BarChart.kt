package com.quan.finance_app.ui.components.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@Composable
fun BarChart(
    data: List<Pair<String, Float>>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val maxVal = data.maxOfOrNull { it.second } ?: 1f
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, value) ->
            val heightPercent = if (maxVal > 0) value / maxVal else 0f
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight(heightPercent)
                        .background(color, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = label, style = MoneyFlowTheme.typography.caption, color = MoneyFlowTheme.colors.textSecondary)
            }
        }
    }
}
