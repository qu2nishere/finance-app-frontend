package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*
import kotlin.math.abs

/**
 * 10h — Điều chỉnh số dư thực tế. Mở từ 8e.
 */
@Composable
fun AdjustBalanceScreen(
    walletName: String = "Vietcombank ●●42",
    appBalance: Long = 9_240_000L,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var realBalance by remember { mutableLongStateOf(9_180_000L) }
    var recordAsExpense by remember { mutableStateOf(true) }
    var note by remember { mutableStateOf("Phí thẻ tháng 8") }

    val diff = realBalance - appBalance

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { MfScreenHeader(title = "Điều chỉnh số dư", onBack = onBack) }

        // Ví đang chỉnh
        item {
            MfCard {
                MfRow(
                    title = walletName,
                    titleWeight = FontWeight.Bold,
                    icon = Icons.Outlined.CreditCard,
                    iconSize = 38.dp,
                    subtitle = "Số dư trong app · 09:14",
                    trailing = {
                        Text(
                            text = formatVnd(appBalance),
                            fontFamily = FontMono,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                    }
                )
            }
        }

        // Số dư thực tế
        item {
            MfCard(borderColor = Blue200) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MfSectionLabel("SỐ DƯ THỰC TẾ")
                    Spacer(modifier = Modifier.height(10.dp))
                    MfAmountStepperField(
                        value = groupDigits(realBalance),
                        onValueChange = { raw ->
                            realBalance = raw.filter { it.isDigit() }.take(12).toLongOrNull() ?: 0L
                        },
                        suffix = "₫",
                        accent = Blue600,
                        onIncrement = { realBalance += 10_000L },
                        onDecrement = { realBalance = (realBalance - 10_000L).coerceAtLeast(0L) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    if (diff == 0L) {
                        MfInfoBanner(
                            text = "Khớp với số dư trong app — không cần điều chỉnh.",
                            icon = Icons.Outlined.CheckCircle,
                            background = Grow50,
                            contentColor = Grow600
                        )
                    } else {
                        MfInfoBanner(
                            text = "Lệch ${signedVnd(diff)} · app sẽ ghi một giao dịch điều chỉnh",
                            icon = if (diff < 0) Icons.Outlined.NorthEast else Icons.Outlined.SouthWest,
                            background = if (diff < 0) Heat50 else Grow50,
                            contentColor = if (diff < 0) Heat600 else Grow600
                        )
                    }
                }
            }
        }

        // Ghi khoản lệch là gì
        item { MfSectionLabel("GHI KHOẢN LỆCH LÀ") }
        item {
            MfCard {
                MfRow(
                    title = if (diff <= 0) "Chi chưa ghi" else "Thu chưa ghi",
                    icon = Icons.Outlined.GridView,
                    iconTint = if (diff <= 0) Heat600 else Grow600,
                    iconBackground = if (diff <= 0) Heat50 else Grow50,
                    subtitle = "Gắn danh mục · Khác",
                    onClick = { recordAsExpense = true },
                    trailing = { MfRadioDot(selected = recordAsExpense) }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Chỉ sửa số dư",
                    icon = Icons.Outlined.Tune,
                    iconTint = MoneyFlowTheme.colors.textTertiary,
                    iconBackground = Ink100,
                    subtitle = "Không tính vào chi tiêu tháng",
                    onClick = { recordAsExpense = false },
                    trailing = { MfRadioDot(selected = !recordAsExpense) }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Ghi chú",
                    icon = Icons.Outlined.Edit,
                    onClick = { },
                    trailing = {
                        Text(
                            text = note.ifBlank { "Thêm ghi chú" },
                            fontFamily = FontCore,
                            fontSize = 13.sp,
                            color = MoneyFlowTheme.colors.textTertiary
                        )
                    }
                )
            }
        }

        item {
            MfFooterActions(
                cancelText = "Huỷ",
                confirmText = "Lưu số dư",
                confirmColor = Blue500,
                onCancel = onBack,
                onConfirm = onBack
            )
        }
    }
}
