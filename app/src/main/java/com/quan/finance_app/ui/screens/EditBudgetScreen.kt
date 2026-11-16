package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.*

/**
 * Màn "Hạn mức" (6b) — chỉ sửa hạn mức của đúng một danh mục chi.
 */
@Composable
fun EditBudgetScreen(
    categoryName: String = "Ăn uống",
    onBack: () -> Unit = {},
    onAddTransaction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val quickAmounts = listOf("3tr", "3,5tr", "4tr", "+500k")
    var selectedQuickIndex by remember { mutableIntStateOf(1) }
    var amountText by remember { mutableStateOf("3.500.000") }
    var cycleIndex by remember { mutableIntStateOf(1) }
    var alertThreshold by remember { mutableFloatStateOf(0.8f) }
    var rolloverEnabled by remember { mutableStateOf(true) }
    var includeSubcategories by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header: back · "Hạn mức" · Huỷ
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = MoneyFlowTheme.colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hạn mức",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontCore,
                    color = MoneyFlowTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Huỷ",
                    fontFamily = FontCore,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.clickable(onClick = onBack)
                )
            }
        }

        // 2. Danh mục đang sửa
        item {
            BudgetSurfaceCard {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .background(Alert500, RoundedCornerShape(99.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Alert50, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Storefront,
                            contentDescription = null,
                            tint = Alert500,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = categoryName,
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "2 danh mục con · chi TB 3 tháng 3,12tr",
                            fontFamily = FontCore,
                            fontSize = 11.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier.clickable { },
                        shape = RoundedCornerShape(999.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
                    ) {
                        Text(
                            text = "Đổi màu",
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                            fontFamily = FontCore,
                            fontSize = 12.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        // 3. Số tiền hạn mức + gợi ý nhanh + cảnh báo vượt chi
        item {
            BudgetSurfaceCard(borderColor = Heat200) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hạn mức mỗi tháng",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = amountText,
                            fontFamily = FontMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Heat500
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₫",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Ink600,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Heat500, RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        quickAmounts.forEachIndexed { index, label ->
                            BudgetQuickChip(
                                label = label,
                                selected = selectedQuickIndex == index,
                                modifier = Modifier.weight(1f)
                            ) {
                                selectedQuickIndex = index
                                amountText = when (label) {
                                    "3tr" -> "3.000.000"
                                    "3,5tr" -> "3.500.000"
                                    "4tr" -> "4.000.000"
                                    else -> amountText
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Alert50, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = Alert500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tháng này đã chi 3,3tr — hạn mức cũ 3,0tr bị vượt 302k",
                            fontFamily = FontCore,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Alert500
                        )
                    }
                }
            }
        }

        // 4. Chu kỳ · ngưỡng cảnh báo · dồn hạn mức · danh mục con
        item {
            BudgetSurfaceCard {
                Column {
                    // Chu kỳ
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BudgetIconTile(Icons.Outlined.CalendarMonth)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Chu kỳ",
                            fontFamily = FontCore,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            listOf("Tuần", "Tháng", "Năm").forEachIndexed { index, label ->
                                BudgetCycleChip(
                                    label = label,
                                    selected = cycleIndex == index,
                                    onClick = { cycleIndex = index }
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

                    // Ngưỡng cảnh báo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BudgetIconTile(Icons.Outlined.WarningAmber)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cảnh báo khi dùng tới",
                                fontFamily = FontCore,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                            Text(
                                text = "Thông báo đẩy + hiện trên trang chủ",
                                fontFamily = FontCore,
                                fontSize = 11.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                        Text(
                            text = "${(alertThreshold * 100).toInt()}%",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    }

                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                        Slider(
                            value = alertThreshold,
                            onValueChange = { alertThreshold = it },
                            valueRange = 0.5f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = Heat500,
                                activeTrackColor = Heat500,
                                inactiveTrackColor = Ink100
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BudgetSliderCaption("50%")
                            BudgetSliderCaption("2,8tr")
                            BudgetSliderCaption("100%")
                        }
                    }

                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

                    // Dồn phần chưa dùng
                    BudgetToggleRow(
                        icon = Icons.Outlined.Sync,
                        title = "Dồn phần chưa dùng sang tháng sau",
                        subtitle = null,
                        checked = rolloverEnabled,
                        onCheckedChange = { rolloverEnabled = it }
                    )

                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

                    // Gồm cả danh mục con
                    BudgetToggleRow(
                        icon = Icons.Outlined.Category,
                        title = "Gồm cả danh mục con",
                        subtitle = "Cà phê · Đi ăn ngoài",
                        checked = includeSubcategories,
                        onCheckedChange = { includeSubcategories = it }
                    )
                }
            }
        }

        // 5. Ghi nhanh một khoản chi
        item {
            BudgetSurfaceCard(borderColor = Heat200) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAddTransaction)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BudgetIconTile(Icons.Outlined.Add, size = 32.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Ghi một khoản chi vào “$categoryName”",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Heat600,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MoneyFlowTheme.colors.textTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 6. Bỏ hạn mức
        item {
            BudgetSurfaceCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Alert50, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = null,
                            tint = Alert500,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Bỏ hạn mức của danh mục này",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = Alert500
                    )
                }
            }
        }

        // 7. Huỷ / Lưu hạn mức
        item {
            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onBack),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
                ) {
                    Text(
                        text = "Huỷ",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = FontCore,
                        fontSize = 15.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
                Surface(
                    modifier = Modifier
                        .weight(2f)
                        .clickable(onClick = onBack),
                    shape = RoundedCornerShape(12.dp),
                    color = Heat500
                ) {
                    Text(
                        text = "Lưu hạn mức",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-components
// -------------------------------------------------------------------------

@Composable
private fun BudgetSurfaceCard(
    borderColor: Color = MoneyFlowTheme.colors.borderSubtle,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(content = content)
    }
}

@Composable
private fun BudgetIconTile(icon: ImageVector, size: androidx.compose.ui.unit.Dp = 36.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Heat50, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Heat600,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Composable
private fun BudgetQuickChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Heat50 else Color.White,
        border = BorderStroke(1.dp, if (selected) Heat500 else MoneyFlowTheme.colors.borderDefault)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),
            textAlign = TextAlign.Center,
            fontFamily = FontCore,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Heat600 else MoneyFlowTheme.colors.textSecondary
        )
    }
}

@Composable
private fun BudgetCycleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Heat500 else Color.White,
        border = BorderStroke(1.dp, if (selected) Heat500 else MoneyFlowTheme.colors.borderDefault)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
            fontFamily = FontCore,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else MoneyFlowTheme.colors.textSecondary
        )
    }
}

@Composable
private fun BudgetSliderCaption(text: String) {
    Text(
        text = text,
        fontFamily = FontCore,
        fontSize = 11.sp,
        color = MoneyFlowTheme.colors.textSecondary
    )
}

@Composable
private fun BudgetToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BudgetIconTile(icon)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = FontCore,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Heat500,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Ink200,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
