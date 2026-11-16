package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quan.finance_app.data.remote.CategoryDto
import com.quan.finance_app.ui.components.core.SkeletonBox
import com.quan.finance_app.ui.theme.MoneyFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    onDismissRequest: () -> Unit,
    categories: List<CategoryDto> = emptyList(),
    isLoading: Boolean = false,
    currentFlow: String = "EXPENSE",
    onCategorySelected: (CategoryDto) -> Unit,
    onAddCategory: ((name: String, flow: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MoneyFlowTheme.colors.surfaceCard
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = MoneyFlowTheme.spacing.gutterScreen, vertical = 12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Chọn danh mục",
                        style = MoneyFlowTheme.typography.title2,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                    Text(
                        text = if (currentFlow == "EXPENSE") "Khoản chi tiêu" else "Khoản thu nhập",
                        style = MoneyFlowTheme.typography.caption,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }

                if (onAddCategory != null) {
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        border = BorderStroke(
                            1.dp,
                            if (currentFlow == "EXPENSE") MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (currentFlow == "EXPENSE") MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Thêm danh mục",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Thêm mới",
                            style = MoneyFlowTheme.typography.caption,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading && categories.isEmpty()) {
                // Skeleton Grid khi đang tải danh mục
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(8) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            SkeletonBox(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            SkeletonBox(modifier = Modifier.size(width = 50.dp, height = 12.dp))
                        }
                    }
                }
            } else if (categories.isEmpty()) {
                // Empty state khi không có danh mục
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chưa có danh mục nào",
                        style = MoneyFlowTheme.typography.body,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentFlow == "EXPENSE") MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tạo danh mục ngay")
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isExpense = category.flow == "EXPENSE"
                        val icon = getCategoryIcon(category.name)

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
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isExpense) MoneyFlowTheme.colors.surfaceOutSoft else MoneyFlowTheme.colors.surfaceInSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = category.name,
                                    tint = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = category.name,
                                style = MoneyFlowTheme.typography.caption,
                                color = MoneyFlowTheme.colors.textPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }

                    // Tile "+ Thêm" ở cuối danh sách grid
                    if (onAddCategory != null) {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { showAddDialog = true }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MoneyFlowTheme.colors.surfacePage)
                                        .border(1.dp, MoneyFlowTheme.colors.borderSubtle, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Thêm danh mục",
                                        tint = if (currentFlow == "EXPENSE") MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Thêm mới",
                                    style = MoneyFlowTheme.typography.caption,
                                    color = MoneyFlowTheme.colors.textSecondary,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Nút viền đứt "Thêm danh mục mới" ở cuối bottom sheet
            if (onAddCategory != null && categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            BorderStroke(
                                1.dp,
                                if (currentFlow == "EXPENSE") MoneyFlowTheme.colors.flowOut.copy(alpha = 0.5f)
                                else MoneyFlowTheme.colors.flowIn.copy(alpha = 0.5f)
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            if (currentFlow == "EXPENSE") MoneyFlowTheme.colors.surfaceOutSoft.copy(alpha = 0.6f)
                            else MoneyFlowTheme.colors.surfaceInSoft.copy(alpha = 0.6f)
                        )
                        .clickable { showAddDialog = true }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = if (currentFlow == "EXPENSE") MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentFlow == "EXPENSE") "Thêm danh mục chi tiêu" else "Thêm danh mục thu nhập",
                            style = MoneyFlowTheme.typography.body,
                            fontWeight = FontWeight.SemiBold,
                            color = if (currentFlow == "EXPENSE") MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog nhập tên danh mục mới
    if (showAddDialog) {
        var newCategoryName by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = if (currentFlow == "EXPENSE") "Thêm danh mục chi" else "Thêm danh mục thu",
                    style = MoneyFlowTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = MoneyFlowTheme.colors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Nhập tên danh mục bạn muốn tạo:",
                        style = MoneyFlowTheme.typography.caption,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = {
                            newCategoryName = it
                            isError = false
                        },
                        placeholder = {
                            Text(
                                if (currentFlow == "EXPENSE") "VD: Tiền trọ, Gym, Cà phê..."
                                else "VD: Bán hàng, Tiền thưởng..."
                            )
                        },
                        isError = isError,
                        supportingText = if (isError) {
                            { Text("Tên danh mục không được để trống") }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newCategoryName.trim()
                        if (trimmed.isNotBlank()) {
                            onAddCategory?.invoke(trimmed, currentFlow)
                            showAddDialog = false
                            onDismissRequest()
                        } else {
                            isError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentFlow == "EXPENSE") MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Thêm", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Huỷ", color = MoneyFlowTheme.colors.textSecondary)
                }
            },
            containerColor = MoneyFlowTheme.colors.surfaceCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
