package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

private data class Currency(val code: String, val name: String, val decimals: String)

private val currencies = listOf(
    Currency("VND ₫", "Đồng Việt Nam", "Không dùng số thập phân"),
    Currency("USD $", "Đô la Mỹ", "2 số thập phân"),
    Currency("EUR €", "Euro", "2 số thập phân"),
    Currency("JPY ¥", "Yên Nhật", "không thập phân"),
    Currency("KRW ₩", "Won Hàn", "không thập phân")
)

/**
 * 10d — Đơn vị tiền tệ & định dạng số. Mở từ Cài đặt (3e).
 */
@Composable
fun CurrencySettingsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var selectedCode by remember { mutableStateOf("VND ₫") }
    var separatorIndex by remember { mutableIntStateOf(0) }
    var symbolPositionIndex by remember { mutableIntStateOf(0) }
    var shortenLargeNumbers by remember { mutableStateOf(true) }
    var convertOldData by remember { mutableStateOf(false) }

    val current = currencies.first { it.code == selectedCode }
    val others = currencies
        .filter { it.code != selectedCode }
        .filter {
            query.isBlank() ||
                it.code.contains(query.trim(), true) ||
                it.name.contains(query.trim(), true)
        }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { MfScreenHeader(title = "Đơn vị tiền tệ", onBack = onBack) }

        // Ô tìm
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MoneyFlowTheme.colors.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Tìm tiền tệ…",
                                fontFamily = FontCore,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textTertiary
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = FontCore,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            ),
                            cursorBrush = SolidColor(MoneyFlowTheme.colors.textPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item { MfSectionLabel("ĐANG DÙNG") }
        item {
            MfCard(borderColor = Blue200) {
                MfRow(
                    title = "${current.code} · ${current.name}",
                    icon = Icons.Outlined.Payments,
                    subtitle = current.decimals,
                    trailing = { MfRadioDot(selected = true) }
                )
            }
        }

        if (others.isNotEmpty()) {
            item { MfSectionLabel("PHỔ BIẾN") }
            item {
                MfCard {
                    others.forEachIndexed { index, c ->
                        MfRow(
                            title = "${c.code} · ${c.name}",
                            icon = Icons.Outlined.Payments,
                            iconTint = MoneyFlowTheme.colors.textTertiary,
                            iconBackground = Ink100,
                            subtitle = c.decimals,
                            onClick = { selectedCode = c.code },
                            trailing = {
                                MfRadioDot(selected = false, onClick = { selectedCode = c.code })
                            }
                        )
                        if (index != others.lastIndex) {
                            HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                        }
                    }
                }
            }
        }

        // Định dạng số
        item { MfSectionLabel("ĐỊNH DẠNG SỐ") }
        item {
            MfCard {
                MfRow(
                    title = "Dấu phân cách",
                    trailing = {
                        MfSegmentedPills(
                            options = listOf("1.234.567", "1,234,567"),
                            selectedIndex = separatorIndex,
                            onSelect = { separatorIndex = it }
                        )
                    }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Vị trí ký hiệu",
                    trailing = {
                        MfSegmentedPills(
                            options = listOf("1.000₫", "₫1.000"),
                            selectedIndex = symbolPositionIndex,
                            onSelect = { symbolPositionIndex = it }
                        )
                    }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Rút gọn số lớn",
                    subtitle = "12.480.000₫ → 12,5tr",
                    checked = shortenLargeNumbers,
                    onCheckedChange = { shortenLargeNumbers = it }
                )
            }
        }

        item {
            MfCard {
                MfToggleRow(
                    title = "Đổi tiền tệ cho dữ liệu cũ",
                    subtitle = "Quy đổi theo tỷ giá ngày giao dịch",
                    icon = Icons.Outlined.Sync,
                    checked = convertOldData,
                    onCheckedChange = { convertOldData = it }
                )
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onBack),
                shape = RoundedCornerShape(12.dp),
                color = Blue500
            ) {
                Text(
                    text = "Lưu",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = FontCore,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}
