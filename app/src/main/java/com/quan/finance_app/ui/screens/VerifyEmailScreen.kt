package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import kotlinx.coroutines.delay

/**
 * 10a — Quên mật khẩu → nhập mã 6 số. Mở từ "Quên mật khẩu?" ở 9a.
 */
@Composable
fun VerifyEmailScreen(
    email: String = "minh@email.com",
    onBack: () -> Unit = {},
    onVerified: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var code by remember { mutableStateOf("4281") }
    var secondsLeft by remember { mutableIntStateOf(161) }

    // Đồng hồ đếm ngược cho nút gửi lại mã
    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = MoneyFlowTheme.colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Xác minh email",
                fontFamily = FontCore,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        MfIconTile(icon = Icons.Outlined.Email, size = 48.dp)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Nhập mã 6 số",
            fontFamily = FontCore,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MoneyFlowTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Mã đã gửi tới $email. Mã có hiệu lực 10 phút.",
            fontFamily = FontCore,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = MoneyFlowTheme.colors.textSecondary
        )

        // 6 ô mã
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(6) { index ->
                val filled = index < code.length
                val isCursor = index == code.length
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable {
                            // bấm để xoá lùi một ký tự, giúp thử nhanh trên wireframe
                            if (code.isNotEmpty()) code = code.dropLast(1)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(
                        1.5.dp,
                        when {
                            isCursor -> Blue500
                            filled -> MoneyFlowTheme.colors.borderDefault
                            else -> MoneyFlowTheme.colors.borderSubtle
                        }
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (filled) code[index].toString() else "",
                            fontFamily = FontMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    }
                }
            }
        }

        // Bàn phím số rút gọn để nhập mã
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").take(5).forEach { d ->
                DigitKey(d, Modifier.weight(1f)) { if (code.length < 6) code += d }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("6", "7", "8", "9", "0").forEach { d ->
                DigitKey(d, Modifier.weight(1f)) { if (code.length < 6) code += d }
            }
        }

        // Đếm ngược gửi lại
        Spacer(modifier = Modifier.height(16.dp))
        MfCard {
            MfRow(
                title = if (secondsLeft > 0) "Gửi lại mã sau" else "Chưa nhận được mã?",
                icon = Icons.Outlined.Schedule,
                iconTint = MoneyFlowTheme.colors.textTertiary,
                iconBackground = Ink100,
                iconSize = 30.dp,
                trailing = {
                    if (secondsLeft > 0) {
                        Text(
                            text = "%02d:%02d".format(secondsLeft / 60, secondsLeft % 60),
                            fontFamily = FontMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    } else {
                        Text(
                            text = "Gửi lại",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Blue600,
                            modifier = Modifier.clickable { secondsLeft = 161 }
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        AuthPrimaryButton(text = "Xác minh", onClick = onVerified)

        Spacer(modifier = Modifier.height(14.dp))
        MfCard {
            MfRow(
                title = "Đổi email nhận mã",
                icon = Icons.Outlined.Email,
                iconSize = 30.dp,
                showChevron = true,
                onClick = onBack
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nhớ ra mật khẩu? ",
                fontFamily = FontCore,
                fontSize = 12.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
            Text(
                text = "Đăng nhập",
                fontFamily = FontCore,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Blue600,
                modifier = Modifier.clickable(onClick = onBack)
            )
        }
    }
}

@Composable
private fun DigitKey(digit: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
    ) {
        Text(
            text = digit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            textAlign = TextAlign.Center,
            fontFamily = FontMono,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MoneyFlowTheme.colors.textPrimary
        )
    }
}
