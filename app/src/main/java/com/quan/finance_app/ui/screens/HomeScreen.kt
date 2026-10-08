package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.data.remote.TransactionDto
import com.quan.finance_app.ui.components.core.DashboardBalanceCardSkeleton
import com.quan.finance_app.ui.components.core.DashboardTransactionsSkeleton
import com.quan.finance_app.ui.home.HomeViewModel
import com.quan.finance_app.ui.theme.FontCore
import com.quan.finance_app.ui.theme.FontMono
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onSeeAllTransactions: () -> Unit = {},
    onScanBill: () -> Unit = {},
    onAddExpense: () -> Unit = {},
    onAddIncome: () -> Unit = {},
    onClassifyTransaction: (String) -> Unit = {},
    onOpenTransaction: (String) -> Unit = {},
    onOpenGoal: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by homeViewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)),
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Header
        item {
            DashboardHeader(
                onOpenGoal = onOpenGoal,
                onOpenNotifications = onOpenNotifications
            )
        }

        // 2. Balance Card (hoặc Skeleton khi đang load)
        item {
            if (uiState.isLoading) {
                DashboardBalanceCardSkeleton()
            } else {
                DashboardBalanceCard(
                    totalBalance = uiState.totalBalance,
                    monthIncome = uiState.monthIncome,
                    monthExpense = uiState.monthExpense
                )
            }
        }

        // 3. Action Buttons
        item {
            DashboardActionButtons(
                onAddExpense = onAddExpense,
                onAddIncome = onAddIncome,
                onScanBill = onScanBill
            )
        }

        // 4. Recent Transactions (hoặc Skeleton khi đang load)
        item {
            if (uiState.isLoading) {
                DashboardTransactionsSkeleton()
            } else {
                DashboardTransactions(
                    transactions = uiState.recentTransactions,
                    onSeeAll = onSeeAllTransactions,
                    onOpenTransaction = onOpenTransaction,
                    onClassify = onClassifyTransaction
                )
            }
        }
    }
}

@Composable
fun DashboardHeader(
    onOpenGoal: () -> Unit = {},
    onOpenNotifications: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("XIN CHÀO", style = MoneyFlowTheme.typography.micro, color = MoneyFlowTheme.colors.textSecondary, fontFamily = FontCore)
            Text("Minh", fontSize = 28.sp, color = MoneyFlowTheme.colors.textPrimary, fontWeight = FontWeight.Bold, fontFamily = FontCore)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Budget Pill
            Row(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(32.dp))
                    .clickable(onClick = onOpenGoal)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFFE0B2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏝️", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Đà Lạt", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MoneyFlowTheme.colors.textPrimary, fontFamily = FontCore)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("4.5/10tr", fontSize = 12.sp, color = MoneyFlowTheme.colors.textSecondary, fontFamily = FontMono)
                }
            }
            // Notification Bell
            IconButton(
                onClick = onOpenNotifications,
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Thông báo", tint = MoneyFlowTheme.colors.textPrimary, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
fun DashboardBalanceCard(
    totalBalance: Long,
    monthIncome: Long,
    monthExpense: Long
) {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN")).apply { maximumFractionDigits = 0 }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Số dư hiện tại", fontSize = 14.sp, color = MoneyFlowTheme.colors.textSecondary, fontFamily = FontCore)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = formatter.format(totalBalance),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MoneyFlowTheme.colors.textPrimary,
                    letterSpacing = (-1).sp,
                    fontFamily = FontMono
                )
                Text(
                    text = " ₫",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoneyFlowTheme.colors.textPrimary,
                    modifier = Modifier.padding(bottom = 6.dp),
                    fontFamily = FontCore
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Mini Bar Chart minh hoạ dòng tiền
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(0.4f, 0.6f, 0.3f, 0.5f, 0.8f, 1.0f, 0.5f)
                heights.forEachIndexed { index, fraction ->
                    val isProminent = index == 5
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(fraction)
                            .background(
                                color = if (isProminent) MoneyFlowTheme.colors.surfaceBrand else Color(0xFFE3EDFF),
                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(16.dp))

            // Bottom row: Thu - Chi trong tháng
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Thu ", color = MoneyFlowTheme.colors.textSecondary, fontSize = 14.sp, fontFamily = FontCore)
                    Text(
                        "+" + formatter.format(monthIncome) + "₫",
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontMono
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Chi ", color = MoneyFlowTheme.colors.textSecondary, fontSize = 14.sp, fontFamily = FontCore)
                    Text(
                        "-" + formatter.format(monthExpense) + "₫",
                        color = MoneyFlowTheme.colors.surfaceBrand,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontMono
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardActionButtons(
    onAddExpense: () -> Unit = {},
    onAddIncome: () -> Unit = {},
    onScanBill: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onAddExpense,
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MoneyFlowTheme.colors.surfaceBrand),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("+ Chi", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontCore)
        }
        OutlinedButton(
            onClick = onAddIncome,
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MoneyFlowTheme.colors.surfaceBrand),
            border = BorderStroke(1.dp, MoneyFlowTheme.colors.surfaceBrand.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("+ Thu", fontWeight = FontWeight.Bold, fontFamily = FontCore)
        }
        OutlinedButton(
            onClick = onScanBill,
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MoneyFlowTheme.colors.textSecondary),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Quét bill", color = MoneyFlowTheme.colors.textPrimary, fontWeight = FontWeight.Bold, fontFamily = FontCore)
        }
    }
}

