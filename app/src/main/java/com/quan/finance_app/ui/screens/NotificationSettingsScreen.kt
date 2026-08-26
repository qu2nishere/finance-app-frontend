package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 9e — Cài đặt thông báo. Mở từ bánh răng hoặc dòng cuối trong màn Thông báo (9c).
 */
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var pushEnabled by remember { mutableStateOf(true) }

    var remindLog by remember { mutableStateOf(true) }
    var remindRecurring by remember { mutableStateOf(true) }

    var alertThreshold by remember { mutableStateOf(true) }
    var alertOverspend by remember { mutableStateOf(true) }
    var alertUncategorized by remember { mutableStateOf(true) }

    var incomeReceived by remember { mutableStateOf(true) }
    var autoSaveDone by remember { mutableStateOf(true) }
    var goalBehind by remember { mutableStateOf(false) }

    var weeklyReport by remember { mutableStateOf(true) }
    var monthlyReport by remember { mutableStateOf(true) }

    var quietHours by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            MfScreenHeader(title = "Cài đặt thông báo", onBack = onBack)
        }

        // Công tắc tổng
        item {
            MfCard {
                MfToggleRow(
                    title = "Bật thông báo đẩy",
                    icon = Icons.Outlined.Notifications,
                    checked = pushEnabled,
                    onCheckedChange = { pushEnabled = it }
                )
            }
        }

        item { MfSectionLabel("NHẮC NHỞ") }
        item {
            MfCard {
                MfToggleRow(
                    title = "Nhắc ghi chi tiêu",
                    subtitle = "Hằng ngày · 21:00",
                    icon = Icons.Outlined.Edit,
                    checked = remindLog && pushEnabled,
                    onCheckedChange = { remindLog = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Giao dịch định kỳ sắp trừ",
                    subtitle = "Trước 3 ngày",
                    icon = Icons.Outlined.CalendarMonth,
                    checked = remindRecurring && pushEnabled,
                    onCheckedChange = { remindRecurring = it }
                )
            }
        }

        item { MfSectionLabel("HẠN MỨC") }
        item {
            MfCard {
                MfToggleRow(
                    title = "Khi dùng tới ngưỡng cảnh báo",
                    icon = Icons.Outlined.WarningAmber,
                    iconTint = Sun600,
                    iconBackground = Sun50,
                    checked = alertThreshold && pushEnabled,
                    onCheckedChange = { alertThreshold = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Khi vượt hạn mức",
                    icon = Icons.Outlined.NorthEast,
                    iconTint = Heat600,
                    iconBackground = Heat50,
                    checked = alertOverspend && pushEnabled,
                    onCheckedChange = { alertOverspend = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Có giao dịch chưa phân loại",
                    icon = Icons.Outlined.GridView,
                    iconTint = Heat600,
                    iconBackground = Heat50,
                    checked = alertUncategorized && pushEnabled,
                    onCheckedChange = { alertUncategorized = it }
                )
            }
        }

        item { MfSectionLabel("THU NHẬP & MỤC TIÊU") }
        item {
            MfCard {
                MfToggleRow(
                    title = "Khi nhận khoản thu",
                    icon = Icons.Outlined.SouthWest,
                    iconTint = Grow600,
                    iconBackground = Grow50,
                    checked = incomeReceived && pushEnabled,
                    onCheckedChange = { incomeReceived = it },
                    accent = Grow500
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Sau khi tự trích vào mục tiêu",
                    icon = Icons.Outlined.Savings,
                    iconTint = Grow600,
                    iconBackground = Grow50,
                    checked = autoSaveDone && pushEnabled,
                    onCheckedChange = { autoSaveDone = it },
                    accent = Grow500
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Mục tiêu chậm tiến độ",
                    icon = Icons.Outlined.CardGiftcard,
                    iconTint = Grow600,
                    iconBackground = Grow50,
                    checked = goalBehind && pushEnabled,
                    onCheckedChange = { goalBehind = it },
                    accent = Grow500
                )
            }
        }

        item { MfSectionLabel("BÁO CÁO") }
        item {
            MfCard {
                MfToggleRow(
                    title = "Báo cáo tuần",
                    subtitle = "Sáng thứ hai",
                    icon = Icons.Outlined.BarChart,
                    checked = weeklyReport && pushEnabled,
                    onCheckedChange = { weeklyReport = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Tổng kết tháng",
                    subtitle = "Ngày 1 mỗi tháng",
                    icon = Icons.Outlined.TableChart,
                    checked = monthlyReport && pushEnabled,
                    onCheckedChange = { monthlyReport = it }
                )
            }
        }

        item { MfSectionLabel("GIỜ IM LẶNG") }
        item {
            MfCard {
                MfToggleRow(
                    title = "Không thông báo",
                    subtitle = "22:00 – 07:00",
                    icon = Icons.Outlined.DarkMode,
                    iconTint = MoneyFlowTheme.colors.textSecondary,
                    iconBackground = Ink100,
                    checked = quietHours && pushEnabled,
                    onCheckedChange = { quietHours = it }
                )
            }
        }
    }
}
