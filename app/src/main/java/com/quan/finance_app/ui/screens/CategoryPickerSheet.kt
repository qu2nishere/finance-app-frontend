package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    onDismissRequest: () -> Unit,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val categories = listOf("Ăn uống", "Di chuyển", "Mua sắm", "Hoá đơn", "Sức khỏe", "Giải trí", "Nhà cửa", "Khác")

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
            Text(
                text = "Chọn danh mục",
                style = MoneyFlowTheme.typography.title2,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(categories) { category ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { 
                            onCategorySelected(category)
                            onDismissRequest() 
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MoneyFlowTheme.colors.surfaceOutSoft, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MoneyFlowTheme.colors.flowOut)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = category,
                            style = MoneyFlowTheme.typography.caption,
                            color = MoneyFlowTheme.colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /* Handle Add */ }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MoneyFlowTheme.colors.surfacePage, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MoneyFlowTheme.colors.textSecondary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Thêm mới",
                            style = MoneyFlowTheme.typography.caption,
                            color = MoneyFlowTheme.colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
