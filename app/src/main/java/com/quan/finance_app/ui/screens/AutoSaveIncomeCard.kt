package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.*

/**
 * 4b — khối "Tự trích vào mục tiêu" ở màn ghi khoản thu.
 *
 * Bản turn 10 của wireframe đổi từ một dòng công tắc đơn thành thẻ 3 dòng:
 * bật/tắt · số tiền sẽ trích cho chính khoản thu đang nhập (kèm "Đổi %") ·
 * lối vào màn cài đặt tự trích hằng tháng (8a).
 */
@Composable
fun AutoSaveIncomeCard(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    percent: Int,
    incomeAmount: Double,
    onChangePercent: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deducted = (incomeAmount * percent / 100).toLong()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Grow100)
    ) {
        Column {
            // Dòng 1: bật/tắt
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tự trích vào mục tiêu",
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$percent% thu nhập → Quỹ dự phòng",
                        fontFamily = FontCore,
                        fontSize = 11.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Grow500,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Ink200,
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            if (enabled) {
                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

                // Dòng 2: số tiền trích cho chính khoản thu này + "Đổi %"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Grow50)
                        .clickable(onClick = onChangePercent)
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color.White, RoundedCornerShape(7.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Percent,
                            contentDescription = null,
                            tint = Grow600,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (deducted > 0) "Trích ${formatVnd(deducted)} khi lưu khoản thu này"
                        else "Nhập số tiền để xem mức trích",
                        modifier = Modifier.weight(1f),
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Grow600
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đổi %",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Grow600
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Grow600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)

            // Dòng 3: mở màn cài đặt tự trích hằng tháng (8a)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenSettings)
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(Grow50, RoundedCornerShape(7.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = Grow600,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Cài đặt tự trích hằng tháng",
                    modifier = Modifier.weight(1f),
                    fontFamily = FontCore,
                    fontSize = 12.sp,
                    color = Grow600
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Grow600,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
