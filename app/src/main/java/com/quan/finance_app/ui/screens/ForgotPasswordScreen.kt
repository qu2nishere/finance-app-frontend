package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.core.MfIconTile
import com.quan.finance_app.ui.components.core.MfScreenHeader
import com.quan.finance_app.ui.theme.MoneyFlowTheme

/**
 * Màn 1/3 của luồng quên mật khẩu: nhập email để nhận mã xác nhận 6 số.
 */
@Composable
fun ForgotPasswordScreen(
    email: String,
    loading: Boolean,
    errorMessage: String?,
    fieldErrors: Map<String, String>,
    onEmailChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val problem = fieldErrors["email"] ?: errorMessage

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
        MfScreenHeader(title = "Quên mật khẩu", onBack = onBack)

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space5))
        PasswordResetStepBar(step = 1)
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space3))
        Text(
            text = "Bước 1/3 · Nhập email đăng ký",
            style = MoneyFlowTheme.typography.caption,
            color = MoneyFlowTheme.colors.textTertiary
        )

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space7))
        MfIconTile(
            icon = Icons.Outlined.LockReset,
            tint = MoneyFlowTheme.colors.textBrand,
            background = MoneyFlowTheme.colors.surfaceBrandSoft,
            size = 48.dp
        )
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space4))
        Text(
            text = "Nhận mã đặt lại mật khẩu",
            style = MoneyFlowTheme.typography.title2.copy(fontWeight = FontWeight.Bold),
            color = MoneyFlowTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space2))
        Text(
            text = "Nhập email bạn dùng để đăng nhập. Nếu tài khoản tồn tại, mã 6 số sẽ được gửi tới hộp thư của bạn.",
            style = MoneyFlowTheme.typography.bodySm,
            color = MoneyFlowTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space6))
        AuthField(
            label = "EMAIL",
            value = email,
            onValueChange = onEmailChange,
            icon = Icons.Outlined.Email,
            focused = true,
            enabled = !loading
        )

        if (problem != null) {
            Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space3))
            Text(
                text = problem,
                style = MoneyFlowTheme.typography.caption,
                color = MoneyFlowTheme.colors.stateDanger
            )
        }

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space6))
        AuthPrimaryButton(
            text = if (loading) "Đang gửi mã…" else "Gửi mã",
            onClick = { if (!loading) onSendCode() }
        )

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space6))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = MoneyFlowTheme.spacing.tapMin),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nhớ ra mật khẩu? ",
                style = MoneyFlowTheme.typography.caption,
                color = MoneyFlowTheme.colors.textSecondary
            )
            Text(
                text = "Đăng nhập",
                style = MoneyFlowTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
                color = MoneyFlowTheme.colors.textLink,
                modifier = Modifier.clickable(enabled = !loading, onClick = onBack)
            )
        }
    }
}

/** Thanh tiến trình 3 bước dùng chung cho 3 màn đặt lại mật khẩu. */
@Composable
internal fun PasswordResetStepBar(step: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(MoneyFlowTheme.spacing.space2)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .background(
                        color = if (index < step) {
                            MoneyFlowTheme.colors.surfaceBrand
                        } else {
                            MoneyFlowTheme.colors.borderSubtle
                        },
                        shape = RoundedCornerShape(MoneyFlowTheme.radii.pill)
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    MoneyFlowTheme {
        ForgotPasswordScreen(
            email = "quan@gmail.com",
            loading = false,
            errorMessage = null,
            fieldErrors = emptyMap(),
            onEmailChange = {},
            onSendCode = {},
            onBack = {}
        )
    }
}
