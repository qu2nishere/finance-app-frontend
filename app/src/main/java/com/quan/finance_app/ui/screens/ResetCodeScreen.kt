package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.core.MfCard
import com.quan.finance_app.ui.components.core.MfIconTile
import com.quan.finance_app.ui.components.core.MfInfoBanner
import com.quan.finance_app.ui.components.core.MfRow
import com.quan.finance_app.ui.components.core.MfScreenHeader
import com.quan.finance_app.ui.components.forms.OtpCodeField
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import java.util.Locale

/**
 * Màn 2/3 của luồng quên mật khẩu: nhập mã OTP 6 chữ số và kiểm tra ngay với server.
 */
@Composable
fun ResetCodeScreen(
    email: String,
    code: String,
    loading: Boolean,
    resendSecondsLeft: Int,
    errorMessage: String?,
    infoMessage: String?,
    fieldErrors: Map<String, String>,
    onCodeChange: (String) -> Unit,
    onVerifyCode: () -> Unit,
    onResendCode: () -> Unit,
    onBack: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val problem = fieldErrors["code"] ?: errorMessage

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
        MfScreenHeader(title = "Xác nhận mã", onBack = onBack)

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space5))
        PasswordResetStepBar(step = 2)
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space3))
        Text(
            text = "Bước 2/3 · Nhập mã xác nhận",
            style = MoneyFlowTheme.typography.caption,
            color = MoneyFlowTheme.colors.textTertiary
        )

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space7))
        MfIconTile(
            icon = Icons.Outlined.MarkEmailRead,
            tint = MoneyFlowTheme.colors.textBrand,
            background = MoneyFlowTheme.colors.surfaceBrandSoft,
            size = 48.dp
        )
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space4))
        Text(
            text = "Nhập mã 6 số",
            style = MoneyFlowTheme.typography.title2.copy(fontWeight = FontWeight.Bold),
            color = MoneyFlowTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space2))
        Text(
            text = "Nếu $email đã đăng ký tài khoản, mã xác nhận đã được gửi tới hộp thư và có hiệu lực 10 phút.",
            style = MoneyFlowTheme.typography.bodySm,
            color = MoneyFlowTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space6))
        OtpCodeField(
            code = code,
            onCodeChange = onCodeChange,
            enabled = !loading,
            hasError = problem != null,
            autoFocus = true,
            onDone = { if (!loading && code.length == 6) onVerifyCode() }
        )

        if (problem != null) {
            Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space3))
            Text(
                text = problem,
                style = MoneyFlowTheme.typography.caption,
                color = MoneyFlowTheme.colors.stateDanger
            )
        } else if (infoMessage != null) {
            Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space3))
            MfInfoBanner(
                text = infoMessage,
                icon = Icons.Outlined.CheckCircle,
                background = MoneyFlowTheme.colors.surfaceInSoft,
                contentColor = MoneyFlowTheme.colors.flowInStrong,
                borderColor = MoneyFlowTheme.colors.flowInTrack
            )
        }

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space5))
        MfCard {
            MfRow(
                title = if (resendSecondsLeft > 0) "Gửi lại mã sau" else "Chưa nhận được mã?",
                icon = Icons.Outlined.Schedule,
                iconTint = MoneyFlowTheme.colors.textTertiary,
                iconBackground = MoneyFlowTheme.colors.surfaceSunken,
                iconSize = 30.dp,
                trailing = {
                    if (resendSecondsLeft > 0) {
                        Text(
                            text = String.format(
                                Locale.ROOT,
                                "%02d:%02d",
                                resendSecondsLeft / 60,
                                resendSecondsLeft % 60
                            ),
                            style = MoneyFlowTheme.typography.amountSm.copy(fontWeight = FontWeight.Bold),
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    } else {
                        Text(
                            text = "Gửi lại",
                            style = MoneyFlowTheme.typography.bodySm.copy(fontWeight = FontWeight.SemiBold),
                            color = MoneyFlowTheme.colors.textLink,
                            modifier = Modifier.clickable(enabled = !loading, onClick = onResendCode)
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space5))
        AuthPrimaryButton(
            text = if (loading) "Đang kiểm tra…" else "Tiếp tục",
            onClick = { if (!loading) onVerifyCode() }
        )

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space4))
        MfCard {
            MfRow(
                title = "Đổi email nhận mã",
                icon = Icons.Outlined.Email,
                iconSize = 30.dp,
                showChevron = true,
                onClick = { if (!loading) onBack() }
            )
        }

        Spacer(modifier = Modifier.height(MoneyFlowTheme.spacing.space5))
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
                modifier = Modifier.clickable(enabled = !loading, onClick = onBackToLogin)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetCodeScreenPreview() {
    MoneyFlowTheme {
        ResetCodeScreen(
            email = "quan@gmail.com",
            code = "4829",
            loading = false,
            resendSecondsLeft = 45,
            errorMessage = null,
            infoMessage = null,
            fieldErrors = emptyMap(),
            onCodeChange = {},
            onVerifyCode = {},
            onResendCode = {},
            onBack = {},
            onBackToLogin = {}
        )
    }
}
