package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

private data class WalletKind(val label: String, val icon: ImageVector)

private val walletKinds = listOf(
    WalletKind("Tiền mặt", Icons.Outlined.Payments),
    WalletKind("Ngân hàng", Icons.Outlined.CreditCard),
    WalletKind("Ví điện tử", Icons.Outlined.Smartphone),
    WalletKind("Tiết kiệm", Icons.Outlined.VerifiedUser)
)

private data class LinkableBank(val name: String, val note: String, val icon: ImageVector, val linked: Boolean)

private val linkableBanks = listOf(
    LinkableBank("Vietcombank", "Đã liên kết · ●●42", Icons.Outlined.CreditCard, true),
    LinkableBank("Techcombank", "Đồng bộ số dư & giao dịch", Icons.Outlined.CreditCard, false),
    LinkableBank("Momo", "Đồng bộ số dư & giao dịch", Icons.Outlined.Smartphone, false),
    LinkableBank("ZaloPay", "Đồng bộ số dư & giao dịch", Icons.Outlined.Smartphone, false)
)

/**
 * 10g — Thêm ví / liên kết ngân hàng. Mở từ 8e.
 */
@Composable
fun AddWalletScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var walletName by remember { mutableStateOf("Ví tiết kiệm riêng") }
    var balance by remember { mutableLongStateOf(0L) }
    var kindIndex by remember { mutableIntStateOf(1) }
    var countInTotal by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { MfScreenHeader(title = "Thêm ví", onBack = onBack) }

        item { MfSectionLabel("NHẬP TAY") }
        item {
            MfCard(borderColor = Blue200) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AuthField(
                        label = "TÊN VÍ",
                        value = walletName,
                        onValueChange = { walletName = it },
                        icon = Icons.Outlined.AccountBalanceWallet,
                        focused = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    MfSectionLabel("SỐ DƯ HIỆN TẠI")
                    Spacer(modifier = Modifier.height(8.dp))
                    MfAmountStepperField(
                        value = groupDigits(balance),
                        onValueChange = { raw ->
                            balance = raw.filter { it.isDigit() }.take(12).toLongOrNull() ?: 0L
                        },
                        suffix = "₫",
                        accent = Blue600,
                        onIncrement = { balance += 100_000L },
                        onDecrement = { balance = (balance - 100_000L).coerceAtLeast(0L) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    MfSectionLabel("LOẠI VÍ")
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        walletKinds.chunked(2).forEachIndexed { rowIndex, row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEachIndexed { colIndex, kind ->
                                    val index = rowIndex * 2 + colIndex
                                    WalletKindTile(
                                        kind = kind,
                                        selected = kindIndex == index,
                                        modifier = Modifier.weight(1f),
                                        onClick = { kindIndex = index }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
                    ) {
                        MfToggleRow(
                            title = "Tính vào tổng số dư",
                            checked = countInTotal,
                            onCheckedChange = { countInTotal = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onBack),
                        shape = RoundedCornerShape(12.dp),
                        color = Blue500
                    ) {
                        Text(
                            text = "Thêm ví",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 15.dp),
                            textAlign = TextAlign.Center,
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        item { MfSectionLabel("HOẶC LIÊN KẾT TỰ ĐỘNG") }
        item {
            MfCard {
                linkableBanks.forEachIndexed { index, bank ->
                    MfRow(
                        title = bank.name,
                        icon = bank.icon,
                        iconTint = if (bank.linked) Grow600 else Blue600,
                        iconBackground = if (bank.linked) Grow50 else Blue50,
                        subtitle = bank.note,
                        trailing = {
                            if (bank.linked) {
                                Text(
                                    text = "Đã nối",
                                    fontFamily = FontCore,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Grow600
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.clickable { },
                                    shape = RoundedCornerShape(999.dp),
                                    color = Blue50,
                                    border = BorderStroke(1.dp, Blue200)
                                ) {
                                    Text(
                                        text = "Liên kết",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontFamily = FontCore,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Blue600
                                    )
                                }
                            }
                        }
                    )
                    if (index != linkableBanks.lastIndex) {
                        HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                    }
                }
            }
        }

        item {
            MfInfoBanner(
                text = "Money Flow chỉ đọc giao dịch, không thực hiện chuyển tiền.",
                icon = Icons.Outlined.VerifiedUser,
                background = Ink50,
                contentColor = MoneyFlowTheme.colors.textSecondary,
                borderColor = MoneyFlowTheme.colors.borderDefault
            )
        }
    }
}

@Composable
private fun WalletKindTile(
    kind: WalletKind,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Blue50 else Color.White,
        border = BorderStroke(1.dp, if (selected) Blue500 else MoneyFlowTheme.colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = kind.icon,
                contentDescription = null,
                tint = if (selected) Blue600 else MoneyFlowTheme.colors.textTertiary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(9.dp))
            Text(
                text = kind.label,
                fontFamily = FontCore,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Blue600 else MoneyFlowTheme.colors.textSecondary
            )
        }
    }
}
