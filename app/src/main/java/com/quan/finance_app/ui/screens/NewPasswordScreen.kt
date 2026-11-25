package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.core.MfIconTile
import com.quan.finance_app.ui.components.core.MfScreenHeader
import com.quan.finance_app.ui.theme.MoneyFlowTheme

/**
 * Màn 3/3 của luồng quên mật khẩu: đặt mật khẩu mới và xác nhận lại.
 * Mật khẩu chỉ giữ bằng `remember` trong RAM, không ghi vào Bundle.
 */
@Composable
fun NewPasswordScreen(
    loading: Boolean,
    errorMessage: String?,
    fieldErrors: Map<String, String>,
    onSubmit: (newPassword: String, confirmPassword: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val strength = remember(newPassword) {
        var score = 0
        if (newPassword.length >= 8) score++
        if (newPassword.any { it.isDigit() }) score++
        if (newPassword.any { it.isLetter() }) score++
        if (newPassword.any { !it.isLetterOrDigit() }) score++
        score
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MoneyFlowTheme.spacing.space7)
            .padding(
                top = MoneyFlowTheme.spacing.space4,
                bottom = MoneyFlowTheme.spacing.space8
            )
    ) {
        MfScreenHeader(title = "Mật khẩu mới", onBack = onBack)

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space5))
        PasswordResetStepBar(step = 3)
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space3))
        Text(
            text = "Bước 3/3 · Tạo mật khẩu mới",
            style = MoneyFlowTheme.typography.caption,
            color = MoneyFlowTheme.colors.textTertiary
        )

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space7))
        MfIconTile(
            icon = Icons.Outlined.Lock,
            tint = MoneyFlowTheme.colors.textBrand,
            background = MoneyFlowTheme.colors.surfaceBrandSoft,
            size = 48.dp
        )
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space4))
        Text(
            text = "Đặt mật khẩu mới",
            style = MoneyFlowTheme.typography.title2.copy(fontWeight = FontWeight.Bold),
            color = MoneyFlowTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space2))
        Text(
            text = "Mật khẩu cần từ 8 đến 72 ký tự. Sau khi đổi, mọi thiết bị đang đăng nhập sẽ cần đăng nhập lại.",
            style = MoneyFlowTheme.typography.bodySm,
            color = MoneyFlowTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space6))
        AuthField(
            label = "MẬT KHẨU MỚI",
            value = newPassword,
            onValueChange = { newPassword = it },
            icon = Icons.Outlined.Lock,
            focused = true,
            enabled = !loading,
            isPassword = !newPasswordVisible,
            trailing = {
                Icon(
                    imageVector = if (newPasswordVisible) {
                        Icons.Outlined.VisibilityOff
                    } else {
                        Icons.Outlined.Visibility
                    },
                    contentDescription = if (newPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                    tint = MoneyFlowTheme.colors.textTertiary,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { newPasswordVisible = !newPasswordVisible }
                )
            }
        )

        val newPasswordError = fieldErrors["newPassword"]
        if (newPasswordError != null) {
            Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space2))
            Text(
                text = newPasswordError,
                style = MoneyFlowTheme.typography.caption,
                color = MoneyFlowTheme.colors.stateDanger
            )
        }

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space3))
        Row(horizontalArrangement = Arrangement.spacedBy(MoneyFlowTheme.spacing.space2)) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            color = if (index < strength) {
                                MoneyFlowTheme.colors.flowIn
                            } else {
                                MoneyFlowTheme.colors.borderSubtle
                            },
                            shape = RoundedCornerShape(MoneyFlowTheme.radii.pill)
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space2))
        Text(
            text = when {
                strength >= 3 -> "Mật khẩu mạnh · đủ 8 ký tự, có chữ và số"
                strength == 2 -> "Mật khẩu trung bình · nên kết hợp thêm số hoặc ký tự đặc biệt"
                else -> "Cần ít nhất 8 ký tự"
            },
            style = MoneyFlowTheme.typography.caption,
            color = if (strength >= 3) {
                MoneyFlowTheme.colors.flowInStrong
            } else {
                MoneyFlowTheme.colors.textSecondary
            }
        )

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space5))
        AuthField(
            label = "NHẬP LẠI MẬT KHẨU",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            icon = Icons.Outlined.Lock,
            enabled = !loading,
            isPassword = !confirmPasswordVisible,
            trailing = {
                Icon(
                    imageVector = if (confirmPasswordVisible) {
                        Icons.Outlined.VisibilityOff
                    } else {
                        Icons.Outlined.Visibility
                    },
                    contentDescription = if (confirmPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                    tint = MoneyFlowTheme.colors.textTertiary,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { confirmPasswordVisible = !confirmPasswordVisible }
                )
            }
        )

        val confirmError = fieldErrors["confirmPassword"]
        if (confirmError != null) {
            Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space2))
            Text(
                text = confirmError,
                style = MoneyFlowTheme.typography.caption,
                color = MoneyFlowTheme.colors.stateDanger
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space3))
            Text(
                text = errorMessage,
                style = MoneyFlowTheme.typography.caption,
                color = MoneyFlowTheme.colors.stateDanger
            )
        }

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space6))
        AuthPrimaryButton(
            text = if (loading) "Đang đổi mật khẩu…" else "Đổi mật khẩu",
            onClick = { if (!loading) onSubmit(newPassword, confirmPassword) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NewPasswordScreenPreview() {
    MoneyFlowTheme {
        NewPasswordScreen(
            loading = false,
            errorMessage = null,
            fieldErrors = emptyMap(),
            onSubmit = { _, _ -> },
            onBack = {}
        )
    }
}
