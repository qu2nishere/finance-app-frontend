package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.data.remote.WalletDto
import com.quan.finance_app.ui.components.core.SkeletonBox
import com.quan.finance_app.ui.theme.FontMono
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletPickerSheet(
    onDismissRequest: () -> Unit,
    wallets: List<WalletDto> = emptyList(),
    selectedWallet: WalletDto? = null,
    isLoading: Boolean = false,
    onWalletSelected: (WalletDto) -> Unit,
    onAddWallet: ((name: String, kind: String, initialBalance: Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddDialog by remember { mutableStateOf(false) }
    val formatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MoneyFlowTheme.colors.surfaceCard
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = MoneyFlowTheme.spacing.gutterScreen, vertical = 12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Chọn ví",
                        style = MoneyFlowTheme.typography.title2,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                    Text(
                        text = "Chọn ví nguồn tiền cho giao dịch",
                        style = MoneyFlowTheme.typography.caption,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }

                if (onAddWallet != null) {
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        border = BorderStroke(1.dp, MoneyFlowTheme.colors.surfaceBrand),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MoneyFlowTheme.colors.surfaceBrand
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Thêm ví",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Thêm mới",
                            style = MoneyFlowTheme.typography.caption,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading && wallets.isEmpty()) {
                // Skeleton loading
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(3) {
                        SkeletonBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            } else if (wallets.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MoneyFlowTheme.colors.textSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Chưa có ví nào",
                        style = MoneyFlowTheme.typography.body,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MoneyFlowTheme.colors.surfaceBrand),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tạo ví ngay")
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(wallets) { wallet ->
                        val isSelected = wallet.id == selectedWallet?.id
                        val icon = getWalletKindIcon(wallet.kind)
                        val kindLabel = getWalletKindLabel(wallet.kind)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) MoneyFlowTheme.colors.surfaceBrand.copy(alpha = 0.08f)
                                    else MoneyFlowTheme.colors.surfacePage
                                )
                                .border(
                                    BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) MoneyFlowTheme.colors.surfaceBrand else MoneyFlowTheme.colors.borderSubtle
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    onWalletSelected(wallet)
                                    onDismissRequest()
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Box
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MoneyFlowTheme.colors.surfaceBrand
                                        else MoneyFlowTheme.colors.surfaceBrand.copy(alpha = 0.12f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = wallet.name,
                                    tint = if (isSelected) Color.White else MoneyFlowTheme.colors.surfaceBrand,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Name & Kind label
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = wallet.name,
                                    style = MoneyFlowTheme.typography.body,
                                    fontWeight = FontWeight.Bold,
                                    color = MoneyFlowTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = kindLabel,
                                    style = MoneyFlowTheme.typography.caption,
                                    color = MoneyFlowTheme.colors.textSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            // Balance & Selected Checkmark
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = formatter.format(wallet.currentBalance) + " ₫",
                                    style = MoneyFlowTheme.typography.body,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontMono,
                                    color = if (isSelected) MoneyFlowTheme.colors.surfaceBrand else MoneyFlowTheme.colors.textPrimary
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(MoneyFlowTheme.colors.surfaceBrand),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Đã chọn",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Nút viền đứt "+ Thêm ví mới" ở cuối bottom sheet
            if (onAddWallet != null && wallets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            BorderStroke(1.dp, MoneyFlowTheme.colors.surfaceBrand.copy(alpha = 0.4f)),
                            RoundedCornerShape(14.dp)
                        )
                        .background(MoneyFlowTheme.colors.surfaceBrand.copy(alpha = 0.05f))
                        .clickable { showAddDialog = true }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MoneyFlowTheme.colors.surfaceBrand,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thêm ví mới",
                            style = MoneyFlowTheme.typography.body,
                            fontWeight = FontWeight.SemiBold,
                            color = MoneyFlowTheme.colors.surfaceBrand
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog tạo ví mới
    if (showAddDialog) {
        var walletName by remember { mutableStateOf("") }
        var selectedKind by remember { mutableStateOf("CASH") }
        var initialBalanceStr by remember { mutableStateOf("") }
        var isNameError by remember { mutableStateOf(false) }

        val kinds = listOf(
            "CASH" to "Tiền mặt",
            "BANK" to "Ngân hàng",
            "EWALLET" to "Ví điện tử",
            "CREDIT_CARD" to "Thẻ tín dụng"
        )

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Thêm ví mới",
                    style = MoneyFlowTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = MoneyFlowTheme.colors.textPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tên ví:",
                        style = MoneyFlowTheme.typography.caption,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = walletName,
                        onValueChange = {
                            walletName = it
                            isNameError = false
                        },
                        placeholder = { Text("VD: Ví chính, Vietcombank, Momo...") },
                        isError = isNameError,
                        supportingText = if (isNameError) {
                            { Text("Tên ví không được để trống") }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Loại ví:",
                        style = MoneyFlowTheme.typography.caption,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(kinds) { (kindKey, kindTitle) ->
                            val isKindSelected = selectedKind == kindKey
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isKindSelected) MoneyFlowTheme.colors.surfaceBrand
                                        else MoneyFlowTheme.colors.surfacePage
                                    )
                                    .border(
                                        1.dp,
                                        if (isKindSelected) MoneyFlowTheme.colors.surfaceBrand else MoneyFlowTheme.colors.borderSubtle,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedKind = kindKey }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = kindTitle,
                                    style = MoneyFlowTheme.typography.caption,
                                    fontWeight = if (isKindSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isKindSelected) Color.White else MoneyFlowTheme.colors.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Số dư ban đầu (₫):",
                        style = MoneyFlowTheme.typography.caption,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = initialBalanceStr,
                        onValueChange = { initialBalanceStr = it.filter { char -> char.isDigit() } },
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = walletName.trim()
                        if (trimmed.isNotBlank()) {
                            val balance = initialBalanceStr.toLongOrNull() ?: 0L
                            onAddWallet?.invoke(trimmed, selectedKind, balance)
                            showAddDialog = false
                            onDismissRequest()
                        } else {
                            isNameError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MoneyFlowTheme.colors.surfaceBrand),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Tạo ví", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Huỷ", color = MoneyFlowTheme.colors.textSecondary)
                }
            },
            containerColor = MoneyFlowTheme.colors.surfaceCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

fun getWalletKindIcon(kind: String?): ImageVector {
    return when (kind?.uppercase()) {
        "CASH" -> Icons.Outlined.Payments
        "BANK" -> Icons.Outlined.AccountBalance
        "EWALLET" -> Icons.Outlined.Smartphone
        "CREDIT_CARD" -> Icons.Outlined.CreditCard
        "SAVINGS" -> Icons.Outlined.Savings
        "INVESTMENT" -> Icons.AutoMirrored.Outlined.TrendingUp
        else -> Icons.Outlined.AccountBalanceWallet
    }
}

fun getWalletKindLabel(kind: String?): String {
    return when (kind?.uppercase()) {
        "CASH" -> "Tiền mặt"
        "BANK" -> "Ngân hàng"
        "EWALLET" -> "Ví điện tử"
        "CREDIT_CARD" -> "Thẻ tín dụng"
        "SAVINGS" -> "Tiết kiệm"
        "INVESTMENT" -> "Đầu tư"
        else -> "Ví thông thường"
    }
}
