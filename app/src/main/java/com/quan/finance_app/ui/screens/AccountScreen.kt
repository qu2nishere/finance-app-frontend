package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 9d — Tài khoản của tôi. Mở từ thẻ hồ sơ trong Cài đặt (3e).
 */
@Composable
fun AccountScreen(
    onBack: () -> Unit = {},
    onOpenSecurity: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var faceIdEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            MfScreenHeader(
                title = "Tài khoản",
                onBack = onBack,
                actionText = "Lưu",
                onAction = onBack
            )
        }

        // Thẻ hồ sơ
        item {
            MfCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(60.dp)) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Blue100, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = Blue600,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        // Huy hiệu đổi ảnh
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(23.dp)
                                .background(Color.White, CircleShape)
                                .padding(2.dp)
                                .background(Blue500, CircleShape)
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = "Đổi ảnh",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nguyễn Minh",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "minh@email.com",
                            fontFamily = FontCore,
                            fontSize = 12.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(999.dp), color = Ink50) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.VerifiedUser,
                                    contentDescription = null,
                                    tint = MoneyFlowTheme.colors.textSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Đã xác thực email",
                                    fontFamily = FontCore,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MoneyFlowTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Thông tin
        item { MfSectionLabel("THÔNG TIN") }
        item {
            MfCard {
                MfRow(
                    title = "Tên hiển thị",
                    icon = Icons.Outlined.Person,
                    iconSize = 30.dp,
                    showChevron = true,
                    onClick = { },
                    trailing = { AccountValue("Nguyễn Minh") }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Email",
                    icon = Icons.Outlined.Email,
                    iconSize = 30.dp,
                    showChevron = true,
                    onClick = { },
                    trailing = { AccountValue("minh@email.com") }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Số điện thoại",
                    icon = Icons.Outlined.Smartphone,
                    iconSize = 30.dp,
                    showChevron = true,
                    onClick = { },
                    trailing = {
                        Text(
                            text = "Chưa thêm",
                            fontFamily = FontCore,
                            fontSize = 13.sp,
                            color = MoneyFlowTheme.colors.textTertiary
                        )
                    }
                )
            }
        }

        // Bảo mật
        item { MfSectionLabel("BẢO MẬT") }
        item {
            MfCard {
                MfRow(
                    title = "Đổi mật khẩu",
                    icon = Icons.Outlined.Lock,
                    iconSize = 30.dp,
                    subtitle = "Đổi lần cuối 3 tháng trước",
                    showChevron = true,
                    onClick = onOpenSecurity
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfToggleRow(
                    title = "Mở app bằng Face ID",
                    icon = Icons.Outlined.Face,
                    checked = faceIdEnabled,
                    onCheckedChange = { faceIdEnabled = it }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Thiết bị đang đăng nhập",
                    icon = Icons.Outlined.Computer,
                    iconSize = 30.dp,
                    showChevron = true,
                    onClick = onOpenSecurity,
                    trailing = {
                        Text(
                            text = "2",
                            fontFamily = FontCore,
                            fontSize = 13.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                )
            }
        }

        // Gói
        item { MfSectionLabel("GÓI") }
        item {
            MfCard(borderColor = Blue200) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MfIconTile(icon = Icons.Outlined.AutoAwesome, size = 34.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gói miễn phí",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "3 ví · 2 mục tiêu · xuất Excel 1 lần/tháng",
                                fontFamily = FontCore,
                                fontSize = 11.sp,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { },
                        shape = RoundedCornerShape(12.dp),
                        color = Blue500
                    ) {
                        Text(
                            text = "Nâng cấp Money Flow Plus",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            textAlign = TextAlign.Center,
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Đăng xuất / xoá tài khoản
        item {
            MfCard {
                MfRow(
                    title = "Đăng xuất",
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    iconTint = MoneyFlowTheme.colors.textSecondary,
                    iconBackground = Ink100,
                    iconSize = 30.dp,
                    onClick = onLogout
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Xoá tài khoản",
                    icon = Icons.Outlined.DeleteOutline,
                    iconTint = Alert500,
                    iconBackground = Alert50,
                    iconSize = 30.dp,
                    titleColor = Alert500,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun AccountValue(text: String) {
    Text(
        text = text,
        fontFamily = FontCore,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        color = MoneyFlowTheme.colors.textPrimary
    )
}
