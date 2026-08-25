package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.*

@Composable
fun SavingsGoalsScreen(
    onBack: () -> Unit = {},
    onNavigateToEditGoal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Top Header with Back button and "+ Thêm"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MoneyFlowTheme.colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mục tiêu tiết kiệm",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontCore,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Surface(
                    modifier = Modifier.clickable { onNavigateToEditGoal() },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEEF4FF)
                ) {
                    Text(
                        text = "+ Thêm",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Blue600,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 2. Overview Card (Tổng đã tích luỹ)
        item {
            TotalAccumulatedCard()
        }

        // 3. Section "Đang chạy (3)"
        item {
            Text(
                text = "Đang chạy (3)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontCore,
                color = MoneyFlowTheme.colors.textPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 4. Active Goals List
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Goal 1: Du lịch Đà Lạt (45%)
                ActiveGoalCard(
                    icon = Icons.Outlined.CardGiftcard,
                    iconBg = Color(0xFFE8F8F0),
                    iconTint = Grow500,
                    title = "Du lịch Đà Lạt",
                    spentAmount = "4,5tr",
                    targetAmount = "10,0tr",
                    subInfo = "Cần 1,4tr/tháng · hạn 12/2026",
                    progress = 0.45f,
                    progressColor = Grow500,
                    onClick = onNavigateToEditGoal
                )

                // Goal 2: Quỹ dự phòng (22%)
                ActiveGoalCard(
                    icon = Icons.Outlined.Shield,
                    iconBg = Color(0xFFE8F8F0),
                    iconTint = Grow500,
                    title = "Quỹ dự phòng",
                    spentAmount = "11,0tr",
                    targetAmount = "50,0tr",
                    subInfo = "Tự trích 3,6tr/tháng · hạn 06/2027",
                    progress = 0.22f,
                    progressColor = Grow500,
                    onClick = onNavigateToEditGoal
                )

                // Goal 3: Đổi điện thoại (25% - Chậm tiến độ)
                ActiveGoalCard(
                    icon = Icons.Outlined.PhoneIphone,
                    iconBg = Color(0xFFFFF8E6),
                    iconTint = Sun500,
                    title = "Đổi điện thoại",
                    spentAmount = "3,0tr",
                    targetAmount = "12,0tr",
                    subInfo = "Chậm 1 tháng so với kế hoạch",
                    subInfoColor = Heat500,
                    progress = 0.25f,
                    progressColor = Sun500,
                    onClick = onNavigateToEditGoal
                )
            }
        }

        // 5. Section "Đã hoàn thành (1)"
        item {
            Text(
                text = "Đã hoàn thành (1)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontCore,
                color = MoneyFlowTheme.colors.textPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 6. Completed Goal Card
        item {
            CompletedGoalCard(
                title = "Laptop mới",
                info = "Hoàn thành 07/2026 · 22,0tr"
            )
        }

        // 7. Insight Banner
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF8E6),
                border = BorderStroke(1.dp, Color(0xFFFFEFC2))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Gợi ý thông minh",
                        tint = Color(0xFFC48304),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color(0xFF4A3B18), fontFamily = FontCore)) {
                                append("Còn dư ")
                            }
                            withStyle(SpanStyle(color = Color(0xFF2C220E), fontWeight = FontWeight.Bold, fontFamily = FontCore)) {
                                append("2,3tr")
                            }
                            withStyle(SpanStyle(color = Color(0xFF4A3B18), fontFamily = FontCore)) {
                                append(" tháng này — nạp thêm vào ")
                            }
                            withStyle(SpanStyle(color = Color(0xFF2C220E), fontWeight = FontWeight.Bold, fontFamily = FontCore)) {
                                append("“Du lịch Đà Lạt”")
                            }
                            withStyle(SpanStyle(color = Color(0xFF4A3B18), fontFamily = FontCore)) {
                                append(" để về đích sớm 1 tháng?")
                            }
                        },
                        style = MoneyFlowTheme.typography.bodySm.copy(fontSize = 13.sp, lineHeight = 18.sp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-components
// -------------------------------------------------------------------------

@Composable
fun TotalAccumulatedCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Progress Indicator 31%
                Box(
                    modifier = Modifier.size(76.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 0.31f },
                        modifier = Modifier.fillMaxSize(),
                        color = Grow500,
                        trackColor = Color(0xFFE8F8F0),
                        strokeWidth = 7.dp
                    )
                    Text(
                        text = "31%",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column {
                    Text(
                        text = "Tổng đã tích luỹ",
                        fontFamily = FontCore,
                        fontSize = 13.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "18.500.000",
                            fontFamily = FontMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Grow500,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "₫",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Grow500,
                            modifier = Modifier.padding(bottom = 2.dp, start = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "trên tổng mục tiêu 60,0tr",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub text: Tháng này đã nạp 3.600.000₫
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                        append("Tháng này đã nạp ")
                    }
                    withStyle(SpanStyle(color = Grow500, fontFamily = FontMono, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                        append("3.600.000₫")
                    }
                }
            )
        }
    }
}

@Composable
fun ActiveGoalCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    spentAmount: String,
    targetAmount: String,
    subInfo: String,
    subInfoColor: Color = MoneyFlowTheme.colors.textSecondary,
    progress: Float,
    progressColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Progress on left
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = progressColor,
                    trackColor = Color(0xFFF0F2F6),
                    strokeWidth = 5.dp
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontFamily = FontCore,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Center details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(iconBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$spentAmount / $targetAmount",
                        fontFamily = FontMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subInfo,
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    color = subInfoColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Plus Action Button
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFF0EC)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nạp thêm",
                        tint = Heat500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedGoalCard(
    title: String,
    info: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE8F8F0), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Đã hoàn thành",
                        tint = Grow500,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = info,
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Xem chi tiết",
                tint = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
