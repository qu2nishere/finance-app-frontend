package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import com.quan.finance_app.ui.theme.FontMono
import com.quan.finance_app.ui.theme.FontCore

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)), // Nền màu xám xanh rất nhạt giống wireframe
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Header
        item {
            DashboardHeader()
        }
        
        // 2. Balance Card (Custom)
        item {
            DashboardBalanceCard()
        }

        // 3. Action Buttons
        item {
            DashboardActionButtons()
        }

        // 4. Recent Transactions
        item {
            DashboardTransactions()
        }
    }
}

@Composable
fun DashboardHeader() {
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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 0.45f }, 
                        modifier = Modifier.size(36.dp), 
                        color = MoneyFlowTheme.colors.surfaceBrand, 
                        strokeWidth = 4.dp, 
                        trackColor = Color(0xFFE0E0E0)
                    )
                    Text("45%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MoneyFlowTheme.colors.textPrimary, fontFamily = FontMono)
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
                onClick = {},
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
fun DashboardBalanceCard() {
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
                Text("12.480.000", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = MoneyFlowTheme.colors.textPrimary, letterSpacing = (-1).sp, fontFamily = FontMono)
                Text(" ₫", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MoneyFlowTheme.colors.textPrimary, modifier = Modifier.padding(bottom = 6.dp), fontFamily = FontCore)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("▲", color = Color(0xFF4CAF50), fontSize = 10.sp, fontFamily = FontCore)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("8,4%", color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontMono)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("+970.000₫", color = MoneyFlowTheme.colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontMono)
                Text(" so với tháng trước", color = MoneyFlowTheme.colors.textSecondary, fontSize = 12.sp, fontFamily = FontCore)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Mini Bar Chart
            Row(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(0.4f, 0.6f, 0.2f, 0.5f, 0.8f, 1.0f, 0.4f)
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
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bottom row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Text("Thu ", color = MoneyFlowTheme.colors.textSecondary, fontSize = 14.sp, fontFamily = FontCore)
                    Text("+18,2tr", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontMono)
                }
                Row {
                    Text("Chi ", color = MoneyFlowTheme.colors.textSecondary, fontSize = 14.sp, fontFamily = FontCore)
                    Text("-9,7tr", color = MoneyFlowTheme.colors.surfaceBrand, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontMono)
                }
            }
        }
    }
}

@Composable
fun DashboardActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {},
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MoneyFlowTheme.colors.surfaceBrand),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("+ Chi", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontCore)
        }
        OutlinedButton(
            onClick = {},
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MoneyFlowTheme.colors.surfaceBrand),
            border = BorderStroke(1.dp, MoneyFlowTheme.colors.surfaceBrand.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("+ Thu", fontWeight = FontWeight.Bold, fontFamily = FontCore)
        }
        OutlinedButton(
            onClick = {},
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
fun DashboardTransactions() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Giao dịch gần đây", style = MoneyFlowTheme.typography.title3, color = MoneyFlowTheme.colors.textPrimary, fontWeight = FontWeight.Bold, fontFamily = FontCore)
            Text("Tất cả >", style = MoneyFlowTheme.typography.body, color = MoneyFlowTheme.colors.surfaceBrand, fontWeight = FontWeight.SemiBold, fontFamily = FontCore)
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column {
                TransactionItem(
                    icon = Icons.Outlined.Home,
                    iconBg = Color(0xFFE3EDFF),
                    iconTint = MoneyFlowTheme.colors.surfaceBrand,
                    title = "Highlands Coffee",
                    subtitle = "Hôm nay - 09:12",
                    amount = "-72.000\u20AB",
                    amountColor = MoneyFlowTheme.colors.textPrimary,
                    showClassify = true
                )
                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 24.dp))
                TransactionItem(
                    icon = Icons.Outlined.LocalShipping,
                    iconBg = Color(0xFFF5F5F5),
                    iconTint = MoneyFlowTheme.colors.textSecondary,
                    title = "Grab - đi làm",
                    subtitle = "Hôm nay - 08:05 - Di chuyển",
                    amount = "-45.000\u20AB",
                    amountColor = MoneyFlowTheme.colors.textPrimary,
                    showClassify = false
                )
                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 24.dp))
                TransactionItem(
                    icon = Icons.Outlined.AttachMoney,
                    iconBg = Color(0xFFE8F5E9),
                    iconTint = Color(0xFF4CAF50),
                    title = "Lương tháng 8",
                    subtitle = "20/08 - Thu nhập",
                    amount = "+18.000.000\u20AB",
                    amountColor = Color(0xFF4CAF50),
                    showClassify = false
                )
                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 24.dp))
                TransactionItem(
                    icon = Icons.Outlined.ShoppingCart,
                    iconBg = Color(0xFFE3EDFF),
                    iconTint = MoneyFlowTheme.colors.surfaceBrand,
                    title = "VinMart",
                    subtitle = "19/08 - 19:44",
                    amount = "-318.000\u20AB",
                    amountColor = MoneyFlowTheme.colors.textPrimary,
                    showClassify = true
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    amount: String,
    amountColor: Color,
    showClassify: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Phân loại >", color = MoneyFlowTheme.colors.surfaceBrand, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontCore)
                }
            }
        }
    }
}
