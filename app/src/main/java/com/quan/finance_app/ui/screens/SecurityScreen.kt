package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 10f — Bảo mật: đổi mật khẩu & thiết bị. Mở từ 9d.
 */
@Composable
fun SecurityScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentPassword by remember { mutableStateOf("12345678") }
    var newPassword by remember { mutableStateOf("Abc@123456!") }
    var repeatPassword by remember { mutableStateOf("Abc@123456!") }
    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showRepeat by remember { mutableStateOf(false) }
    var faceIdEnabled by remember { mutableStateOf(true) }
    var autoLockIndex by remember { mutableIntStateOf(1) }

    val autoLockOptions = listOf("Ngay", "1 phút", "5 phút", "15 phút")

    val strength = remember(newPassword) {
        var s = 0
        if (newPassword.length >= 8) s++
        if (newPassword.length >= 12) s++
        if (newPassword.any { it.isDigit() }) s++
        if (newPassword.any { !it.isLetterOrDigit() }) s++
        s
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { MfScreenHeader(title = "Bảo mật", onBack = onBack) }

        item { MfSectionLabel("ĐỔI MẬT KHẨU") }
        item {
            AuthField(
                label = "MẬT KHẨU HIỆN TẠI",
                value = currentPassword,
                onValueChange = { currentPassword = it },
                icon = Icons.Outlined.Lock,
                isPassword = !showCurrent,
                trailing = {
                    EyeToggle(showCurrent) { showCurrent = !showCurrent }
                }
            )
        }
        item {
            AuthField(
                label = "MẬT KHẨU MỚI",
                value = newPassword,
                onValueChange = { newPassword = it },
                icon = Icons.Outlined.Lock,
                focused = true,
                isPassword = !showNew,
                trailing = {
                    EyeToggle(showNew) { showNew = !showNew }
                }
            )
        }
        item {
            AuthField(
                label = "NHẬP LẠI MẬT KHẨU MỚI",
                value = repeatPassword,
                onValueChange = { repeatPassword = it },
                icon = Icons.Outlined.Lock,
                isPassword = !showRepeat,
                trailing = {
                    EyeToggle(showRepeat) { showRepeat = !showRepeat }
                }
            )
        }

        // Độ mạnh
        item {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(
                                    if (index < strength) Grow500 else Ink200,
                                    RoundedCornerShape(99.dp)
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when {
                        repeatPassword != newPassword -> "Hai mật khẩu chưa khớp"
                        strength >= 4 -> "Rất mạnh · ${newPassword.length} ký tự, có số và ký tự đặc biệt"
                        strength == 3 -> "Mạnh · nên thêm ký tự đặc biệt"
                        else -> "Yếu · cần ít nhất 8 ký tự và có số"
                    },
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    color = when {
                        repeatPassword != newPassword -> Alert500
                        strength >= 3 -> Grow600
                        else -> MoneyFlowTheme.colors.textSecondary
                    }
                )
            }
        }

        item {
            AuthPrimaryButton(text = "Đổi mật khẩu", onClick = onBack)
        }

        // Mở app
        item { MfSectionLabel("MỞ APP") }
        item {
            MfCard {
                MfToggleRow(
                    title = "Face ID",
                    icon = Icons.Outlined.Face,
                    checked = faceIdEnabled,
                    onCheckedChange = { faceIdEnabled = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Mã PIN 6 số",
                    icon = Icons.Outlined.Lock,
                    trailing = {
                        Text(
                            text = "Đổi PIN",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Blue600,
                            modifier = Modifier.clickable { }
                        )
                    }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Tự khoá sau",
                    icon = Icons.Outlined.Schedule,
                    trailing = {
                        MfEditableChip(
                            value = autoLockOptions[autoLockIndex],
                            accent = Blue600,
                            onClick = {
                                autoLockIndex = (autoLockIndex + 1) % autoLockOptions.size
                            }
                        )
                    }
                )
            }
        }

        // Thiết bị
        item { MfSectionLabel("THIẾT BỊ ĐANG ĐĂNG NHẬP") }
        item {
            MfCard {
                MfRow(
                    title = "iPhone 14 · thiết bị này",
                    titleWeight = FontWeight.SemiBold,
                    icon = Icons.Outlined.Smartphone,
                    iconTint = Grow600,
                    iconBackground = Grow50,
                    subtitle = "TP.HCM · đang hoạt động"
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "MacBook Air · Chrome",
                    icon = Icons.Outlined.Computer,
                    iconTint = MoneyFlowTheme.colors.textTertiary,
                    iconBackground = Ink100,
                    subtitle = "TP.HCM · 2 ngày trước",
                    trailing = {
                        Text(
                            text = "Đăng xuất",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Alert500,
                            modifier = Modifier.clickable { }
                        )
                    }
                )
            }
        }

        item {
            MfCard {
                MfRow(
                    title = "Đăng xuất mọi thiết bị khác",
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    iconTint = Alert500,
                    iconBackground = Alert50,
                    titleColor = Alert500,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun EyeToggle(visible: Boolean, onClick: () -> Unit) {
    Icon(
        imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
        contentDescription = "Hiện mật khẩu",
        tint = MoneyFlowTheme.colors.textTertiary,
        modifier = Modifier
            .size(18.dp)
            .clickable(onClick = onClick)
    )
}
