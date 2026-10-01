package com.quan.finance_app.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 9b — Đăng ký, bước 1/3 (thông tin cơ bản).
 * Bước 2 & 3 chưa có trong wireframe nên "Tiếp tục" đi thẳng vào app.
 */
@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onContinue: (fullName: String, email: String, password: String) -> Unit = { _, _, _ -> },
    onGoogleLogin: () -> Unit = {},
    onGoToLogin: () -> Unit = {},
    loading: Boolean = false,
    errorMessage: String? = null,
    fieldErrors: Map<String, String> = emptyMap(),
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("Nguyễn Minh") }
    var email by remember { mutableStateOf("minh@email.com") }
    var password by remember { mutableStateOf("12345678") }
    var passwordVisible by remember { mutableStateOf(false) }
    var acceptedTerms by remember { mutableStateOf(true) }

    // 4 vạch, độ mạnh tính thô theo độ dài + có chữ số
    val strength = remember(password) {
        var s = 0
        if (password.length >= 8) s++
        if (password.any { it.isDigit() }) s++
        if (password.any { it.isLetter() }) s++
        if (password.any { !it.isLetterOrDigit() }) s++
        s
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 28.dp)
    ) {
        // Header
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
                text = "Tạo tài khoản",
                modifier = Modifier.weight(1f),
                fontFamily = FontCore,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Text(
                text = "Bỏ qua",
                fontFamily = FontCore,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.clickable { if (!loading) onContinue(name, email, password) }
            )
        }

        // Thanh tiến trình 3 bước
        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .background(
                            if (index == 0) Blue500 else Ink200,
                            RoundedCornerShape(99.dp)
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bước 1/3 · Thông tin cơ bản",
            fontFamily = FontCore,
            fontSize = 11.sp,
            color = MoneyFlowTheme.colors.textTertiary
        )

        Spacer(modifier = Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            AuthField(
                label = "TÊN CỦA BẠN",
                value = name,
                onValueChange = { name = it },
                icon = Icons.Outlined.Person
            )
            AuthField(
                label = "EMAIL",
                value = email,
                onValueChange = { email = it },
                icon = Icons.Outlined.Email,
                focused = true
            )
            AuthField(
                label = "MẬT KHẨU",
                value = password,
                onValueChange = { password = it },
                icon = Icons.Outlined.Lock,
                isPassword = !passwordVisible,
                trailing = {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Hiện mật khẩu",
                        tint = MoneyFlowTheme.colors.textTertiary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { passwordVisible = !passwordVisible }
                    )
                }
            )

            // Độ mạnh mật khẩu
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
                        strength >= 3 -> "Mật khẩu mạnh · đủ 8 ký tự, có số"
                        strength == 2 -> "Mật khẩu trung bình · nên thêm số hoặc ký tự đặc biệt"
                        else -> "Mật khẩu yếu · cần ít nhất 8 ký tự"
                    },
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    color = if (strength >= 3) Grow600 else MoneyFlowTheme.colors.textSecondary
                )
            }

            // Đơn vị tiền tệ
            Column {
                Text(
                    text = "ĐƠN VỊ TIỀN TỆ",
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.9.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                MfCard {
                    MfRow(
                        title = "VND ₫ · Việt Nam",
                        icon = Icons.Outlined.Payments,
                        iconTint = MoneyFlowTheme.colors.textTertiary,
                        iconBackground = Ink100,
                        iconSize = 30.dp,
                        showChevron = true,
                        onClick = { }
                    )
                }
            }

            // Điều khoản
            MfCard {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    AuthCheckbox(checked = acceptedTerms, onCheckedChange = { acceptedTerms = it })
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tôi đồng ý với Điều khoản sử dụng và Chính sách bảo mật",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }

            // Lỗi từ backend: 409 email trùng, 400 mật khẩu quá ngắn / email sai định dạng...
            val problem = errorMessage ?: fieldErrors.values.joinToString("\n").ifBlank { null }
            if (problem != null) {
                Text(
                    text = problem,
                    fontFamily = FontCore,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MoneyFlowTheme.colors.stateDanger
                )
            }

            AuthPrimaryButton(
                text = if (loading) "Đang tạo tài khoản…" else "Tiếp tục",
                onClick = { if (!loading) onContinue(name, email, password) }
            )

            // hoặc
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MoneyFlowTheme.colors.borderSubtle
                )
                Text(
                    text = "hoặc",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    color = MoneyFlowTheme.colors.textTertiary
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MoneyFlowTheme.colors.borderSubtle
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SocialButton(
                    label = "Google",
                    iconRes = com.quan.finance_app.R.drawable.ic_google,
                    modifier = Modifier.weight(1f),
                    onClick = { if (!loading) onGoogleLogin() }
                )
                SocialButton(
                    label = "Apple",
                    icon = Icons.Outlined.PhoneIphone,
                    modifier = Modifier.weight(1f),
                    onClick = { }
                )
            }

            MfInfoBanner(
                text = "Bước sau: chọn danh mục thường dùng và đặt hạn mức đầu tiên — bỏ qua được, sửa sau trong Cài đặt.",
                icon = Icons.Outlined.Info,
                background = Ink50,
                contentColor = MoneyFlowTheme.colors.textSecondary,
                borderColor = MoneyFlowTheme.colors.borderDefault
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Đã có tài khoản? ",
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
                    modifier = Modifier.clickable(onClick = onGoToLogin)
                )
            }
        }
    }
}
