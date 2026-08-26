package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 9a — Đăng nhập. Màn đầu tiên, nằm ngoài khung tab.
 */
@Composable
fun LoginScreen(
    onLogin: () -> Unit = {},
    onGoToRegister: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("minh@email.com") }
    var password by remember { mutableStateOf("12345678") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage)
            .verticalScroll(rememberScrollState())
    ) {
        // Header gradient
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(GradientHero),
                    RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(21.dp)
                    )
                }
                Spacer(modifier = Modifier.width(11.dp))
                Text(
                    text = "Money Flow",
                    fontFamily = FontCore,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Chào bạn trở lại",
                fontFamily = FontCore,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Đăng nhập để xem dòng tiền tháng này.",
                fontFamily = FontCore,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AuthField(
                label = "EMAIL",
                value = email,
                onValueChange = { email = it },
                icon = Icons.Outlined.Email
            )

            AuthField(
                label = "MẬT KHẨU",
                value = password,
                onValueChange = { password = it },
                icon = Icons.Outlined.Lock,
                focused = true,
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

            // Ghi nhớ · quên mật khẩu
            Row(verticalAlignment = Alignment.CenterVertically) {
                AuthCheckbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                Spacer(modifier = Modifier.width(9.dp))
                Text(
                    text = "Ghi nhớ đăng nhập",
                    modifier = Modifier.weight(1f),
                    fontFamily = FontCore,
                    fontSize = 12.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                Text(
                    text = "Quên mật khẩu?",
                    fontFamily = FontCore,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Blue600,
                    modifier = Modifier.clickable { }
                )
            }

            AuthPrimaryButton(text = "Đăng nhập", onClick = onLogin)

            // Face ID
            MfCard {
                MfRow(
                    title = "Đăng nhập bằng Face ID",
                    icon = Icons.Outlined.Face,
                    iconSize = 30.dp,
                    showChevron = true,
                    onClick = onLogin
                )
            }

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
                    icon = Icons.Outlined.Language,
                    modifier = Modifier.weight(1f),
                    onClick = onLogin
                )
                SocialButton(
                    label = "Apple",
                    icon = Icons.Outlined.PhoneIphone,
                    modifier = Modifier.weight(1f),
                    onClick = onLogin
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 28.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Chưa có tài khoản? ",
                    fontFamily = FontCore,
                    fontSize = 12.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                Text(
                    text = "Đăng ký",
                    fontFamily = FontCore,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue600,
                    modifier = Modifier.clickable(onClick = onGoToRegister)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// Dùng chung cho 9a và 9b
// -------------------------------------------------------------------------

@Composable
internal fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    focused: Boolean = false,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    trailing: @Composable (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            fontFamily = FontCore,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.9.sp,
            color = MoneyFlowTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(
                if (focused) 1.5.dp else 1.dp,
                if (focused) Blue500 else MoneyFlowTheme.colors.borderDefault
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (focused) Blue600 else MoneyFlowTheme.colors.textTertiary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = true,
                    visualTransformation = if (isPassword) PasswordVisualTransformation()
                    else VisualTransformation.None,
                    textStyle = TextStyle(
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    ),
                    cursorBrush = SolidColor(Blue500),
                    modifier = Modifier.weight(1f)
                )
                if (trailing != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    trailing()
                }
            }
        }
    }
}

@Composable
internal fun AuthCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(
                if (checked) Blue500 else Color.White,
                RoundedCornerShape(6.dp)
            )
            .then(
                if (checked) Modifier
                else Modifier.border(1.5.dp, MoneyFlowTheme.colors.borderStrong, RoundedCornerShape(6.dp))
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
internal fun AuthPrimaryButton(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Blue500,
        shadowElevation = 4.dp
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center,
            fontFamily = FontCore,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = Color.White
        )
    }
}

@Composable
private fun SocialButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 13.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MoneyFlowTheme.colors.textPrimary,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontFamily = FontCore,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
        }
    }
}
