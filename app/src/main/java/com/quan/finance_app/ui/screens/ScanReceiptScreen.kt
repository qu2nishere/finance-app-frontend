package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 8g — Quét hoá đơn. Mở từ nút "Quét bill" ở trang chủ.
 * Nửa trên là khung camera nền tối, nửa dưới là sheet trắng với kết quả đọc được.
 */
@Composable
fun ScanReceiptScreen(
    onDismissRequest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var splitIntoItems by remember { mutableStateOf(false) }

    val lineItems = listOf(
        "Sữa tươi Vinamilk ×2" to "76.000₫",
        "Thịt ba rọi 500g" to "142.000₫",
        "Rau củ & gia vị" to "100.000₫"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage)
    ) {
        // Khung camera
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Ink900)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 26.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismissRequest, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Đóng",
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = "Quét hoá đơn",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontFamily = FontCore,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DocumentScanner,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Đưa hoá đơn vào khung",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.weight(1f).clickable { },
                    shape = RoundedCornerShape(999.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "Chọn từ thư viện",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = FontCore,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
                Surface(
                    modifier = Modifier.weight(1f).clickable { },
                    shape = RoundedCornerShape(999.dp),
                    color = Color.White
                ) {
                    Text(
                        text = "Chụp",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Ink900
                    )
                }
            }
        }

        // Sheet kết quả
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .offset(y = (-14).dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 44.dp, height = 4.dp)
                        .background(MoneyFlowTheme.colors.borderStrong, RoundedCornerShape(99.dp))
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Blue600,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "Đã đọc được từ hoá đơn",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Blue600
                    )
                }

                // Hoá đơn đọc được
                Spacer(modifier = Modifier.height(12.dp))
                MfCard(borderColor = Heat100) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MfIconTile(
                                icon = Icons.Outlined.ShoppingCart,
                                tint = Heat600,
                                background = Heat50
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "VinMart Nguyễn Trãi",
                                    fontFamily = FontCore,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MoneyFlowTheme.colors.textPrimary
                                )
                                Text(
                                    text = "22/08/2026 · 19:44",
                                    fontFamily = FontCore,
                                    fontSize = 11.sp,
                                    color = MoneyFlowTheme.colors.textSecondary
                                )
                            }
                            Text(
                                text = "−318.000₫",
                                fontFamily = FontMono,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = Heat600
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MoneyFlowTheme.colors.borderDefault)
                        Spacer(modifier = Modifier.height(10.dp))

                        lineItems.forEach { (name, price) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Text(
                                    text = name,
                                    modifier = Modifier.weight(1f),
                                    fontFamily = FontCore,
                                    fontSize = 12.sp,
                                    color = MoneyFlowTheme.colors.textSecondary
                                )
                                Text(
                                    text = price,
                                    fontFamily = FontMono,
                                    fontSize = 12.sp,
                                    color = MoneyFlowTheme.colors.textPrimary
                                )
                            }
                        }
                    }
                }

                // Danh mục / nguồn tiền / tách giao dịch
                Spacer(modifier = Modifier.height(12.dp))
                MfCard(borderColor = Blue200) {
                    MfRow(
                        title = "Danh mục · Mua sắm",
                        icon = Icons.Outlined.GridView,
                        subtitle = "Gợi ý từ tên cửa hàng",
                        showChevron = true,
                        onClick = { }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                MfCard {
                    MfRow(
                        title = "Nguồn tiền · Tiền mặt",
                        icon = Icons.Outlined.AccountBalanceWallet,
                        iconTint = MoneyFlowTheme.colors.textTertiary,
                        iconBackground = Ink100,
                        titleColor = MoneyFlowTheme.colors.textSecondary,
                        showChevron = true,
                        onClick = { }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                MfCard {
                    MfToggleRow(
                        title = "Tách thành ${lineItems.size} giao dịch theo món",
                        checked = splitIntoItems,
                        onCheckedChange = { splitIntoItems = it }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                MfFooterActions(
                    cancelText = "Sửa tay",
                    confirmText = "Lưu khoản chi",
                    confirmColor = Heat500,
                    onCancel = onDismissRequest,
                    onConfirm = onDismissRequest
                )
            }
        }
    }
}
