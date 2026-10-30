package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.MfSectionLabel
import com.quan.finance_app.ui.components.core.MfTabHeader
import com.quan.finance_app.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateToCategories: () -> Unit = {},
    onNavigateToSavingsGoals: () -> Unit = {},
    onNavigateToWallets: () -> Unit = {},
    onNavigateToRecurring: () -> Unit = {},
    onNavigateToExport: () -> Unit = {},
    onNavigateToAccount: () -> Unit = {},
    onNavigateToCurrency: () -> Unit = {},
    onNavigateToBackup: () -> Unit = {},
    onDeleteAllData: () -> Unit = {},
    onNavigateToAutoSave: () -> Unit = {},
    onNavigateToNotificationSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var isBiometricLockEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Ink50),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header: "Cài đặt"
        item {
            Box(modifier = Modifier.padding(bottom = 4.dp)) {
                MfTabHeader(title = "Cài đặt")
            }
        }

        // 2. User Profile Card
        item {
            UserProfileCard(
                name = "Nguyễn Minh",
                email = "minh@email.com",
                onClick = onNavigateToAccount
            )
        }

        // 3. Section 1: TIỀN & DANH MỤC
        item {
            SectionHeader(title = "TIỀN & DANH MỤC")
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Ink100)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    // Danh mục và Hạn mức
                    SettingNavigationRow(
                        icon = Icons.Outlined.GridView,
                        title = "Danh mục và Hạn mức",
                        trailingText = "14 · 5 hạn mức",
                        onClick = onNavigateToCategories
                    )

                    HorizontalDivider(color = Ink100)

                    // Ví & tài khoản
                    SettingNavigationRow(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = "Ví & tài khoản",
                        trailingText = "3",
                        onClick = onNavigateToWallets
                    )

                    HorizontalDivider(color = Ink100)

                    // Giao dịch định kỳ
                    SettingNavigationRow(
                        icon = Icons.Outlined.Sync,
                        title = "Giao dịch định kỳ",
                        trailingText = null,
                        onClick = onNavigateToRecurring
                    )

                    HorizontalDivider(color = Ink100)

                    // Mục tiêu tiết kiệm
                    SettingNavigationRow(
                        icon = Icons.Outlined.Savings,
                        title = "Mục tiêu tiết kiệm",
                        trailingText = "3 đang chạy",
                        onClick = onNavigateToSavingsGoals
                    )

                    HorizontalDivider(color = Ink100)

                    // Tự trích thu nhập
                    SettingNavigationRow(
                        icon = Icons.Outlined.Percent,
                        title = "Tự trích thu nhập",
                        subtitle = "20% → Quỹ dự phòng",
                        trailingText = "20%",
                        trailingTextColor = Grow600,
                        onClick = onNavigateToAutoSave
                    )

                    HorizontalDivider(color = Ink100)

                    // Đơn vị tiền tệ
                    SettingNavigationRow(
                        icon = Icons.Outlined.Payments,
                        title = "Đơn vị tiền tệ",
                        trailingText = "VND ₫",
                        onClick = onNavigateToCurrency
                    )
                }
            }
        }

        // 4. Section 2: ỨNG DỤNG
        item {
            SectionHeader(title = "ỨNG DỤNG")
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Ink100)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    // Thông báo & nhắc nhở — gom mọi loại thông báo vào màn riêng (9e)
                    SettingNavigationRow(
                        icon = Icons.Outlined.Notifications,
                        title = "Thông báo & nhắc nhở",
                        subtitle = "Nhắc ghi chi tiêu 21:00 · 8 loại bật",
                        trailingText = "8 bật",
                        onClick = onNavigateToNotificationSettings
                    )

                    HorizontalDivider(color = Ink100)

                    // Chế độ tối
                    SettingToggleItem(
                        icon = Icons.Outlined.Visibility,
                        title = "Chế độ tối",
                        subtitle = null,
                        isChecked = isDarkModeEnabled,
                        onCheckedChange = { isDarkModeEnabled = it }
                    )

                    HorizontalDivider(color = Ink100)

                    // Khoá bằng Face ID / PIN
                    SettingToggleItem(
                        icon = Icons.Outlined.Lock,
                        title = "Khoá bằng Face ID / PIN",
                        subtitle = null,
                        isChecked = isBiometricLockEnabled,
                        onCheckedChange = { isBiometricLockEnabled = it }
                    )

                    HorizontalDivider(color = Ink100)

                    // Ngôn ngữ
                    SettingNavigationRow(
                        icon = Icons.Outlined.Language,
                        title = "Ngôn ngữ",
                        trailingText = "Tiếng Việt",
                        onClick = {}
                    )
                }
            }
        }

        // 5. Section 3: DỮ LIỆU
        item {
            SectionHeader(title = "DỮ LIỆU")
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Ink100)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    // Xuất Excel / CSV
                    SettingNavigationRow(
                        icon = Icons.Outlined.FileDownload,
                        title = "Xuất Excel / CSV",
                        trailingText = null,
                        onClick = onNavigateToExport
                    )

                    HorizontalDivider(color = Ink100)

                    // Sao lưu & đồng bộ
                    SettingNavigationRow(
                        icon = Icons.Outlined.CloudSync,
                        title = "Sao lưu & đồng bộ",
                        trailingText = "Hôm nay",
                        trailingTextColor = Grow500,
                        onClick = onNavigateToBackup
                    )

                    HorizontalDivider(color = Ink100)

                    // Xoá toàn bộ dữ liệu
                    SettingNavigationRow(
                        icon = Icons.Outlined.DeleteOutline,
                        iconBg = Alert50,
                        iconTint = Alert500,
                        title = "Xoá toàn bộ dữ liệu",
                        titleColor = Alert500,
                        trailingText = null,
                        onClick = onDeleteAllData
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
fun SectionHeader(title: String) {
    Box(modifier = Modifier.padding(top = 6.dp)) {
        MfSectionLabel(text = title)
    }
}

@Composable
fun UserProfileCard(
    name: String,
    email: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Ink100)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Heat50, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Avatar",
                    tint = Heat500,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontFamily = FontCore,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = email,
                    fontFamily = FontCore,
                    fontSize = 12.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Chi tiết",
                tint = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingNavigationRow(
    icon: ImageVector,
    iconBg: Color = Blue50,
    iconTint: Color = Blue500,
    title: String,
    subtitle: String? = null,
    titleColor: Color = MoneyFlowTheme.colors.textPrimary,
    trailingText: String? = null,
    trailingTextColor: Color = MoneyFlowTheme.colors.textSecondary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontFamily = FontCore,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = titleColor
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    color = trailingTextColor
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Mở",
                tint = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SettingToggleItem(
    icon: ImageVector,
    iconBg: Color = Blue50,
    iconTint: Color = Blue500,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontFamily = FontCore,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Blue500,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Ink200,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
