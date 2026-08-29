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
import androidx.compose.material.icons.filled.Add
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
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

@Composable
fun EditGoalScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var goalName by remember { mutableStateOf("Du lịch Đà Lạt") }
    var targetAmount by remember { mutableStateOf("10.000.000₫") }
    var targetDate by remember { mutableStateOf("31/12/2026") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var isAutoDeductEnabled by remember { mutableStateOf(true) }
    // turn 10: số tiền trích mỗi tháng sửa được ngay tại hàng
    var autoDeductAmount by remember { mutableLongStateOf(1_400_000L) }
    var isShowOnHomeEnabled by remember { mutableStateOf(true) }
    var isReminderEnabled by remember { mutableStateOf(true) }

    val colorOptions = listOf(
        Grow500, // Green
        Heat500, // Orange
        Sun500,  // Yellow
        Blue500  // Blue
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Header: Back + "Sửa mục tiêu" + "Cài đặt >"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MoneyFlowTheme.colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sửa mục tiêu",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontCore,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Text(
                    text = "Cài đặt >",
                    fontFamily = FontCore,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.clickable { }
                )
            }
        }

        // 2. Goal Preview Header Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Color(0xFFF0F3F8))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { 0.45f },
                            modifier = Modifier.fillMaxSize(),
                            color = Grow500,
                            trackColor = Color(0xFFF0F2F6),
                            strokeWidth = 6.dp
                        )
                        Text(
                            text = "45%",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = goalName,
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "4.500.000₫",
                                fontFamily = FontMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Grow500
                            )
                            Text(
                                text = " / 10.000.000₫",
                                fontFamily = FontMono,
                                fontSize = 13.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Xem trước thẻ hiển thị ở trang chủ",
                            fontFamily = FontCore,
                            fontSize = 11.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        // 3. Form Fields Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Color(0xFFF0F3F8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Row 1: Tên mục tiêu
                    FormRow(
                        icon = Icons.Outlined.Edit,
                        label = "Tên mục tiêu",
                        trailingContent = {
                            Text(
                                text = goalName,
                                fontFamily = FontCore,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF4F6F9), modifier = Modifier.padding(vertical = 12.dp))

                    // Row 2: Icon & màu nhãn
                    FormRow(
                        icon = Icons.Outlined.CardGiftcard,
                        label = "Icon & màu nhãn",
                        trailingContent = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                colorOptions.forEachIndexed { index, color ->
                                    val isSelected = selectedColorIndex == index
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(color, CircleShape)
                                            .then(
                                                if (isSelected) Modifier.border(2.dp, Color.White, CircleShape).border(3.5.dp, color, CircleShape)
                                                else Modifier
                                            )
                                            .clickable { selectedColorIndex = index }
                                    )
                                }
                            }
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF4F6F9), modifier = Modifier.padding(vertical = 12.dp))

                    // Row 3: Số tiền mục tiêu
                    FormRow(
                        icon = Icons.Outlined.MonetizationOn,
                        label = "Số tiền mục tiêu",
                        trailingContent = {
                            Text(
                                text = targetAmount,
                                fontFamily = FontMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Heat500
                            )
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF4F6F9), modifier = Modifier.padding(vertical = 12.dp))

                    // Row 4: Ngày hoàn thành
                    FormRow(
                        icon = Icons.Outlined.CalendarToday,
                        label = "Ngày hoàn thành",
                        trailingContent = {
                            Text(
                                text = targetDate,
                                fontFamily = FontMono,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF4F6F9), modifier = Modifier.padding(vertical = 12.dp))

                    // Row 5: Ví tích luỹ
                    FormRow(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        label = "Ví tích luỹ",
                        trailingContent = {
                            Text(
                                text = "Vietcombank ••42",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                        }
                    )
                }
            }
        }

        // 4. Section "TỰ ĐỘNG & HIỂN THỊ"
        item {
            Text(
                text = "TỰ ĐỘNG & HIỂN THỊ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontCore,
                color = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 5. Automation Settings Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Color(0xFFF0F3F8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Toggle 1: Tự trích mỗi tháng
                    SettingToggleRow(
                        icon = Icons.Outlined.Sync,
                        title = "Tự trích mỗi tháng",
                        subtitle = "Ngày 25 · sửa số tiền hoặc %",
                        isChecked = isAutoDeductEnabled,
                        onCheckedChange = { isAutoDeductEnabled = it },
                        trailing = {
                            MfEditableChip(
                                value = formatVnd(autoDeductAmount),
                                accent = Grow600,
                                onClick = { autoDeductAmount += 100_000L }
                            )
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF4F6F9), modifier = Modifier.padding(vertical = 12.dp))

                    // Toggle 2: Hiện ở góc trang chủ
                    SettingToggleRow(
                        icon = Icons.Outlined.Home,
                        title = "Hiện ở góc trang chủ",
                        subtitle = "Thẻ nhỏ góc trên phải",
                        isChecked = isShowOnHomeEnabled,
                        onCheckedChange = { isShowOnHomeEnabled = it }
                    )

                    HorizontalDivider(color = Color(0xFFF4F6F9), modifier = Modifier.padding(vertical = 12.dp))

                    // Toggle 3: Nhắc nạp nếu chậm tiến độ
                    SettingToggleRow(
                        icon = Icons.Outlined.NotificationsNone,
                        title = "Nhắc nạp nếu chậm tiến độ",
                        subtitle = null,
                        isChecked = isReminderEnabled,
                        onCheckedChange = { isReminderEnabled = it }
                    )

                    HorizontalDivider(color = Color(0xFFF4F6F9), modifier = Modifier.padding(vertical = 12.dp))

                    // Row 4: Thứ tự ưu tiên
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFFEEF4FF), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FormatListNumbered,
                                    contentDescription = "Thứ tự ưu tiên",
                                    tint = Blue500,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Thứ tự ưu tiên",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#1 / 3",
                                fontFamily = FontMono,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                contentDescription = "Xem",
                                tint = MoneyFlowTheme.colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 6. Action Buttons: Nạp thêm + Xoá mục tiêu
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Button 1: Nạp thêm tiền vào mục tiêu
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable { },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFC9F5DD))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Nạp thêm",
                                tint = Grow500,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Nạp thêm tiền vào mục tiêu",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Grow500
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = "Tiếp tục",
                            tint = Grow500,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Button 2: Xoá mục tiêu (giữ lại tiền đã nạp)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable { onBack() },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFFFE0E5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Xoá",
                            tint = Alert500,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Xoá mục tiêu (giữ lại tiền đã nạp)",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Alert500
                        )
                    }
                }
            }
        }

        // 7. Bottom Action Buttons: Huỷ & Lưu mục tiêu
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFDCE2EC)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Ink600
                    )
                ) {
                    Text(
                        text = "Huỷ",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue500,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Lưu mục tiêu",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-components for EditGoalScreen
// -------------------------------------------------------------------------

@Composable
fun FormRow(
    icon: ImageVector,
    label: String,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(0xFFEEF4FF), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Blue500,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontFamily = FontCore,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }

        trailingContent()
    }
}

@Composable
fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(0xFFEEF4FF), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Blue500,
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

        if (trailing != null) {
            Spacer(modifier = Modifier.width(10.dp))
            trailing()
            Spacer(modifier = Modifier.width(8.dp))
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Blue500,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFDCE2EC),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
