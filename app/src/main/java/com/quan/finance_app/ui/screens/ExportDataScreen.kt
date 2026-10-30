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
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
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
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

private data class ExportFormat(
    val name: String,
    val note: String,
    val icon: ImageVector,
    val extension: String
)

private val exportFormats = listOf(
    ExportFormat("Excel (.xlsx)", "Có công thức tổng theo danh mục", Icons.Outlined.TableChart, "xlsx"),
    ExportFormat("CSV", "Nhập được vào mọi phần mềm", Icons.Outlined.Description, "csv"),
    ExportFormat("PDF", "Báo cáo in được", Icons.AutoMirrored.Outlined.InsertDriveFile, "pdf")
)

private val exportRanges = listOf(
    "Tháng này", "Tháng trước", "3 tháng gần nhất", "Năm 2026", "Tất cả", "Tuỳ chọn…"
)

/**
 * 8h — Xuất Excel / CSV. Mở từ Cài đặt (3e).
 */
@Composable
fun ExportDataScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var rangeIndex by remember { mutableIntStateOf(0) }
    var formatIndex by remember { mutableIntStateOf(0) }
    var includeTransactions by remember { mutableStateOf(true) }
    var includeNotes by remember { mutableStateOf(false) }
    var includeLimits by remember { mutableStateOf(true) }
    var includeGoals by remember { mutableStateOf(true) }

    val fileName = "moneyflow-2026-08.${exportFormats[formatIndex].extension}"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            MfScreenHeader(title = "Xuất dữ liệu", onBack = onBack)
        }

        // Khoảng thời gian — lưới 2 cột
        item { MfSectionLabel("KHOẢNG THỜI GIAN") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                exportRanges.chunked(2).forEachIndexed { rowIndex, pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEachIndexed { colIndex, label ->
                            val index = rowIndex * 2 + colIndex
                            RangeOption(
                                label = label,
                                selected = rangeIndex == index,
                                modifier = Modifier.weight(1f),
                                onClick = { rangeIndex = index }
                            )
                        }
                    }
                }
            }
        }

        // Định dạng
        item { MfSectionLabel("ĐỊNH DẠNG") }
        item {
            MfCard {
                exportFormats.forEachIndexed { index, format ->
                    val selected = formatIndex == index
                    MfRow(
                        title = format.name,
                        icon = format.icon,
                        iconTint = if (selected) Blue600 else MoneyFlowTheme.colors.textTertiary,
                        iconBackground = if (selected) Blue50 else Ink100,
                        subtitle = format.note,
                        onClick = { formatIndex = index },
                        trailing = { RadioDot(selected) }
                    )
                    if (index != exportFormats.lastIndex) {
                        HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                    }
                }
            }
        }

        // Gồm những gì
        item { MfSectionLabel("GỒM") }
        item {
            MfCard {
                MfToggleRow(
                    title = "Giao dịch chi & thu",
                    checked = includeTransactions,
                    onCheckedChange = { includeTransactions = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Ghi chú & ảnh hoá đơn",
                    checked = includeNotes,
                    onCheckedChange = { includeNotes = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Hạn mức theo danh mục",
                    checked = includeLimits,
                    onCheckedChange = { includeLimits = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Mục tiêu tiết kiệm",
                    checked = includeGoals,
                    onCheckedChange = { includeGoals = it }
                )
            }
        }

        item {
            MfInfoBanner(
                text = "42 giao dịch · ước tính 38 KB · tên tệp $fileName",
                icon = Icons.Outlined.Info,
                background = Ink50,
                contentColor = MoneyFlowTheme.colors.textSecondary,
                borderColor = MoneyFlowTheme.colors.borderDefault
            )
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                shape = RoundedCornerShape(12.dp),
                color = Blue500
            ) {
                Text(
                    text = "Xuất và chia sẻ",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
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

@Composable
private fun RangeOption(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Blue50 else Color.White,
        border = BorderStroke(1.dp, if (selected) Blue500 else MoneyFlowTheme.colors.borderDefault)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 13.dp),
            textAlign = TextAlign.Center,
            fontFamily = FontCore,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Blue600 else MoneyFlowTheme.colors.textSecondary
        )
    }
}

@Composable
private fun RadioDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(if (selected) Blue500 else Color.White, CircleShape)
            .then(
                if (selected) Modifier
                else Modifier.border(1.5.dp, MoneyFlowTheme.colors.borderStrong, CircleShape)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}
