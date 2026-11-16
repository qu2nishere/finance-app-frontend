package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
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

private data class OnboardCategory(val name: String, val icon: ImageVector)

private val onboardExpense = listOf(
    OnboardCategory("Ăn uống", Icons.Outlined.Storefront),
    OnboardCategory("Di chuyển", Icons.Outlined.LocalShipping),
    OnboardCategory("Mua sắm", Icons.Outlined.ShoppingBag),
    OnboardCategory("Hoá đơn", Icons.AutoMirrored.Outlined.ReceiptLong),
    OnboardCategory("Nhà ở", Icons.Outlined.Home),
    OnboardCategory("Sức khoẻ", Icons.Outlined.FavoriteBorder),
    OnboardCategory("Giải trí", Icons.Outlined.LocalFireDepartment),
    OnboardCategory("Học tập", Icons.Outlined.Category)
)

private val onboardIncome = listOf(
    OnboardCategory("Lương", Icons.Outlined.Payments),
    OnboardCategory("Thưởng", Icons.Outlined.CardGiftcard),
    OnboardCategory("Freelance", Icons.Outlined.Edit),
    OnboardCategory("Đầu tư", Icons.Outlined.BarChart)
)

/**
 * 10b — Đăng ký bước 2/3: chọn danh mục thường dùng.
 */
@Composable
fun RegisterStep2Screen(
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
    onSkip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val picked = remember {
        mutableStateListOf("Ăn uống", "Di chuyển", "Mua sắm", "Hoá đơn", "Lương")
    }

    fun toggle(name: String) {
        if (name in picked) picked.remove(name) else picked.add(name)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 28.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = MoneyFlowTheme.colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Tạo tài khoản",
                modifier = Modifier.weight(1f),
                fontFamily = FontCore,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Text(
                text = "Bỏ qua",
                fontFamily = FontCore,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.clickable(onClick = onSkip)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        RegisterProgress(step = 2)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bước 2/3 · Danh mục thường dùng",
            fontFamily = FontCore,
            fontSize = 11.sp,
            color = MoneyFlowTheme.colors.textTertiary
        )

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Bạn hay chi vào đâu?",
            fontFamily = FontCore,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MoneyFlowTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Chọn ít nhất 3 danh mục. Thêm hoặc bớt sau trong Cài đặt.",
            fontFamily = FontCore,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = MoneyFlowTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))
        MfSectionLabel("CHI TIÊU")
        Spacer(modifier = Modifier.height(10.dp))
        CategoryGrid(onboardExpense, picked) { toggle(it) }

        Spacer(modifier = Modifier.height(18.dp))
        MfSectionLabel("THU NHẬP")
        Spacer(modifier = Modifier.height(10.dp))
        CategoryGrid(onboardIncome, picked) { toggle(it) }

        Spacer(modifier = Modifier.height(16.dp))
        MfInfoBanner(
            text = "Đã chọn ${picked.size} danh mục · tạo danh mục riêng ở bước sau.",
            icon = Icons.Outlined.Info,
            background = Ink50,
            contentColor = MoneyFlowTheme.colors.textSecondary,
            borderColor = MoneyFlowTheme.colors.borderDefault
        )

        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onBack),
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
            ) {
                Text(
                    text = "Quay lại",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = FontCore,
                    fontSize = 15.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }
            Surface(
                modifier = Modifier
                    .weight(2f)
                    .clickable(enabled = picked.size >= 3, onClick = onContinue),
                shape = RoundedCornerShape(12.dp),
                color = if (picked.size >= 3) Blue500 else Ink300
            ) {
                Text(
                    text = "Tiếp tục",
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

/** Thanh 3 bước dùng chung cho 9b · 10b · 10c. */
@Composable
internal fun RegisterProgress(step: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .background(
                        if (index < step) Blue500 else Ink200,
                        RoundedCornerShape(99.dp)
                    )
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    items: List<OnboardCategory>,
    picked: List<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cat ->
                    CategoryTile(
                        category = cat,
                        selected = cat.name in picked,
                        modifier = Modifier.weight(1f),
                        onClick = { onToggle(cat.name) }
                    )
                }
                // giữ lưới 4 cột khi hàng cuối thiếu ô
                repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun CategoryTile(
    category: OnboardCategory,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            color = if (selected) Blue50 else Color.White,
            border = BorderStroke(
                if (selected) 1.5.dp else 1.dp,
                if (selected) Blue500 else MoneyFlowTheme.colors.borderSubtle
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = if (selected) Blue600 else MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = category.name,
                    fontFamily = FontCore,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) Blue600 else MoneyFlowTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 5.dp, y = (-5).dp)
                    .size(18.dp)
                    .background(Blue500, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}
