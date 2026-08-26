package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
 * Màn "Danh mục thu" (7b) — sửa danh mục thu. Không có hạn mức, chỉ phân loại
 * và cấu hình cách nhận tiền.
 */
@Composable
fun EditIncomeCategoryScreen(
    categoryName: String = "Lương",
    onBack: () -> Unit = {},
    onAddTransaction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val iconOptions = listOf(
        Icons.Outlined.Payments,
        Icons.Outlined.AccountBalanceWallet,
        Icons.Outlined.Savings,
        Icons.Outlined.CardGiftcard
    )
    val colorOptions = listOf(Grow500, Blue500, Sun500, CatBills)

    var selectedIconIndex by remember { mutableIntStateOf(0) }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var monthlyExpectedEnabled by remember { mutableStateOf(true) }
    var lateReminderEnabled by remember { mutableStateOf(true) }
    var autoSaveEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header: back · "Danh mục thu" · Huỷ
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
                    text = "Danh mục thu",
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
            IncomeSurfaceCard {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .background(colorOptions[selectedColorIndex], RoundedCornerShape(99.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Grow50, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconOptions[selectedIconIndex],
                            contentDescription = null,
                            tint = Grow600,
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
                            text = "Đã nhận 6/6 tháng · TB 18,0tr",
                            fontFamily = FontCore,
                            fontSize = 11.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        // 3. Tên · icon · màu nhãn
        item {
            IncomeSurfaceCard {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IncomeIconTile(Icons.Outlined.Edit)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tên danh mục",
                            fontFamily = FontCore,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = categoryName,
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    }

                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

                    // Icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IncomeIconTile(Icons.Outlined.GridView)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Icon",
                            fontFamily = FontCore,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            iconOptions.forEachIndexed { index, icon ->
                                val selected = selectedIconIndex == index
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            if (selected) Grow50 else Ink100,
                                            RoundedCornerShape(9.dp)
                                        )
                                        .then(
                                            if (selected) Modifier.border(
                                                1.5.dp,
                                                Grow500,
                                                RoundedCornerShape(9.dp)
                                            ) else Modifier
                                        )
                                        .clickable { selectedIconIndex = index },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (selected) Grow600 else MoneyFlowTheme.colors.textTertiary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

                    // Màu nhãn
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IncomeIconTile(Icons.Outlined.LocalOffer)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Màu nhãn",
                                fontFamily = FontCore,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                            Text(
                                text = "Hiện trước tên trong danh sách",
                                fontFamily = FontCore,
                                fontSize = 11.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            colorOptions.forEachIndexed { index, color ->
                                val selected = selectedColorIndex == index
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .then(
                                            if (selected) Modifier.border(2.dp, color, CircleShape)
                                            else Modifier
                                        )
                                        .padding(if (selected) 4.dp else 3.dp)
                                        .background(color, CircleShape)
                                        .clickable { selectedColorIndex = index }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Nhận tiền
        item {
            Text(
                text = "NHẬN TIỀN",
                fontFamily = FontCore,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                color = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            IncomeSurfaceCard {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IncomeIconTile(Icons.Outlined.AccountBalanceWallet)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Ví nhận mặc định",
                            fontFamily = FontCore,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Vietcombank ●●42",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    }

                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

                    IncomeToggleRow(
                        icon = Icons.Outlined.Sync,
                        title = "Dự kiến nhận hằng tháng",
                        subtitle = "Ngày 20 · 18.000.000₫",
                        checked = monthlyExpectedEnabled,
                        onCheckedChange = { monthlyExpectedEnabled = it }
                    )

                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

                    IncomeToggleRow(
                        icon = Icons.Outlined.Notifications,
                        title = "Nhắc nếu quá 2 ngày chưa nhận",
                        subtitle = null,
                        checked = lateReminderEnabled,
                        onCheckedChange = { lateReminderEnabled = it }
                    )

                    HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

                    IncomeToggleRow(
                        icon = Icons.Outlined.Savings,
                        title = "Tự trích vào mục tiêu",
                        subtitle = "20% → Quỹ dự phòng",
                        checked = autoSaveEnabled,
                        onCheckedChange = { autoSaveEnabled = it }
                    )
                }
            }
        }

        // 5. Nhắc: không có hạn mức
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Ink50,
                border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MoneyFlowTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = "Không có hạn mức cho danh mục thu.",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }
        }

        // 6. Ghi nhanh một khoản thu
        item {
            IncomeSurfaceCard(borderColor = Grow100) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAddTransaction)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IncomeIconTile(Icons.Outlined.Add, size = 32.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Ghi một khoản thu vào “$categoryName”",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Grow600,
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

        // 7. Xoá danh mục
        item {
            IncomeSurfaceCard {
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
                        text = "Xoá danh mục (chuyển giao dịch sang “Khác”)",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        color = Alert500
                    )
                }
            }
        }

        // 8. Huỷ / Lưu danh mục
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
                    color = Grow500
                ) {
                    Text(
                        text = "Lưu danh mục",
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
private fun IncomeSurfaceCard(
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
private fun IncomeIconTile(icon: ImageVector, size: androidx.compose.ui.unit.Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Grow50, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Grow600,
            modifier = Modifier.size(size * 0.53f)
        )
    }
}

@Composable
private fun IncomeToggleRow(
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
        IncomeIconTile(icon)
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
                checkedTrackColor = Grow500,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Ink200,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