@Composable
fun DashboardTransactions(
    transactions: List<TransactionDto>,
    onSeeAll: () -> Unit = {},
    onOpenTransaction: (String) -> Unit = {},
    onClassify: (String) -> Unit = {}
) {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN")).apply { maximumFractionDigits = 0 }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Giao dịch gần đây", style = MoneyFlowTheme.typography.title3, color = MoneyFlowTheme.colors.textPrimary, fontWeight = FontWeight.Bold, fontFamily = FontCore)
            Text(
                "Tất cả >",
                style = MoneyFlowTheme.typography.body,
                color = MoneyFlowTheme.colors.surfaceBrand,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontCore,
                modifier = Modifier.clickable(onClick = onSeeAll)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có giao dịch nào trong tháng này.\nBấm + Chi hoặc + Thu để bắt đầu!",
                        style = MoneyFlowTheme.typography.body,
                        color = MoneyFlowTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column {
                    transactions.forEachIndexed { index, txn ->
                        val isIncome = txn.type == "INCOME"
                        val amountText = (if (isIncome) "+" else "-") + formatter.format(txn.amount) + " ₫"
                        val amountColor = if (isIncome) Color(0xFF4CAF50) else MoneyFlowTheme.colors.textPrimary
                        val icon = getCategoryIcon(txn.categoryName)
                        val title = txn.note?.ifBlank { null } ?: txn.categoryName ?: "Giao dịch"
                        val subtitle = (txn.categoryName ?: "Chưa phân loại") +
                                (txn.walletName?.let { " · $it" } ?: "") +
                                " · ${txn.occurredDate}"

                        TransactionItem(
                            icon = icon,
                            iconBg = if (isIncome) Color(0xFFE8F5E9) else Color(0xFFE3EDFF),
                            iconTint = if (isIncome) Color(0xFF4CAF50) else MoneyFlowTheme.colors.surfaceBrand,
                            title = title,
                            subtitle = subtitle,
                            amount = amountText,
                            amountColor = amountColor,
                            showClassify = txn.categoryId == null,
                            onClick = { onOpenTransaction(title) },
                            onClassify = { onClassify(title) }
                        )

                        if (index < transactions.lastIndex) {
                            HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    amount: String,
    amountColor: Color,
    showClassify: Boolean,
    onClick: () -> Unit = {},
    onClassify: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MoneyFlowTheme.typography.body, color = MoneyFlowTheme.colors.textPrimary, fontWeight = FontWeight.Bold, fontFamily = FontCore)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MoneyFlowTheme.typography.caption, color = MoneyFlowTheme.colors.textSecondary, fontFamily = FontCore)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(amount, style = MoneyFlowTheme.typography.body, color = amountColor, fontWeight = FontWeight.Bold, fontFamily = FontMono)
            if (showClassify) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE3EDFF), RoundedCornerShape(12.dp))
                        .clickable(onClick = onClassify)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Phân loại >", color = MoneyFlowTheme.colors.surfaceBrand, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontCore)
                }
            }
        }
    }
}

fun getCategoryIcon(name: String?): ImageVector {
    return when (name?.lowercase()?.trim()) {
        "ăn uống", "cà phê", "đi ăn ngoài" -> Icons.Outlined.Storefront
        "di chuyển", "grab" -> Icons.Outlined.LocalShipping
        "mua sắm" -> Icons.Outlined.ShoppingCart
        "giải trí" -> Icons.Outlined.LocalFireDepartment
        "sức khoẻ" -> Icons.Outlined.FavoriteBorder
        "hoá đơn & nhà ở", "hoá đơn" -> Icons.AutoMirrored.Outlined.ReceiptLong
        "điện thoại & internet" -> Icons.Outlined.PhoneAndroid
        "quà tặng" -> Icons.Outlined.CardGiftcard
        "thú cưng" -> Icons.Outlined.Pets
        "giáo dục" -> Icons.Outlined.School
        "lương" -> Icons.Outlined.Payments
        "thưởng" -> Icons.Outlined.CardGiftcard
        "freelance" -> Icons.Outlined.Storefront
        "đầu tư" -> Icons.Outlined.TrendingUp
        "được tặng" -> Icons.Outlined.Redeem
        "cho thuê" -> Icons.Outlined.Home
        else -> Icons.Outlined.Category
    }
}
