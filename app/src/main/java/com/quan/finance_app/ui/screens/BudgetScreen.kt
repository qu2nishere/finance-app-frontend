package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.WarningAmber
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
fun BudgetScreen(
    onNavigateToSavingsGoals: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Top Header: Title "Ngân sách" + "+ Thêm" Button
        item {
            BudgetHeader(
                onAddClick = { onNavigateToSavingsGoals() }
            )
        }

        // 2. Main Monthly Budget Overview Card
        item {
            TotalMonthlyBudgetCard()
        }

        // 3. Section "Theo danh mục"
        item {
            Text(
                text = "Theo danh mục",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontCore,
                color = MoneyFlowTheme.colors.textPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 4. Category Budget Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Card 1: Ăn uống (Vượt hạn mức - Alert Red)
                CategoryBudgetCard(
                    icon = Icons.Outlined.Storefront,
                    iconBg = Color(0xFFFFF0F2),
                    iconTint = Color(0xFFE11D48),
                    title = "Ăn uống",
                    spentAmount = "3,3tr",
                    limitAmount = "3,0tr",
                    progress = 1.0f,
                    progressColor = Color(0xFFE11D48),
                    borderColor = Color(0xFFFFB4C0),
                    isOverBudget = true,
                    overAmount = "302.000₫"
                )

                // Card 2: Di chuyển (Bình thường - Orange)
                CategoryBudgetCard(
                    icon = Icons.Outlined.DirectionsCar,
                    iconBg = Color(0xFFEEF4FF),
                    iconTint = Blue500,
                    title = "Di chuyển",
                    spentAmount = "2,0tr",
                    limitAmount = "2,5tr",
                    progress = 0.80f,
                    progressColor = Heat500,
                    borderColor = Color(0xFFF0F3F8),
                    isOverBudget = false
                )

                // Card 3: Mua sắm (Bình thường - Orange)
                CategoryBudgetCard(
                    icon = Icons.Outlined.ShoppingBag,
                    iconBg = Color(0xFFF2F0FF),
                    iconTint = Color(0xFF7A5CF0),
                    title = "Mua sắm",
                    spentAmount = "1,5tr",
                    limitAmount = "2,0tr",
                    progress = 0.75f,
                    progressColor = Heat500,
                    borderColor = Color(0xFFF0F3F8),
                    isOverBudget = false
                )

                // Button: Đặt ngân sách cho danh mục khác
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable { /* Add new category budget */ },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF4F7FF),
                    border = BorderStroke(1.dp, Color(0xFFD6E4FF))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "+ Đặt ngân sách cho danh mục khác",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Blue600
                        )
                    }
                }
            }
        }

        // 5. Section "Mục tiêu tiết kiệm" with navigation button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mục tiêu tiết kiệm",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontCore,
                    color = MoneyFlowTheme.colors.textPrimary
                )

                Text(
                    text = "Tất cả >",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontCore,
                    color = Blue600,
                    modifier = Modifier.clickable { onNavigateToSavingsGoals() }
                )
            }
        }

        // 6. Savings Goals Cards (Clickable to open Savings Goals)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Goal 1: Du lịch Đà Lạt
                SavingsGoalCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSavingsGoals() },
                    icon = Icons.Outlined.CardGiftcard,
                    iconBg = Color(0xFFE8F8F0),
                    iconTint = Grow500,
                    title = "Du lịch Đà Lạt",
                    progress = 0.45f,
                    progressColor = Grow500,
                    infoText = "4,5/10tr · T12"
                )

                // Goal 2: Quỹ dự phòng
                SavingsGoalCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSavingsGoals() },
                    icon = Icons.Outlined.Shield,
                    iconBg = Color(0xFFF0F4F8),
                    iconTint = Ink600,
                    title = "Quỹ dự phòng",
                    progress = 0.22f,
                    progressColor = Ink600,
                    infoText = "11/50tr"
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// Sub-components
// -------------------------------------------------------------------------

@Composable
fun BudgetHeader(onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ngân sách",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontCore,
            color = MoneyFlowTheme.colors.textPrimary
        )

        Surface(
            modifier = Modifier
                .clickable(onClick = onAddClick),
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

@Composable
fun TotalMonthlyBudgetCard() {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ngân sách tháng 8",
                    fontFamily = FontCore,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                Text(
                    text = "còn 12 ngày",
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "9,7tr",
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Heat500,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = " / 12,0tr",
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color(0xFFF0F2F6), RoundedCornerShape(5.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.81f)
                        .fillMaxHeight()
                        .background(Heat500, RoundedCornerShape(5.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                        append("Đã dùng 81% · còn ")
                    }
                    withStyle(
                        SpanStyle(
                            color = Grow500,
                            fontFamily = FontMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    ) {
                        append("2.288.000₫")
                    }
                    withStyle(SpanStyle(color = Ink600, fontFamily = FontCore, fontSize = 13.sp)) {
                        append(" (~190k/ngày)")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryBudgetCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    spentAmount: String,
    limitAmount: String,
    progress: Float,
    progressColor: Color,
    borderColor: Color,
    isOverBudget: Boolean,
    overAmount: String = ""
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(if (isOverBudget) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(iconBg, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontFamily = FontCore,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    )
                }

                Text(
                    text = "$spentAmount / $limitAmount",
                    fontFamily = FontMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFF0F2F6), RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(progressColor, RoundedCornerShape(4.dp))
                )
            }

            if (isOverBudget) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = "Cảnh báo vượt hạn mức",
                        tint = Color(0xFFE11D48),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Vượt $overAmount",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color(0xFFE11D48)
                    )
                }
            }
        }
    }
}

@Composable
fun SavingsGoalCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    progress: Float,
    progressColor: Color,
    infoText: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF0F3F8))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontFamily = FontCore,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color(0xFFF0F2F6), RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(progressColor, RoundedCornerShape(3.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = infoText,
                fontFamily = FontMono,
                fontSize = 12.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }
    }
}
