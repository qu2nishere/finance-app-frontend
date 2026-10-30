package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

/**
 * 8d — Chi tiết một giao dịch. Mở khi bấm một dòng trong sổ giao dịch (8c).
 */
@Composable
fun TransactionDetailScreen(
    title: String = "Highlands Coffee",
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
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
                title = "Chi tiết giao dịch",
                onBack = onBack,
                actionIcon = Icons.Outlined.Edit,
                onAction = onEdit
            )
        }

        // Thẻ số tiền
        item {
            MfCard(borderColor = Heat100) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MfIconTile(
                        icon = Icons.Outlined.Storefront,
                        tint = Heat600,
                        background = Heat50,
                        size = 52.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "−72.000₫",
                        fontFamily = FontMono,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 34.sp,
                        color = Heat600
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(shape = RoundedCornerShape(999.dp), color = Heat50) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NorthEast,
                                contentDescription = null,
                                tint = Heat600,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Tiền ra · Ăn uống",
                                fontFamily = FontCore,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Heat600
                            )
                        }
                    }
                }
            }
        }

        // Thông tin
        item {
            MfCard {
                MfRow(
                    title = "Thời gian",
                    icon = Icons.Outlined.CalendarMonth,
                    trailing = { DetailValue("Hôm nay · 22/08 · 09:12") }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Nguồn tiền",
                    icon = Icons.Outlined.AccountBalanceWallet,
                    trailing = { DetailValue("Ví Momo") }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Danh mục",
                    icon = Icons.Outlined.GridView,
                    showChevron = true,
                    onClick = { },
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .background(CatFood, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            DetailValue("Ăn uống · Cà phê")
                        }
                    }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Ghi chú",
                    icon = Icons.Outlined.Edit,
                    subtitle = "Cà phê sáng với team dự án"
                )
            }
        }

        // Ảnh hoá đơn
        item {
            MfCard {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Ảnh hoá đơn",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Ink50,
                        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = null,
                                tint = MoneyFlowTheme.colors.textTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "bill-0822.jpg",
                                fontFamily = FontCore,
                                fontSize = 12.sp,
                                color = MoneyFlowTheme.colors.textTertiary
                            )
                        }
                    }
                }
            }
        }

        // Ảnh hưởng hạn mức
        item {
            MfCard {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Ảnh hưởng hạn mức “Ăn uống”",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // Phần đã chi trước đó (92%) + phần của giao dịch này (2%)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .background(Ink100, RoundedCornerShape(99.dp))
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(0.92f)
                                    .fillMaxHeight()
                                    .background(Heat500, RoundedCornerShape(topStart = 99.dp, bottomStart = 99.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(0.02f)
                                    .fillMaxHeight()
                                    .background(Heat200)
                            )
                            Spacer(modifier = Modifier.weight(0.06f))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "3.302.000₫ / 3.500.000₫ · giao dịch này chiếm 2%",
                        fontFamily = FontMono,
                        fontSize = 11.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }
        }

        // Hành động
        item {
            MfCard {
                MfRow(
                    title = "Nhân bản giao dịch",
                    icon = Icons.Outlined.ContentCopy,
                    showChevron = true,
                    onClick = { }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Đặt thành giao dịch định kỳ",
                    icon = Icons.Outlined.Sync,
                    showChevron = true,
                    onClick = { }
                )
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                MfRow(
                    title = "Xoá giao dịch",
                    icon = Icons.Outlined.DeleteOutline,
                    iconTint = Alert500,
                    iconBackground = Alert50,
                    titleColor = Alert500,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun DetailValue(text: String) {
    Text(
        text = text,
        fontFamily = FontCore,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = MoneyFlowTheme.colors.textPrimary
    )
}
