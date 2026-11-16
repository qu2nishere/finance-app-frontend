package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

private data class Wallet(
    val name: String,
    val note: String,
    val amount: String,
    val icon: ImageVector,
    val tint: Color = Blue600
)

private val myWallets = listOf(
    Wallet("Vietcombank ●●42", "Ngân hàng · đồng bộ tự động", "9.240.000₫", Icons.Outlined.CreditCard),
    Wallet("Ví Momo", "Liên kết · đồng bộ tự động", "1.870.000₫", Icons.Outlined.Smartphone),
    Wallet("Tiền mặt", "Nhập tay", "1.370.000₫", Icons.Outlined.Payments)
)

private val excludedWallets = listOf(
    Wallet("Quỹ dự phòng", "Sổ tiết kiệm · mục tiêu", "11.000.000₫", Icons.Outlined.VerifiedUser, Grow600),
    Wallet("Du lịch Đà Lạt", "Mục tiêu tiết kiệm", "4.500.000₫", Icons.Outlined.CardGiftcard, Grow600)
)

/**
 * 8e — Ví & tài khoản. Mở từ Cài đặt (3e).
 */
@Composable
fun WalletsScreen(
    onBack: () -> Unit = {},
    onAddWallet: () -> Unit = {},
    onAdjustBalance: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            MfScreenHeader(
                title = "Ví & tài khoản",
                onBack = onBack,
                actionText = "Sửa"
            )
        }

        // Thẻ tổng số dư (gradient thương hiệu)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .background(Brush.linearGradient(GradientHero))
                        .padding(20.dp)
                ) {
                    Text(
                        text = "TỔNG SỐ DƯ",
                        fontFamily = FontCore,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.9.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "12.480.000₫",
                        fontFamily = FontMono,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "3 ví · cập nhật 2 phút trước",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item { MfSectionLabel("VÍ CỦA TÔI") }
        item {
            MfCard {
                myWallets.forEachIndexed { index, wallet ->
                    WalletRow(wallet)
                    if (index != myWallets.lastIndex) {
                        HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                    }
                }
            }
        }

        item { MfSectionLabel("KHÔNG TÍNH VÀO SỐ DƯ") }
        item {
            MfCard {
                excludedWallets.forEachIndexed { index, wallet ->
                    WalletRow(wallet)
                    if (index != excludedWallets.lastIndex) {
                        HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                    }
                }
            }
        }

        item {
            MfCard {
                MfRow(
                    title = "Điều chỉnh số dư thực tế",
                    icon = Icons.AutoMirrored.Outlined.ArrowForward,
                    subtitle = "Khi số dư trong app lệch với thực tế",
                    showChevron = true,
                    onClick = onAdjustBalance
                )
            }
        }

        item {
            MfDashedAction(
                text = "Thêm ví hoặc liên kết ngân hàng",
                color = Blue600,
                borderColor = Blue200,
                onClick = onAddWallet
            )
        }
    }
}

@Composable
private fun WalletRow(wallet: Wallet) {
    MfRow(
        title = wallet.name,
        icon = wallet.icon,
        iconTint = wallet.tint,
        iconBackground = Blue50,
        subtitle = wallet.note,
        showChevron = true,
        onClick = { },
        trailing = {
            Text(
                text = wallet.amount,
                fontFamily = FontMono,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
        }
    )
}
