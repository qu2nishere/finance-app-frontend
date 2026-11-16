package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

private data class BackupSnapshot(val time: String, val detail: String)

private val backupHistory = listOf(
    BackupSnapshot("Hôm nay · 09:14", "1,2 MB · 42 giao dịch"),
    BackupSnapshot("21/08 · 09:02", "1,2 MB · 40 giao dịch"),
    BackupSnapshot("20/08 · 08:58", "1,1 MB · 38 giao dịch")
)

/**
 * 10e — Sao lưu & đồng bộ. Mở từ Cài đặt (3e).
 */
@Composable
fun BackupSyncScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var autoBackup by remember { mutableStateOf(true) }
    var frequencyIndex by remember { mutableIntStateOf(0) }
    var wifiOnly by remember { mutableStateOf(true) }
    var storeInCloud by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { MfScreenHeader(title = "Sao lưu & đồng bộ", onBack = onBack) }

        // Trạng thái
        item {
            MfCard(borderColor = Grow100) {
                MfRow(
                    title = "Đã đồng bộ",
                    titleWeight = FontWeight.Bold,
                    icon = Icons.Outlined.CheckCircle,
                    iconTint = Grow600,
                    iconBackground = Grow50,
                    iconSize = 38.dp,
                    subtitle = "Hôm nay · 09:14 · 42 giao dịch",
                    trailing = {
                        Surface(
                            modifier = Modifier.clickable { },
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Grow500)
                        ) {
                            Text(
                                text = "Đồng bộ",
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                                fontFamily = FontCore,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Grow600
                            )
                        }
                    }
                )
            }
        }

        // Tự động
        item { MfSectionLabel("TỰ ĐỘNG") }
        item {
            MfCard {
                MfToggleRow(
                    title = "Tự sao lưu",
                    icon = Icons.Outlined.Sync,
                    checked = autoBackup,
                    onCheckedChange = { autoBackup = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Tần suất",
                    icon = Icons.Outlined.Schedule,
                    trailing = {
                        MfSegmentedPills(
                            options = listOf("Ngày", "Tuần", "Tháng"),
                            selectedIndex = frequencyIndex,
                            onSelect = { frequencyIndex = it }
                        )
                    }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Chỉ sao lưu khi có Wi-Fi",
                    icon = Icons.Outlined.Smartphone,
                    checked = wifiOnly,
                    onCheckedChange = { wifiOnly = it }
                )
            }
        }

        // Nơi lưu
        item { MfSectionLabel("NƠI LƯU") }
        item {
            MfCard {
                MfRow(
                    title = "Tài khoản Money Flow",
                    icon = Icons.Outlined.CloudQueue,
                    subtitle = "minh@email.com · 1,2 MB",
                    onClick = { storeInCloud = true },
                    trailing = { MfRadioDot(selected = storeInCloud) }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Tệp trên máy",
                    icon = Icons.Outlined.Folder,
                    iconTint = MoneyFlowTheme.colors.textTertiary,
                    iconBackground = Ink100,
                    subtitle = "Tự tải bản .mfbak",
                    onClick = { storeInCloud = false },
                    trailing = { MfRadioDot(selected = !storeInCloud) }
                )
            }
        }

        // Lịch sử
        item { MfSectionLabel("BẢN SAO LƯU GẦN ĐÂY") }
        item {
            MfCard {
                backupHistory.forEachIndexed { index, snap ->
                    MfRow(
                        title = snap.time,
                        icon = Icons.Outlined.Inventory2,
                        iconTint = MoneyFlowTheme.colors.textTertiary,
                        iconBackground = Ink100,
                        subtitle = snap.detail,
                        trailing = {
                            Text(
                                text = "Phục hồi",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Blue600,
                                modifier = Modifier.clickable { }
                            )
                        }
                    )
                    if (index != backupHistory.lastIndex) {
                        HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileUpload,
                    contentDescription = null,
                    tint = Blue600,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Phục hồi từ tệp .mfbak",
                    fontFamily = FontCore,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Blue600
                )
            }
        }
    }
}
