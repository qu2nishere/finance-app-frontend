package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
    brandColor: Color = MoneyFlowTheme.colors.surfaceBrand,
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
            // Header — nút thêm ví chỉ nằm ở cuối danh sách, không lặp lại ở đây
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
                        colors = ButtonDefaults.buttonColors(containerColor = brandColor),
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
                                    if (isSelected) brandColor.copy(alpha = 0.08f)
                                    else MoneyFlowTheme.colors.surfacePage
                                )
                                .border(
                                    BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) brandColor else MoneyFlowTheme.colors.borderSubtle
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
                                        if (isSelected) brandColor
                                        else brandColor.copy(alpha = 0.12f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = wallet.name,
                                    tint = if (isSelected) Color.White else brandColor,
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
                                    color = if (isSelected) brandColor else MoneyFlowTheme.colors.textPrimary
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(brandColor),
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
                            BorderStroke(1.dp, brandColor.copy(alpha = 0.4f)),
                            RoundedCornerShape(14.dp)
                        )
                        .background(brandColor.copy(alpha = 0.05f))
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
                            tint = brandColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thêm ví mới",
                            style = MoneyFlowTheme.typography.body,
                            fontWeight = FontWeight.SemiBold,
                            color = brandColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddDialog) {
        AddWalletDialog(
            brand = brandColor,
            onDismissRequest = { showAddDialog = false },
            onConfirm = { name, kind, initialBalance ->
                onAddWallet?.invoke(name, kind, initialBalance)
                showAddDialog = false
                onDismissRequest()
            }
        )
    }
}

/** Các loại ví khớp với enum WalletKind ở ledger-service. */
private val addableWalletKinds = listOf("CASH", "BANK", "EWALLET", "CREDIT_CARD", "SAVINGS", "INVESTMENT")

private fun walletNameHint(kind: String): String = when (kind) {
    "BANK" -> "VD: Vietcombank, Techcombank..."
    "EWALLET" -> "VD: Momo, ZaloPay..."
    "CREDIT_CARD" -> "VD: Thẻ VIB, Thẻ TPBank..."
    "SAVINGS" -> "VD: Quỹ dự phòng, Sổ tiết kiệm..."
    "INVESTMENT" -> "VD: Chứng khoán, Vàng..."
    else -> "VD: Ví chính, Tiền mặt..."
}

@Composable
private fun AddWalletDialog(
    brand: Color,
    onDismissRequest: () -> Unit,
    onConfirm: (name: String, kind: String, initialBalance: Long) -> Unit
) {
    var walletName by remember { mutableStateOf("") }
    var selectedKind by remember { mutableStateOf("CASH") }
    // Giữ dạng đã chia nhóm "1.500.000" và đặt con trỏ ở cuối sau mỗi lần gõ
    var balanceField by remember { mutableStateOf(TextFieldValue("")) }
    val balance = balanceField.text.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val nameFocus = remember { FocusRequester() }
    val canSubmit = walletName.isNotBlank()
    val fieldColors = OutlinedTextFieldDefaults.colors(focusedBorderColor = brand, cursorColor = brand)

    fun submit() {
        if (canSubmit) onConfirm(walletName.trim(), selectedKind, balance)
    }

    LaunchedEffect(Unit) { nameFocus.requestFocus() }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MoneyFlowTheme.colors.surfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header: icon theo loại ví + tiêu đề + nút đóng
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brand),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getWalletKindIcon(selectedKind),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Thêm ví mới",
                            style = MoneyFlowTheme.typography.title3,
                            fontWeight = FontWeight.Bold,
                            color = MoneyFlowTheme.colors.textPrimary
                        )
                        Text(
                            text = getWalletKindLabel(selectedKind),
                            style = MoneyFlowTheme.typography.caption,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AddWalletLabel("LOẠI VÍ")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    addableWalletKinds.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { kind ->
                                WalletKindOption(
                                    kind = kind,
                                    selected = kind == selectedKind,
                                    brand = brand,
                                    onClick = { selectedKind = kind },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                AddWalletLabel("TÊN VÍ")
                OutlinedTextField(
                    value = walletName,
                    onValueChange = { walletName = it.take(50) },
                    placeholder = { Text(walletNameHint(selectedKind)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(nameFocus),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(18.dp))

                AddWalletLabel("SỐ DƯ BAN ĐẦU")
                OutlinedTextField(
                    value = balanceField,
                    onValueChange = { input ->
                        val digits = input.text.filter { it.isDigit() }.trimStart('0').take(12)
                        val grouped = if (digits.isEmpty()) "" else groupDigits(digits.toLong())
                        balanceField = TextFieldValue(grouped, TextRange(grouped.length))
                    },
                    placeholder = { Text("0", fontFamily = FontMono) },
                    suffix = { Text("₫", fontWeight = FontWeight.SemiBold) },
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontMono,
                        fontWeight = FontWeight.SemiBold
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(100_000L, 500_000L, 1_000_000L).forEach { step ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(brand.copy(alpha = 0.08f))
                                .clickable {
                                    val grouped = groupDigits((balance + step).coerceAtMost(999_999_999_999L))
                                    balanceField = TextFieldValue(grouped, TextRange(grouped.length))
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "+" + groupDigits(step),
                                style = MoneyFlowTheme.typography.caption,
                                fontWeight = FontWeight.SemiBold,
                                color = brand
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
                    ) {
                        Text("Huỷ", color = MoneyFlowTheme.colors.textSecondary)
                    }
                    Button(
                        onClick = { submit() },
                        enabled = canSubmit,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brand)
                    ) {
                        Text("Tạo ví", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddWalletLabel(text: String) {
    Text(
        text = text,
        style = MoneyFlowTheme.typography.caption,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        color = MoneyFlowTheme.colors.textSecondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun WalletKindOption(
    kind: String,
    selected: Boolean,
    brand: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) brand.copy(alpha = 0.08f) else MoneyFlowTheme.colors.surfacePage)
            .border(
                BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) brand else MoneyFlowTheme.colors.borderSubtle),
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            getWalletKindIcon(kind),
            contentDescription = null,
            tint = if (selected) brand else MoneyFlowTheme.colors.textSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = getWalletKindLabel(kind),
            style = MoneyFlowTheme.typography.caption,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) brand else MoneyFlowTheme.colors.textPrimary,
            maxLines = 1
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
