package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerOverlay(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MoneyFlowTheme.colors.surfaceCard
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(MoneyFlowTheme.spacing.gutterScreen)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tháng 8, 2026",
                    style = MoneyFlowTheme.typography.title3,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                Row {
                    TextButton(onClick = { /* Prev */ }) { Text("<") }
                    TextButton(onClick = { /* Next */ }) { Text(">") }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Days of week
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { day ->
                    Text(text = day, style = MoneyFlowTheme.typography.caption, color = MoneyFlowTheme.colors.textSecondary)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Days grid (mocked)
            val days = (1..31).toList()
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(days) { day ->
                    val isSelected = day == 23
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(if (isSelected) MoneyFlowTheme.colors.surfaceBrand else Color.Transparent)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            style = MoneyFlowTheme.typography.body,
                            color = if (isSelected) Color.White else MoneyFlowTheme.colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = MoneyFlowTheme.colors.borderSubtle)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time Selector Placeholder
            Text(
                text = "14:30",
                style = MoneyFlowTheme.typography.title1,
                color = MoneyFlowTheme.colors.textBrand,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
